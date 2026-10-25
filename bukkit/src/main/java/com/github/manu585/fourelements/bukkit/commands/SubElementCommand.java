package com.github.manu585.fourelements.bukkit.commands;

import com.github.manu585.fourelements.api.bending.SubElement;
import com.github.manu585.fourelements.bukkit.commands.argument.EnumArgumentType;
import com.github.manu585.fourelements.bukkit.manager.BenderManager;
import com.github.manu585.fourelements.bukkit.text.BendingText;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * {@code /elements sub add|remove <subelement> [target]} grants or takes away a Sub-Element
 * of yourself or another player, online or offline. Only Sub-Elements valid for the
 * target's main element can be granted.
 */
public class SubElementCommand extends TargetedCommand {

  private static final String ARGUMENT = "subelement";

  private final BenderManager benderManager;

  public SubElementCommand(BenderManager benderManager) {
    super("sub");
    this.benderManager = benderManager;
  }

  @Override
  protected void configure(LiteralArgumentBuilder<CommandSourceStack> root) {
    root.then(Commands.literal("add")
            .then(withOptionalTarget(argument(ARGUMENT, EnumArgumentType.subElement()), this::add)))
        .then(Commands.literal("remove")
            .then(withOptionalTarget(argument(ARGUMENT, EnumArgumentType.subElement()), this::remove)));
  }

  private void add(CommandContext<CommandSourceStack> context, Target target) {
    SubElement subElement = context.getArgument(ARGUMENT, SubElement.class);

    reply(context, target, benderManager.editBender(target.uuid(), bender -> bender.addSubElement(subElement)), result -> switch (result) {
      case ADDED -> Component.text("Gave " + target.name() + " the ", NamedTextColor.GREEN)
          .append(BendingText.name(subElement))
          .append(Component.text(" sub-element.", NamedTextColor.GREEN));
      case ALREADY_KNOWN -> Component.text(target.name() + " already has that sub-element.", NamedTextColor.YELLOW);
      case NO_ELEMENT -> Component.text(target.name() + " has no element yet. Use /elements set first.", NamedTextColor.RED);
      case INCOMPATIBLE -> Component.text(subElement.displayName() + " is not available to " + target.name() + "'s element.", NamedTextColor.RED);
    });
  }

  private void remove(CommandContext<CommandSourceStack> context, Target target) {
    SubElement subElement = context.getArgument(ARGUMENT, SubElement.class);

    reply(context, target, benderManager.editBender(target.uuid(), bender -> bender.removeSubElement(subElement)), removed -> removed
        ? Component.text("Removed the " + subElement.displayName() + " sub-element from " + target.name() + ".", NamedTextColor.GREEN)
        : Component.text(target.name() + " does not have that sub-element.", NamedTextColor.YELLOW));
  }

}
