package com.github.manu585.fourelements.bukkit.listeners.bending;

import com.github.manu585.fourelements.api.bending.Element;
import com.github.manu585.fourelements.bukkit.manager.BenderManager;
import com.github.manu585.fourelements.bukkit.text.BendingText;
import com.github.manu585.fourelements.core.bender.OnlineBenderPlayer;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

@AllArgsConstructor
public class AvatarSwitchElementListener implements Listener {

  private final BenderManager benderManager;

  @EventHandler
  public void onOffHandSwitch(PlayerSwapHandItemsEvent event) {
    UUID uuid = event.getPlayer().getUniqueId();
    OnlineBenderPlayer benderPlayer = benderManager.getBender(uuid);

    if (!benderPlayer.isAvatar()) return;
    if (benderPlayer.activeElement().isEmpty()) return;

    Element nextElement = Element.nextElementInCycle(benderPlayer.activeElement().get());
    chooseElement(nextElement, benderPlayer);
  }

  private void chooseElement(Element element, OnlineBenderPlayer bender) {
    Player player = Bukkit.getPlayer(bender.uuid());
    if (player == null) return;

    switch (element) {
      case AIR: {
        player.sendMessage(BendingText.name(Element.AIR));
        bender.chooseActiveElement(Element.AIR);
        break;
      }

      case WATER: {
        player.sendMessage(BendingText.name(Element.WATER));
        bender.chooseActiveElement(Element.WATER);
        break;
      }

      case EARTH: {
        player.sendMessage(BendingText.name(Element.EARTH));
        bender.chooseActiveElement(Element.EARTH);
        break;
      }

      case FIRE: {
        player.sendMessage(BendingText.name(Element.FIRE));
        bender.chooseActiveElement(Element.FIRE);
        break;
      }
    }
  }

}
