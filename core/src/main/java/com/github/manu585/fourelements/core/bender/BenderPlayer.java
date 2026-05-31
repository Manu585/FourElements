package com.github.manu585.fourelements.core.bender;

import com.github.manu585.fourelements.api.bender.Bender;
import com.github.manu585.fourelements.api.bending.registry.Element;
import java.util.List;
import java.util.UUID;

public record BenderPlayer(UUID uuid, List<Element> elements) implements Bender {

  public BenderPlayer(UUID uuid, List<Element> elements) {
    this.uuid = uuid;
    this.elements = elements == null || elements.isEmpty() ? List.of() : elements;
  }

  public BenderPlayer(UUID uuid, Element element) {
    this(uuid, List.of(element));
  }

  @Override
  public List<Element> elements() {
    return List.copyOf(elements);
  }

}
