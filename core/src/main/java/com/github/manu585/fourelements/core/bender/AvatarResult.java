package com.github.manu585.fourelements.core.bender;

/**
 * Outcome of {@link BenderPlayer#assignAvatar}.
 */
public enum AvatarResult {

  // The bender became the Avatar
  GRANTED,

  // The bender is no longer the Avatar
  REVOKED,

  // The bender already was (or already was not) the Avatar
  UNCHANGED,

  // The bender has no element yet, and the Avatar needs a home element
  NO_ELEMENT

}
