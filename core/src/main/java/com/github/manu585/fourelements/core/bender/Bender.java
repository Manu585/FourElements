package com.github.manu585.fourelements.core.bender;

import com.github.manu585.fourelements.api.bender.IBender;
import com.github.manu585.fourelements.api.bending.element.Element;
import java.util.List;
import java.util.UUID;

public record Bender(UUID uuid, List<Element> elements) implements IBender {

  public Bender(UUID uuid, List<Element> elements) {
    this.uuid = uuid;
    this.elements = elements == null || elements.isEmpty() ? List.of() : elements;
  }

  public Bender(UUID uuid, Element element) {
    this(uuid, List.of(element));
  }

  @Override
  public List<Element> elements() {
    return List.copyOf(elements);
  }

}
