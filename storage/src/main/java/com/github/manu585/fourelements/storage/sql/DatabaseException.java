package com.github.manu585.fourelements.storage.sql;

/**
 * A database operation failed. Unchecked, because it travels through futures.
 */
public final class DatabaseException extends RuntimeException {

  public DatabaseException(String message, Throwable cause) {
    super(message, cause);
  }

}
