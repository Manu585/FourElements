package com.github.manu585.fourelements.bukkit.repository.interfaces;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface SqlConsumer {

  void accept(Connection connection) throws SQLException;

}
