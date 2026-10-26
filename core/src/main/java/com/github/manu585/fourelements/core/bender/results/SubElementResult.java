package com.github.manu585.fourelements.core.bender.results;

import com.github.manu585.fourelements.core.bender.BenderPlayer;

/**
 * Outcome of {@link BenderPlayer#addSubElement}.
 */
public enum SubElementResult {

  // The Sub-Element was added
  ADDED,

  // The bender already possessed the Sub-Element
  ALREADY_KNOWN,

  // The bender has no main element yet
  NO_ELEMENT,

  // The Sub-Element is not valid for the bender's main element
  INCOMPATIBLE

}
