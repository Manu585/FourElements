package com.github.manu585.fourelements.bukkit;

public interface PluginBootstrap extends io.papermc.paper.plugin.bootstrap.PluginBootstrap {

  void onEnable();

  void onDisable();

  void registerListeners();

}
