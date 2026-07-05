package com.github.manu585.fourelements.bukkit.commands;

import com.github.manu585.fourelements.api.bending.Element;
import com.github.manu585.fourelements.bukkit.commands.argument.ElementArgumentType;
import com.github.manu585.fourelements.bukkit.manager.BenderManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * {@code /elements add <element> [target]} grants an element to yourself or another player, online or offline.
 */
public class AddCommand extends FourElementsCommand {

  private final BenderManager benderManager;

  public AddCommand(BenderManager benderManager) {
    super("add");
    this.benderManager = benderManager;
  }

  @Override
  protected void configure(LiteralArgumentBuilder<CommandSourceStack> root) {
    root.then(argument("element", new ElementArgumentType())
            // /elements add <element>
            .executes(player((sender, context) -> grant(context, sender.getUniqueId(), sender.getName())))
            // /elements add <element> <target>
            .then(argument("target", StringArgumentType.word())
                    .suggests(this::suggestOnlinePlayers)
                    .executes(this::grantToTarget)));
  }

  private int grantToTarget(CommandContext<CommandSourceStack> context) {
    String name = StringArgumentType.getString(context, "target");

    Player online = Bukkit.getPlayerExact(name);
    if (online != null) {
      grant(context, online.getUniqueId(), online.getName());
      return Command.SINGLE_SUCCESS;
    }

    // Non-blocking lookup: only resolves players the server has already seen.
    OfflinePlayer offline = Bukkit.getOfflinePlayerIfCached(name);
    if (offline == null) {
      context.getSource().getSender().sendMessage(
              Component.text("Never seen a player named " + name + ".", NamedTextColor.RED));
      return 0;
    }

    String display = offline.getName() != null ? offline.getName() : name;
    grant(context, offline.getUniqueId(), display);
    return Command.SINGLE_SUCCESS;
  }

  private void grant(CommandContext<CommandSourceStack> context, UUID target, String targetName) {
    Element element = context.getArgument("element", Element.class);
    CommandSender sender = context.getSource().getSender();

    benderManager.editBender(target, benderPlayer -> benderPlayer.addElement(element))
            .whenComplete((added, throwable) -> {
              if (throwable != null) {
                sender.sendMessage(Component.text("Could not update " + targetName + "'s elements.", NamedTextColor.RED));
                return;
              }

              if (added) {
                sender.sendMessage(Component.text("Gave " + targetName + " the ", NamedTextColor.GREEN)
                        .append(Component.text(element.displayName(), element.textColor()))
                        .append(Component.text(" element.", NamedTextColor.GREEN)));
              } else {
                sender.sendMessage(Component.text(targetName + " already has that element.", NamedTextColor.YELLOW));
              }
            });
  }

  private CompletableFuture<Suggestions> suggestOnlinePlayers(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
    String input = builder.getRemainingLowerCase();
    for (Player player : Bukkit.getOnlinePlayers()) {
      if (player.getName().toLowerCase(Locale.ROOT).startsWith(input)) {
        builder.suggest(player.getName());
      }
    }
    return builder.buildFuture();
  }

}
