package com.github.manu585.fourelements.storage.sql;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * A piece of database work that runs on a connection and may throw {@link SQLException}.
 *
 * @param <T> what the work returns
 */
@FunctionalInterface
public interface SqlWork<T> {

  T apply(Connection connection) throws SQLException;

}
