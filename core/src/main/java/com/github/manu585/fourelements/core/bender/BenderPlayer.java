package com.github.manu585.fourelements.core.bender;

import com.github.manu585.fourelements.api.bender.Bender;
import com.github.manu585.fourelements.api.bending.Element;
import com.github.manu585.fourelements.api.bending.SubElement;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.jspecify.annotations.Nullable;

/**
 * Persistent bender aggregate: the data loaded from and saved back to the database.
 *
 * <p>This class owns the bending invariants, so no caller can create an inconsistent state:
 * <ul>
 *   <li>a bender has at most one {@link Element}</li>
 *   <li>the Avatar is a bender (an Avatar always has their home element)</li>
 *   <li>every {@link SubElement} they hold is one they may possess, i.e. it belongs to their element, or they are the Avatar, a non-bender holds none.</li>
 * </ul>
 */
public class BenderPlayer implements Bender {

  private final EnumSet<SubElement> subElements = EnumSet.noneOf(SubElement.class);

  private final UUID uuid;

  private @Nullable Element element;
  private @Nullable Element channeled; // The element the avatar is currently channeling
  private boolean avatar;

  /**
   * Create a bender without any element.
   *
   * @param uuid UUID of the player
   */
  public BenderPlayer(UUID uuid) {
    this(uuid, null, false, null, Set.of());
  }

  /**
   * Create a bender with the given state.
   *
   * @param uuid UUID of the player
   * @param element the bender's element, or {@code null} for a non-bender
   * @param avatar whether the bender is the Avatar
   * @param channeled the element the Avatar is channeling, ignored for anyone but the Avatar, and defaults to the home element when {@code null}
   * @param subElements Sub-Elements to start with
   *
   * @throws IllegalArgumentException if the Avatar has no element or a Sub-Element is not valid for the bender
   */
  public BenderPlayer(UUID uuid, @Nullable Element element, boolean avatar, @Nullable Element channeled, Collection<SubElement> subElements) {
    if (avatar && element == null) {
      throw new IllegalArgumentException("The Avatar needs an element");
    }

    this.uuid = uuid;
    this.element = element;
    this.avatar = avatar;
    this.channeled = avatar ? (channeled != null ? channeled : element) : null;

    subElements.forEach(subElement -> {
      SubElementResult result = addSubElement(subElement);
      if (result == SubElementResult.NO_ELEMENT || result == SubElementResult.INCOMPATIBLE) {
        throw new IllegalArgumentException("Sub-Element " + subElement + " is not valid for element " + element);
      }
    });
  }

  @Override
  public UUID uuid() {
    return this.uuid;
  }

  @Override
  public Optional<Element> element() {
    return Optional.ofNullable(element);
  }

  @Override
  public boolean isAvatar() {
    return avatar;
  }

  @Override
  public Optional<Element> activeElement() {
    return Optional.ofNullable(avatar ? channeled : element);
  }

  @Override
  public Set<SubElement> subElements() {
    return Collections.unmodifiableSet(subElements);
  }

  /**
   * Make the given element this bender's element, replacing the previous one. For the
   * Avatar this changes their home element. Sub-Elements the bender may no longer possess
   * are dropped, the others are kept.
   *
   * @param newElement Element to assign
   * @return <code>true</code> if the element changed, <code>false</code> if it already was this one
   */
  public boolean assignElement(Element newElement) {
    if (newElement == element) {
      return false;
    }
    element = newElement;
    dropForeignSubElements();
    return true;
  }

  /**
   * Make this bender the Avatar, or take that status away again. Losing it drops every
   * Sub-Element that does not belong to their element.
   *
   * @param avatar <code>true</code> to grant, <code>false</code> to revoke
   * @return what happened, see {@link AvatarResult}
   */
  public AvatarResult assignAvatar(boolean avatar) {
    if (element == null) {
      return AvatarResult.NO_ELEMENT;
    }
    if (avatar == this.avatar) {
      return AvatarResult.UNCHANGED;
    }
    this.avatar = avatar;
    this.channeled = avatar ? element : null;
    dropForeignSubElements();
    return avatar ? AvatarResult.GRANTED : AvatarResult.REVOKED;
  }

  /**
   * Let the Avatar switch to another of the four classical elements. The choice is part of
   * the persistent state, and is forgotten when the Avatar status is taken away.
   *
   * @param choice Element to channel
   * @return <code>true</code> if switched, <code>false</code> if this bender is not the Avatar
   */
  public boolean channelElement(Element choice) {
    if (!avatar) {
      return false;
    }
    channeled = choice;
    return true;
  }

  /**
   * Grant a Sub-Element, provided the bender may possess it.
   *
   * @param subElement Sub-Element to add
   * @return what happened, see {@link SubElementResult}
   */
  public SubElementResult addSubElement(SubElement subElement) {
    if (element == null) {
      return SubElementResult.NO_ELEMENT;
    }
    if (mayPossess(subElement)) {
      return SubElementResult.INCOMPATIBLE;
    }
    return subElements.add(subElement) ? SubElementResult.ADDED : SubElementResult.ALREADY_KNOWN;
  }

  /**
   * Take a Sub-Element away from this bender.
   *
   * @param subElement Sub-Element to remove
   * @return <code>true</code> if it was known and removed
   */
  public boolean removeSubElement(SubElement subElement) {
    return subElements.remove(subElement);
  }

  /**
   * Gets rid of any Sub-Elements the bender shouldn't be able to possess.
   */
  private void dropForeignSubElements() {
    subElements.removeIf(this::mayPossess);
  }

  /**
   * Checks if the Sub-Element is even possible for the corresponding Bender.
   *
   * @param subElement SubElement to check for
   *
   * @return <code>true</code> if it's not possible, else <code>false</code>
   */
  private boolean mayPossess(SubElement subElement) {
    return !avatar && subElement.parent() != element;
  }

  /**
   * Create an independent copy of a bender. It shares no mutable state with the original,
   * so it can be handed to another thread while the original keeps being modified.
   *
   * @param bender bender to copy
   * @return a detached copy with the same UUID, element, Avatar flag, channeled element and sub-elements
   */
  public static BenderPlayer copyOf(Bender bender) {
    return new BenderPlayer(bender.uuid(), bender.element().orElse(null), bender.isAvatar(), bender.activeElement().orElse(null), bender.subElements());
  }

}
