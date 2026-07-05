package com.github.manu585.fourelements.bukkit.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import java.util.function.Predicate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

/**
 * Base for every FourElements sub-command.
 *
 * <p>Subclasses describe only their Brigadier subtree in {@link #configure}, the base owns
 * the root literal, wires the permission requirement, and exposes helpers so the common
 * cases (player-only bodies, permission-gated nodes, arguments) stay terse. Because each
 * command hands back a real {@link LiteralCommandNode}, arbitrary argument trees and
 * sub-literals are possible instead of a single flat leaf.
 */
public abstract class FourElementsCommand {

  private final String name;

  /** Permission required to see and run the command, or {@code null} for an open command. */
  private final String permission;

  /**
   * Command with the conventional {@code fourelements.command.<name>} permission.
   *
   * @param name literal name of the command (e.g. {@code "who"})
   */
  protected FourElementsCommand(String name) {
    this(name, "fourelements.command." + name);
  }

  /**
   * @param name       literal name of the command
   * @param permission permission required to see/run it, or {@code null} for no gate
   */
  protected FourElementsCommand(String name, String permission) {
    this.name = name;
    this.permission = permission;
  }

  public final String name() {
    return name;
  }

  public final String permission() {
    return permission;
  }

  /**
   * Build this command's Brigadier subtree: a root literal named {@link #name()}, gated by
   * {@link #permission()} when present, and populated by {@link #configure}.
   *
   * @return the built command node, ready to be attached to a parent
   */
  public final LiteralCommandNode<CommandSourceStack> build() {
    LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(name);
    if (permission != null) {
      root.requires(requires(permission));
    }
    configure(root);
    return root.build();
  }

  /**
   * Describe the command tree: attach {@code executes(...)}, arguments and sub-literals to
   * {@code root}. The root's name and top-level permission are already applied.
   *
   * @param root the root literal builder for this command
   */
  protected abstract void configure(LiteralArgumentBuilder<CommandSourceStack> root);

  // --- Helpers ---------------------------------------------------------------

  /**
   * A permission predicate over the command source, for gating individual nodes.
   *
   * @param permission permission to check
   * @return predicate suitable for {@code requires(...)}
   */
  protected Predicate<CommandSourceStack> requires(String permission) {
    return source -> source.getSender().hasPermission(permission);
  }

  /**
   * A permission-gated child literal, for per-node (dynamic) permissions within a tree.
   *
   * @param name       literal name
   * @param permission permission required to use this branch
   * @return a literal builder already gated by {@code permission}
   */
  protected LiteralArgumentBuilder<CommandSourceStack> literal(String name, String permission) {
    return Commands.literal(name).requires(requires(permission));
  }

  /**
   * An argument node of the given type.
   *
   * @param name argument name used to look the value up in the context
   * @param type argument type (parsing + suggestions)
   * @param <T>  parsed argument value type
   * @return the argument builder
   */
  protected <T> RequiredArgumentBuilder<CommandSourceStack, T> argument(String name, ArgumentType<T> type) {
    return Commands.argument(name, type);
  }

  /**
   * Wrap a player-only action as a Brigadier command: rejects non-player sources with a
   * message, and reports {@link Command#SINGLE_SUCCESS} once the action runs.
   *
   * @param executor the action to run for a player source
   * @return a command usable in {@code executes(...)}
   */
  protected Command<CommandSourceStack> player(PlayerExecutor executor) {
    return context -> {
      if (!(context.getSource().getExecutor() instanceof Player player)) {
        context.getSource().getSender().sendMessage(
                Component.text("Only players can use this command.", NamedTextColor.RED));
        return 0;
      }
      executor.execute(player, context);
      return Command.SINGLE_SUCCESS;
    };
  }

  /** A command body that requires a player executor. */
  @FunctionalInterface
  protected interface PlayerExecutor {
    void execute(Player player, CommandContext<CommandSourceStack> context);
  }

}
