package com.github.manu585.fourelements.core.bender;

import com.github.manu585.fourelements.api.bender.IBender;
import com.github.manu585.fourelements.api.bending.element.Elements;
import java.util.Set;
import java.util.UUID;

public class Bender implements IBender {

  private final UUID uuid;
  private final Set<Elements> elements;

  public Bender(UUID uuid, Set<Elements> elements) {
    this.uuid = uuid;
    this.elements = elements == null || elements.isEmpty() ? Set.of() : elements;
  }

  @Override
  public UUID uuid() {
    return uuid;
  }

  @Override
  public Set<Elements> elements() {
    return Set.copyOf(elements);
  }

}
