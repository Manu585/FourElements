package com.github.manu585.fourelements.bukkit.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Base for commands that act on a player: executor by default, or an online or offline
 * player named in a trailing {@code [target]} argument.
 */
public abstract class TargetedCommand extends FourElementsCommand {

  private static final String TARGET_ARGUMENT = "target";

  protected TargetedCommand(String name) {
    super(name);
  }

  /**
   * The player a command acts on, resolved to a UUID plus a name to show in messages.
   */
  protected record Target(UUID uuid, String name) {}

  /**
   * The body of a targeted command.
   */
  @FunctionalInterface
  protected interface TargetAction {

    void run(CommandContext<CommandSourceStack> context, Target target);

  }

  /**
   * Make {@code node} runnable on its own (acting on the sender) and with an optional
   * trailing target player.
   *
   * @param node the last required argument of the command
   * @param action what to do once the target is known
   * @param <T> the argument's value type
   *
   * @return {@code node}, for chaining
   */
  protected <T> RequiredArgumentBuilder<CommandSourceStack, T> withOptionalTarget(RequiredArgumentBuilder<CommandSourceStack, T> node, TargetAction action) {
    return node
        .executes(player((sender, context) -> action.run(context, new Target(sender.getUniqueId(), sender.getName()))))
        .then(argument(TARGET_ARGUMENT, StringArgumentType.word())
            .suggests(ONLINE_PLAYERS)
            .executes(context -> runOnNamedTarget(context, action)));
  }

  /**
   * Tell the sender how an edit of the target ended, or that it failed.
   *
   * @param context the executing command
   * @param target who was edited
   * @param result the pending edit
   * @param message builds the success message from the edit's result
   * @param <R> the edit's result type
   */
  protected <R> void reply(CommandContext<CommandSourceStack> context, Target target, CompletableFuture<R> result, Function<R, Component> message) {
    CommandSender sender = context.getSource().getSender();
    result.whenComplete((value, throwable) -> sender.sendMessage(throwable != null
        ? Component.text("Could not update " + target.name() + ".", NamedTextColor.RED)
        : message.apply(value)));
  }

  private int runOnNamedTarget(CommandContext<CommandSourceStack> context, TargetAction action) {
    String name = StringArgumentType.getString(context, TARGET_ARGUMENT);

    Player online = Bukkit.getPlayerExact(name);
    if (online != null) {
      action.run(context, new Target(online.getUniqueId(), online.getName()));
      return Command.SINGLE_SUCCESS;
    }

    // Non-blocking lookup: only resolves players the server has already seen
    OfflinePlayer offline = Bukkit.getOfflinePlayerIfCached(name);
    if (offline == null) {
      context.getSource().getSender().sendMessage(Component.text("Never seen a player named " + name + ".", NamedTextColor.RED));
      return 0;
    }

    String display = offline.getName() != null ? offline.getName() : name;
    action.run(context, new Target(offline.getUniqueId(), display));
    return Command.SINGLE_SUCCESS;
  }

}
