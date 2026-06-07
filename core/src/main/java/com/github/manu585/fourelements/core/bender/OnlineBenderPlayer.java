package com.github.manu585.fourelements.core.bender;

import com.github.manu585.fourelements.api.bending.registry.Element;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/**
 * Runtime bender on server
 */
public class OnlineBenderPlayer {

  @Getter
  private final BenderPlayer benderPlayer;

  @Getter
  private Element activeElement;
  @Getter
  @Setter
  private boolean inBendingMode;

  /**
   * Constructor for Runtime bender on the server.
   */
  public OnlineBenderPlayer(BenderPlayer benderPlayer) {
    this.benderPlayer = benderPlayer;
  }

  public void setActiveElement(Element activeElement) {
    if (!benderPlayer.elements().contains(activeElement)) {
      throw new IllegalArgumentException("Player " + uuid() + " does not possess element "+ activeElement);
    }
    this.activeElement = activeElement;
  }

  public UUID uuid() {
    return benderPlayer.uuid();
  }

  public List<Element> elements() {
    return benderPlayer.elements();
  }

}
