package com.github.manu585.fourelements.api.metadata;

import com.github.manu585.fourelements.api.bending.element.Element;
import java.util.UUID;

public interface AbilityMetadata {

  UUID uuid();

  String name();

  String description();

  String author();

  Element element();

}
