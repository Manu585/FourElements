package com.github.manu585.fourelements.core.bender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.manu585.fourelements.api.bending.Element;
import com.github.manu585.fourelements.api.bending.SubElement;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class BenderPlayerTest {

  private final BenderPlayer bender = new BenderPlayer(UUID.randomUUID());

  // --- element ---

  @Test
  void newBender_isNotABender() {
    assertFalse(bender.isBender());
    assertFalse(bender.isAvatar());
    assertEquals(Optional.empty(), bender.element());
    assertTrue(bender.subElements().isEmpty());
  }

  @Test
  void assignElement_replacesPreviousElement() {
    bender.assignElement(Element.FIRE);

    assertTrue(bender.assignElement(Element.WATER));
    assertEquals(Optional.of(Element.WATER), bender.element());
  }

  @Test
  void assignElement_sameElement_reportsNoChange() {
    bender.assignElement(Element.FIRE);

    assertFalse(bender.assignElement(Element.FIRE));
  }

  @Test
  void assignElement_dropsSubElementsTheNewElementDoesNotAccept() {
    bender.assignElement(Element.FIRE);
    bender.addSubElement(SubElement.LIGHTNING);

    bender.assignElement(Element.WATER);

    assertTrue(bender.subElements().isEmpty());
  }

  // --- avatar ---

  @Test
  void assignAvatar_withoutElement_isRejected() {
    assertEquals(AvatarResult.NO_ELEMENT, bender.assignAvatar(true));
    assertFalse(bender.isAvatar());
  }

  @Test
  void assignAvatar_grantsAndRevokes() {
    bender.assignElement(Element.EARTH);

    assertEquals(AvatarResult.GRANTED, bender.assignAvatar(true));
    assertTrue(bender.isAvatar());
    assertEquals(AvatarResult.UNCHANGED, bender.assignAvatar(true));
    assertEquals(AvatarResult.REVOKED, bender.assignAvatar(false));
    assertFalse(bender.isAvatar());
    assertEquals(AvatarResult.UNCHANGED, bender.assignAvatar(false));
  }

  @Test
  void assignAvatar_revoked_dropsForeignSubElementsButKeepsOwn() {
    bender.assignElement(Element.FIRE);
    bender.assignAvatar(true);
    bender.addSubElement(SubElement.LIGHTNING);
    bender.addSubElement(SubElement.ICE);

    bender.assignAvatar(false);

    assertEquals(Set.of(SubElement.LIGHTNING), bender.subElements());
  }

  @Test
  void assignElement_ofAvatar_keepsSubElementsAndStatus() {
    bender.assignElement(Element.FIRE);
    bender.assignAvatar(true);
    bender.addSubElement(SubElement.ICE);

    bender.assignElement(Element.WATER);

    assertTrue(bender.isAvatar());
    assertEquals(Set.of(SubElement.ICE), bender.subElements());
  }

  // --- sub-elements ---

  @Test
  void addSubElement_withoutElement_isRejected() {
    assertEquals(SubElementResult.NO_ELEMENT, bender.addSubElement(SubElement.ICE));
    assertTrue(bender.subElements().isEmpty());
  }

  @Test
  void addSubElement_ofParentElement_isAdded() {
    bender.assignElement(Element.WATER);

    assertEquals(SubElementResult.ADDED, bender.addSubElement(SubElement.ICE));
    assertEquals(SubElementResult.ALREADY_KNOWN, bender.addSubElement(SubElement.ICE));
  }

  @Test
  void addSubElement_ofOtherElement_isRejected() {
    bender.assignElement(Element.WATER);

    assertEquals(SubElementResult.INCOMPATIBLE, bender.addSubElement(SubElement.LIGHTNING));
    assertTrue(bender.subElements().isEmpty());
  }

  @Test
  void addSubElement_toAvatar_acceptsAnySubElement() {
    bender.assignElement(Element.AIR);
    bender.assignAvatar(true);

    for (SubElement subElement : SubElement.values()) {
      assertEquals(SubElementResult.ADDED, bender.addSubElement(subElement));
    }
  }

  @Test
  void removeSubElement_reportsWhetherItWasKnown() {
    bender.assignElement(Element.EARTH);
    bender.addSubElement(SubElement.METAL);

    assertTrue(bender.removeSubElement(SubElement.METAL));
    assertFalse(bender.removeSubElement(SubElement.METAL));
  }

  // --- construction ---

  @Test
  void constructor_withValidState_keepsIt() {
    BenderPlayer loaded = new BenderPlayer(UUID.randomUUID(), Element.EARTH, false, null, List.of(SubElement.LAVA));

    assertEquals(Optional.of(Element.EARTH), loaded.element());
    assertEquals(Set.of(SubElement.LAVA), loaded.subElements());
  }

  @Test
  void constructor_withIncompatibleSubElement_throws() {
    UUID uuid = UUID.randomUUID();

    assertThrows(IllegalArgumentException.class, () -> new BenderPlayer(uuid, Element.EARTH, false, null, List.of(SubElement.ICE)));
    assertThrows(IllegalArgumentException.class, () -> new BenderPlayer(uuid, null, false, null, List.of(SubElement.ICE)));
  }

  @Test
  void constructor_avatarWithoutElement_throws() {
    assertThrows(IllegalArgumentException.class, () -> new BenderPlayer(UUID.randomUUID(), null, true, null, List.of()));
  }

  // --- active element ---

  @Test
  void activeElement_ofRegularBender_isTheirElement() {
    bender.assignElement(Element.FIRE);
    OnlineBenderPlayer online = new OnlineBenderPlayer(bender);

    assertEquals(Optional.of(Element.FIRE), online.activeElement());
    assertFalse(online.chooseActiveElement(Element.WATER));
    assertEquals(Optional.of(Element.FIRE), online.activeElement());
  }

  @Test
  void activeElement_ofAvatar_startsAtHomeElementAndFollowsTheirChoice() {
    bender.assignElement(Element.WATER);
    bender.assignAvatar(true);
    OnlineBenderPlayer online = new OnlineBenderPlayer(bender);

    assertEquals(Optional.of(Element.WATER), online.activeElement());
    assertTrue(online.chooseActiveElement(Element.EARTH));
    assertEquals(Optional.of(Element.EARTH), online.activeElement());
  }

  @Test
  void activeElement_ofNonBender_isEmpty() {
    assertEquals(Optional.empty(), new OnlineBenderPlayer(bender).activeElement());
  }

  // --- channeled element (persisted) ---

  @Test
  void channeledElement_isForgottenWhenAvatarIsRevoked() {
    bender.assignElement(Element.FIRE);
    bender.assignAvatar(true);
    bender.channelElement(Element.EARTH);

    bender.assignAvatar(false);
    assertEquals(Optional.of(Element.FIRE), bender.activeElement());

    bender.assignAvatar(true);
    assertEquals(Optional.of(Element.FIRE), bender.activeElement());
  }

  @Test
  void channeledElement_survivesChangingTheHomeElement() {
    bender.assignElement(Element.FIRE);
    bender.assignAvatar(true);
    bender.channelElement(Element.EARTH);

    bender.assignElement(Element.WATER);

    assertEquals(Optional.of(Element.EARTH), bender.activeElement());
  }

  @Test
  void constructor_restoresTheChanneledElementOfTheAvatar() {
    BenderPlayer loaded = new BenderPlayer(UUID.randomUUID(), Element.AIR, true, Element.EARTH, List.of());

    assertEquals(Optional.of(Element.EARTH), loaded.activeElement());
  }

  @Test
  void constructor_withoutStoredChannel_defaultsToHomeElement() {
    BenderPlayer loaded = new BenderPlayer(UUID.randomUUID(), Element.AIR, true, null, List.of());

    assertEquals(Optional.of(Element.AIR), loaded.activeElement());
  }

  @Test
  void constructor_ignoresStaleChannelOfNonAvatar() {
    BenderPlayer loaded = new BenderPlayer(UUID.randomUUID(), Element.AIR, false, Element.EARTH, List.of());

    assertEquals(Optional.of(Element.AIR), loaded.activeElement());
    assertFalse(loaded.channelElement(Element.FIRE));
  }

  // --- copy ---

  @Test
  void copyOf_isIndependentOfTheOriginal() {
    bender.assignElement(Element.FIRE);
    bender.assignAvatar(true);
    bender.channelElement(Element.EARTH);
    bender.addSubElement(SubElement.ICE);

    BenderPlayer copy = BenderPlayer.copyOf(bender);
    bender.assignAvatar(false);
    bender.assignElement(Element.WATER);

    assertEquals(Optional.of(Element.FIRE), copy.element());
    assertTrue(copy.isAvatar());
    assertEquals(Optional.of(Element.EARTH), copy.activeElement());
    assertEquals(Set.of(SubElement.ICE), copy.subElements());
  }

  // --- mode ---

  @Test
  void mode_startsPassive() {
    OnlineBenderPlayer online = new OnlineBenderPlayer(bender);

    assertEquals(BendingMode.PASSIVE, online.mode());
    online.mode(BendingMode.COMBAT);
    assertEquals(BendingMode.COMBAT, online.mode());
  }

}
