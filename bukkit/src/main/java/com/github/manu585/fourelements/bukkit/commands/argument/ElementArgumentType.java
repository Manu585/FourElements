package com.github.manu585.fourelements.bukkit.commands.argument;

import com.github.manu585.fourelements.api.bending.Element;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import org.jspecify.annotations.NonNull;

/**
 * Brigadier argument type that parses an {@link Element} from its enum name
 * (case-insensitive) and tab-completes the available elements.
 *
 * <p>Backed by a native {@code word()} string, so it serializes correctly to the client
 * while the server converts it to a real {@link Element}.
 */
public final class ElementArgumentType implements CustomArgumentType.Converted<Element, String> {

  private static final SimpleCommandExceptionType UNKNOWN_ELEMENT = new SimpleCommandExceptionType(new LiteralMessage("Unknown element"));

  @Override
  public @NonNull Element convert(String nativeType) throws CommandSyntaxException {
    try {
      return Element.valueOf(nativeType.toUpperCase(Locale.ROOT));
    } catch (IllegalArgumentException e) {
      throw UNKNOWN_ELEMENT.create();
    }
  }

  @Override
  public @NonNull ArgumentType<String> getNativeType() {
    return StringArgumentType.word();
  }

  @Override
  public <S> @NonNull CompletableFuture<Suggestions> listSuggestions(@NonNull CommandContext<S> context, SuggestionsBuilder builder) {
    String input = builder.getRemainingLowerCase();
    for (Element element : Element.values()) {
      String token = element.name().toLowerCase(Locale.ROOT);
      if (token.startsWith(input)) {
        builder.suggest(token);
      }
    }
    return builder.buildFuture();
  }

}
