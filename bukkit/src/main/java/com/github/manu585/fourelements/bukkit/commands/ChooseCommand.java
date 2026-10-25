package com.github.manu585.fourelements.bukkit.commands;

import com.github.manu585.fourelements.api.bending.Element;
import com.github.manu585.fourelements.bukkit.commands.argument.EnumArgumentType;
import com.github.manu585.fourelements.bukkit.manager.BenderManager;
import com.github.manu585.fourelements.bukkit.text.BendingText;
import com.github.manu585.fourelements.core.bender.OnlineBenderPlayer;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

/**
 * {@code /elements choose <element>} lets the Avatar switch which of the four classical
 * elements they are currently channeling. Open to everyone, non-Avatars are told no.
 */
public class ChooseCommand extends FourElementsCommand {

  private final BenderManager benderManager;

  public ChooseCommand(BenderManager benderManager) {
    super("choose", null);
    this.benderManager = benderManager;
  }

  @Override
  protected void configure(LiteralArgumentBuilder<CommandSourceStack> root) {
    root.then(argument("element", EnumArgumentType.element()).executes(player(this::choose)));
  }

  private void choose(Player player, CommandContext<CommandSourceStack> context) {
    Element element = context.getArgument("element", Element.class);
    OnlineBenderPlayer bender = benderManager.getBender(player.getUniqueId());

    if (!bender.chooseActiveElement(element)) {
      player.sendMessage(Component.text("Only the Avatar can choose an element.", NamedTextColor.RED));
      return;
    }

    // Persist right away so the choice survives a relog or crash, not just a clean quit
    benderManager.save(bender.benderPlayer()).exceptionally(throwable -> {
      player.sendMessage(Component.text("Could not save your choice; it may be lost when you leave.", NamedTextColor.RED));
      return null;
    });

    player.sendMessage(Component.text("You are now channeling ", NamedTextColor.GREEN)
        .append(BendingText.name(element))
        .append(Component.text(".", NamedTextColor.GREEN)));
  }

}
