package com.github.manu585.fourelements.bukkit.commands.argument;

import com.github.manu585.fourelements.api.bending.Element;
import com.github.manu585.fourelements.api.bending.SubElement;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import org.jspecify.annotations.NonNull;

/**
 * Brigadier argument type that parses one of a fixed set of enum constants
 * from its name (case-insensitive) and tab-completes only the allowed ones.
 *
 * <p>Backed by a native {@code word()} string, so it serializes correctly to the client
 * while the server converts it to the real constant. Use the factory methods to get the
 * common variants.
 *
 * @param <E> the enum being parsed
 */
public final class EnumArgumentType<E extends Enum<E>> implements CustomArgumentType.Converted<@NonNull E, String> {

  private final List<E> allowed;
  private final SimpleCommandExceptionType unknown;

  private EnumArgumentType(List<E> allowed, String label) {
    this.allowed = allowed;
    this.unknown = new SimpleCommandExceptionType(new LiteralMessage("Unknown " + label));
  }

  /**
   * Any of the four classical elements
   */
  public static EnumArgumentType<Element> element() {
    return new EnumArgumentType<>(List.of(Element.values()), "element");
  }

  /**
   * Any {@link SubElement}
   */
  public static EnumArgumentType<SubElement> subElement() {
    return new EnumArgumentType<>(List.of(SubElement.values()), "sub-element");
  }

  @Override
  public @NonNull E convert(@NonNull String nativeType) throws CommandSyntaxException {
    for (E candidate : allowed) {
      if (candidate.name().equalsIgnoreCase(nativeType)) {
        return candidate;
      }
    }
    throw unknown.create();
  }

  @Override
  public @NonNull ArgumentType<String> getNativeType() {
    return StringArgumentType.word();
  }

  @Override
  public <S> @NonNull CompletableFuture<Suggestions> listSuggestions(@NonNull CommandContext<S> context, SuggestionsBuilder builder) {
    String input = builder.getRemainingLowerCase();
    for (E candidate : allowed) {
      String token = candidate.name().toLowerCase(Locale.ROOT);
      if (token.startsWith(input)) {
        builder.suggest(token);
      }
    }
    return builder.buildFuture();
  }

}
