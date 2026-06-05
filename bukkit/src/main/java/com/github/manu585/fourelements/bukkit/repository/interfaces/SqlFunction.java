package com.github.manu585.fourelements.bukkit.repository.interfaces;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface SqlFunction <T> {

  T apply(Connection connection) throws SQLException;

}
