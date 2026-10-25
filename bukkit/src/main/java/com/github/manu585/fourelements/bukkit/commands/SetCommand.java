package com.github.manu585.fourelements.bukkit.commands;

import com.github.manu585.fourelements.api.bending.Element;
import com.github.manu585.fourelements.bukkit.commands.argument.EnumArgumentType;
import com.github.manu585.fourelements.bukkit.manager.BenderManager;
import com.github.manu585.fourelements.bukkit.text.BendingText;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * {@code /elements set <element> [target]} sets the element of yourself or another player,
 * online or offline. A bender only ever has one, so it replaces the old one.
 */
public class SetCommand extends TargetedCommand {

  private final BenderManager benderManager;

  public SetCommand(BenderManager benderManager) {
    super("set");
    this.benderManager = benderManager;
  }

  @Override
  protected void configure(LiteralArgumentBuilder<CommandSourceStack> root) {
    root.then(withOptionalTarget(argument("element", EnumArgumentType.element()), this::setElement));
  }

  private void setElement(CommandContext<CommandSourceStack> context, Target target) {
    Element element = context.getArgument("element", Element.class);

    reply(context, target, benderManager.editBender(target.uuid(), bender -> bender.assignElement(element)), changed -> {
      if (!changed) {
        return Component.text(target.name() + " already is that element.", NamedTextColor.YELLOW);
      }
      return Component.text("Set " + target.name() + "'s element to ", NamedTextColor.GREEN)
          .append(BendingText.name(element))
          .append(Component.text(".", NamedTextColor.GREEN));
    });
  }

}
