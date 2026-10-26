package com.github.manu585.fourelements.storage.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool.PoolInitializationException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The one door to the database: owns the connection pool and runs all work on its own
 * threads, never on the caller's.
 *
 * <p>Repositories describe what to do as an {@link SqlWork} and get a future back. A failure
 * always completes that future exceptionally with a {@link DatabaseException}, it is never
 * swallowed, so callers can rely on "completed normally" meaning "it was stored".
 *
 * <p>An instance only exists once the database has been reached, see {@link #connect}.
 */
public final class Database {

  private static final Logger LOGGER = Logger.getLogger(Database.class.getName());

  private final SqlDialect dialect;
  private final HikariDataSource dataSource;
  private final DatabaseExecutorPool executorPool;

  private Database(SqlDialect dialect, HikariDataSource dataSource, DatabaseExecutorPool executorPool) {
    this.dialect = dialect;
    this.dataSource = dataSource;
    this.executorPool = executorPool;
  }

  /**
   * Opens the connection pool, failing fast if the database cannot be reached.
   *
   * @throws SQLException if no connection could be established, its message is the root cause
   */
  public static Database connect(DatabaseSettings settings) throws SQLException {
    HikariDataSource dataSource;
    try {
      // Hikari opens a first connection right here and throws if that fails
      dataSource = new HikariDataSource(hikariConfig(settings));
    } catch (PoolInitializationException e) {
      throw new SQLException(rootMessage(e), e);
    }
    return new Database(settings.dialect(), dataSource, new DatabaseExecutorPool(settings.poolSize()));
  }

  /**
   * @return the SQL flavor of the connected database
   */
  public SqlDialect dialect() {
    return dialect;
  }

  /**
   * Borrow a connection, for work that already runs on a suitable thread (the schema
   * migration at startup). Close it to give it back.
   */
  public Connection getConnection() throws SQLException {
    return dataSource.getConnection();
  }

  /**
   * Run read or single-statement work on a database thread.
   *
   * @param description what the work does, used in error messages
   * @param work the work, given a pooled connection that is closed afterwards
   * @param <T> result type
   * @return a future with the result, or failing with a {@link DatabaseException}
   */
  public <T> CompletableFuture<T> query(String description, SqlWork<T> work) {
    return CompletableFuture.supplyAsync(() -> run(description, work, false), executor());
  }

  /**
   * Run work on a database thread as one transaction: it is committed if the work returns
   * and rolled back if it throws.
   *
   * @param description what the work does, used in error messages
   * @param work the work, given a pooled connection that is closed afterwards
   * @param <T> result type
   * @return a future with the result, or failing with a {@link DatabaseException}
   */
  public <T> CompletableFuture<T> transaction(String description, SqlWork<T> work) {
    return CompletableFuture.supplyAsync(() -> run(description, work, true), executor());
  }

  public void close() {
    executorPool.shutdown();
    dataSource.close();
  }

  private ExecutorService executor() {
    return executorPool.getExecutor();
  }

  private <T> T run(String description, SqlWork<T> work, boolean transactional) {
    try (Connection connection = getConnection()) {
      if (!transactional) {
        return work.apply(connection);
      }
      connection.setAutoCommit(false);
      try {
        T result = work.apply(connection);
        connection.commit();
        return result;
      } catch (SQLException | RuntimeException e) {
        rollback(connection, e);
        throw e;
      }
    } catch (SQLException e) {
      DatabaseException failure = new DatabaseException("Database operation failed: " + description, e);
      LOGGER.log(Level.SEVERE, failure.getMessage(), e);
      throw failure;
    }
  }

  private static void rollback(Connection connection, Exception cause) {
    try {
      connection.rollback();
    } catch (SQLException rollbackFailure) {
      cause.addSuppressed(rollbackFailure);
    }
  }

  private static HikariConfig hikariConfig(DatabaseSettings settings) {
    HikariConfig hikariConfig = new HikariConfig();
    // Named explicitly: the drivers are downloaded by Paper's library loader, which
    // DriverManager's own lookup does not see
    hikariConfig.setDriverClassName(settings.dialect().driverClass());
    hikariConfig.setJdbcUrl(settings.jdbcUrl());
    if (settings.dialect() != SqlDialect.SQLITE) {
      hikariConfig.setUsername(settings.username());
      hikariConfig.setPassword(settings.password());
    }
    hikariConfig.setMaximumPoolSize(settings.poolSize());
    hikariConfig.setConnectionTimeout(settings.connectionTimeoutMillis());
    hikariConfig.setPoolName("FourElements-HikariPool");

    if (settings.dialect() == SqlDialect.MYSQL) {
      hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
      hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
      hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
      hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");
    }
    return hikariConfig;
  }

  /**
   * The driver wraps the actual reason several layers deep in multi-line messages,
   * the innermost one is the part worth showing to an admin.
   */
  private static String rootMessage(Throwable throwable) {
    Throwable root = throwable;
    while (root.getCause() != null) {
      root = root.getCause();
    }
    String message = root.getMessage();
    return message == null ? root.getClass().getSimpleName() : message.lines().findFirst().orElse(message);
  }

}
