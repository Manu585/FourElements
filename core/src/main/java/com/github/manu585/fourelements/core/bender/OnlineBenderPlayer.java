package com.github.manu585.fourelements.core.bender;

import com.github.manu585.fourelements.api.bender.Bender;
import com.github.manu585.fourelements.api.bender.OnlineBender;
import com.github.manu585.fourelements.api.bending.Element;
import com.github.manu585.fourelements.api.bending.SubElement;
import com.github.manu585.fourelements.core.bender.chi.ChiPool;
import com.github.manu585.fourelements.core.bender.mode.BendingMode;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Runtime bender: an online player's {@link BenderPlayer} plus transient, session-only
 * state (bending mode) that is never persisted.
 *
 * <p>Delegates the persistent {@link Bender} contract to the wrapped {@link BenderPlayer},
 * so an online bender can be used anywhere a {@link Bender} is expected, and adds the
 * session-only {@link OnlineBender} state.
 */
public class OnlineBenderPlayer implements OnlineBender {

  private final BenderPlayer benderPlayer;
  private final ChiPool chi;

  private BendingMode mode = BendingMode.PASSIVE;

  public OnlineBenderPlayer(BenderPlayer benderPlayer) {
    this.benderPlayer = benderPlayer;
    this.chi = new ChiPool(ChiPool.DEFAULT_MAX);
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
   * The API only sees this as a read-only {@link com.github.manu585.fourelements.api.bender.Chi},
   * the plugin itself gets the pool to spend and restore chi.
   */
  @Override
  public ChiPool chi() {
    return chi;
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
