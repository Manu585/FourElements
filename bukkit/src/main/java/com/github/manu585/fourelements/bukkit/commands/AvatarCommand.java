package com.github.manu585.fourelements.bukkit.commands;

import com.github.manu585.fourelements.bukkit.manager.BenderManager;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * {@code /elements avatar <true|false> [target]} makes yourself or another player, online
 * or offline, the Avatar or takes that status away. The Avatar keeps their element as home
 * element, so the target needs one first.
 */
public class AvatarCommand extends TargetedCommand {

  private static final String ARGUMENT = "avatar";

  private final BenderManager benderManager;

  public AvatarCommand(BenderManager benderManager) {
    super("avatar");
    this.benderManager = benderManager;
  }

  @Override
  protected void configure(LiteralArgumentBuilder<CommandSourceStack> root) {
    root.then(withOptionalTarget(argument(ARGUMENT, BoolArgumentType.bool()), this::setAvatar));
  }

  private void setAvatar(CommandContext<CommandSourceStack> context, Target target) {
    boolean avatar = BoolArgumentType.getBool(context, ARGUMENT);

    reply(context, target, benderManager.editBender(target.uuid(), bender -> bender.assignAvatar(avatar)), result -> switch (result) {
      case GRANTED -> Component.text(target.name() + " is now the Avatar.", NamedTextColor.DARK_PURPLE);
      case REVOKED -> Component.text(target.name() + " is no longer the Avatar.", NamedTextColor.DARK_PURPLE);
      case UNCHANGED -> Component.text(target.name() + (avatar ? " already is" : " already is not") + " the Avatar.", NamedTextColor.LIGHT_PURPLE);
      case NO_ELEMENT -> Component.text(target.name() + " has no element yet. Use /elements set first.", NamedTextColor.RED);
    });
  }

}
