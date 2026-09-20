package com.github.manu585.fourelements.bukkit.bootstrap;

/**
 * An expected startup failure (database unreachable, migration failed, etc.)
 *
 * <p>The message is written for the server admin and logged as a single line, without
 * a stack trace. Actual bugs should keep surfacing as unchecked exceptions instead.
 */
public final class StartupException extends Exception {

  public StartupException(String message, Throwable cause) {
    super(message, cause);
  }

}
