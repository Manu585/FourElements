package com.github.manu585.fourelements.bukkit.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public abstract class FourElementsCommand {

  public static final LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("elements");

}
