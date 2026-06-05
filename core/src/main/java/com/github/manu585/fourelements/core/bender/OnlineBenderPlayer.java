package com.github.manu585.fourelements.core.bender;

import com.github.manu585.fourelements.api.bending.registry.Element;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

public class OnlineBenderPlayer extends BenderPlayer {

  @Getter
  @Setter
  private Element activeElement;

  public OnlineBenderPlayer(UUID uuid, List<Element> elements) {
    super(uuid, elements);
  }

}
