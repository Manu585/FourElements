package com.github.manu585.fourelements.bukkit.commands;

import com.github.manu585.fourelements.api.bending.Element;
import com.github.manu585.fourelements.bukkit.manager.BenderManager;
import com.github.manu585.fourelements.bukkit.text.BendingText;
import com.github.manu585.fourelements.core.bender.OnlineBenderPlayer;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

/**
 * {@code /elements info} tells a player which element they bend, plus their Sub-Elements
 * and, for the Avatar, the element they are currently channeling.
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
    OnlineBenderPlayer bender = benderManager.getBender(player.getUniqueId());

    Element element = bender.element().orElse(null);
    if (element == null) {
      player.sendMessage(Component.text("You are not a bender yet.", NamedTextColor.GRAY));
      return;
    }

    player.sendMessage(Component.text("Element: ", NamedTextColor.YELLOW)
        .append(BendingText.name(element)));

    if (!bender.subElements().isEmpty()) {
      player.sendMessage(Component.text("Sub-elements: ", NamedTextColor.YELLOW)
          .append(Component.join(JoinConfiguration.commas(true), bender.subElements().stream()
              .map(BendingText::name)
              .toList())));
    }

    if (bender.isAvatar()) {
      player.sendMessage(Component.text("You are the Avatar.", NamedTextColor.LIGHT_PURPLE));
      bender.activeElement().ifPresent(active -> player.sendMessage(Component.text("Channeling: ", NamedTextColor.YELLOW)
          .append(BendingText.name(active))));
    }
  }

}
