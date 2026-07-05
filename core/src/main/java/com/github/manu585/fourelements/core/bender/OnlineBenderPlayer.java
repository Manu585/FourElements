package com.github.manu585.fourelements.core.bender;

import com.github.manu585.fourelements.api.bender.Bender;
import com.github.manu585.fourelements.api.bending.Element;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/**
 * Runtime bender: an online player's {@link BenderPlayer} plus transient, session-only
 * state (active element, bending mode) that is never persisted.
 *
 * <p>Delegates the persistent {@link Bender} contract to the wrapped {@link BenderPlayer},
 * so an online bender can be used anywhere a {@link Bender} is expected.
 */
public class OnlineBenderPlayer implements Bender {

  private final BenderPlayer benderPlayer;

  private Element activeElement;
  @Setter
  @Getter
  private boolean inBendingMode;

  public OnlineBenderPlayer(BenderPlayer benderPlayer) {
    this.benderPlayer = benderPlayer;
  }

  /**
   * @return the persistent bender backing this runtime object (what gets saved)
   */
  public BenderPlayer benderPlayer() {
    return benderPlayer;
  }

  public Element activeElement() {
    return activeElement;
  }

  /**
   * Set the element this bender is currently channeling.
   *
   * @param activeElement Element to activate; must be one the bender possesses
   * @throws IllegalArgumentException if the bender does not possess the element
   */
  public void setActiveElement(Element activeElement) {
    if (!benderPlayer.hasElement(activeElement)) {
      throw new IllegalArgumentException("Player " + uuid() + " does not possess element " + activeElement);
    }
    this.activeElement = activeElement;
  }

  @Override
  public UUID uuid() {
    return benderPlayer.uuid();
  }

  @Override
  public List<Element> elements() {
    return benderPlayer.elements();
  }

}
