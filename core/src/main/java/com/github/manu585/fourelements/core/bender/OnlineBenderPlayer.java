package com.github.manu585.fourelements.core.bender;

import com.github.manu585.fourelements.api.bender.Bender;
import com.github.manu585.fourelements.api.bending.Element;
import com.github.manu585.fourelements.api.bending.SubElement;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Runtime bender: an online player's {@link BenderPlayer} plus transient, session-only
 * state (bending mode) that is never persisted.
 *
 * <p>Delegates the persistent {@link Bender} contract to the wrapped {@link BenderPlayer},
 * so an online bender can be used anywhere a {@link Bender} is expected.
 */
public class OnlineBenderPlayer implements Bender {

  private final BenderPlayer benderPlayer;

  private BendingMode mode = BendingMode.PASSIVE;
  private double chi;

  public OnlineBenderPlayer(BenderPlayer benderPlayer) {
    this.benderPlayer = benderPlayer;
  }

  /**
   * @return the persistent bender backing this runtime object (what gets saved)
   */
  public BenderPlayer benderPlayer() {
    return benderPlayer;
  }

  public BendingMode mode() {
    return mode;
  }

  public void mode(BendingMode mode) {
    this.mode = mode;
  }

  /**
   * Let the Avatar switch to another of the four classical elements.
   *
   * @param choice Element to channel
   * @return <code>true</code> if switched, <code>false</code> if this bender is not the Avatar
   * @see BenderPlayer#channelElement(Element)
   */
  public boolean chooseActiveElement(Element choice) {
    return benderPlayer.channelElement(choice);
  }

  @Override
  public UUID uuid() {
    return benderPlayer.uuid();
  }

  @Override
  public Optional<Element> element() {
    return benderPlayer.element();
  }

  @Override
  public boolean isAvatar() {
    return benderPlayer.isAvatar();
  }

  @Override
  public Optional<Element> activeElement() {
    return benderPlayer.activeElement();
  }

  @Override
  public Set<SubElement> subElements() {
    return benderPlayer.subElements();
  }

}
