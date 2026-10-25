package com.github.manu585.fourelements.api.bender;

import com.github.manu585.fourelements.api.bending.Element;
import com.github.manu585.fourelements.api.bending.SubElement;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface Bender {

  /**
   * The bender's UUID
   *
   * @return the bender's UUID
   */
  UUID uuid();

  /**
   * The one element this bender belongs to. For the Avatar this is their home element, the
   * one they were born into.
   *
   * @return the element, or empty if this player is not a bender
   */
  Optional<Element> element();

  /**
   * Check if this bender is the Avatar: someone who, on top of their home {@link #element()},
   * can possess every {@link SubElement} and channel any of the four elements.
   *
   * @return <code>true</code> for the Avatar, never for a non-bender
   */
  boolean isAvatar();

  /**
   * The element this bender is currently bending: their own element, or for the Avatar the
   * one they are channeling right now (their home element until they choose another).
   *
   * @return the active element, or empty if this player is not a bender
   */
  Optional<Element> activeElement();

  /**
   * Sub-Elements this bender possesses.
   *
   * @return unmodifiable set, empty for non-benders
   */
  Set<SubElement> subElements();

  default boolean isBender() {
    return element().isPresent();
  }

}
