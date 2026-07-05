package com.github.manu585.fourelements.bukkit.commands;

import com.github.manu585.fourelements.api.bender.Bender;
import com.github.manu585.fourelements.api.bending.Element;
import com.github.manu585.fourelements.bukkit.manager.BenderManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

/**
 * {@code /elements who} tells a player which element they primarily bend.
 * Open to everyone (no permission).
 */
public class InfoCommand extends FourElementsCommand {

  private final BenderManager benderManager;

  public InfoCommand(BenderManager benderManager) {
    super("info", null);
    this.benderManager = benderManager;
  }

  @Override
  protected void configure(LiteralArgumentBuilder<CommandSourceStack> root) {
    root.executes(player(this::showElement));
  }

  private void showElement(Player player, CommandContext<CommandSourceStack> context) {
    Bender bender = benderManager.getBender(player.getUniqueId());

    if (bender == null || bender.elements().isEmpty()) {
      player.sendMessage(Component.text("You are not a bender yet.", NamedTextColor.GRAY));
      return;
    }

    Element element = bender.elements().getFirst();

    player.sendMessage(
            Component.text("You are an", NamedTextColor.YELLOW)
                    .appendSpace()
                    .append(Component.text(element.displayName(), element.textColor()))
                    .appendSpace()
                    .append(Component.text("Bender", NamedTextColor.YELLOW)));
  }

}
