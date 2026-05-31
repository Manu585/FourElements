package com.github.manu585.fourelements.api.bending.metadata.air;

import com.github.manu585.fourelements.api.bending.metadata.AbilityMetadata;
import com.github.manu585.fourelements.api.bending.registry.Element;

public class AirBlast implements AbilityMetadata {

  @Override
  public String name() {
    return "AirBlast";
  }

  @Override
  public String description() {
    return "A powerful blast of air.";
  }

  @Override
  public String author() {
    return "Manu";
  }

  @Override
  public Element element() {
    return Element.AIR;
  }

}
