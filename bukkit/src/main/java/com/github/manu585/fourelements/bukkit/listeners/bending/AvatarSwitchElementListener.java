package com.github.manu585.fourelements.bukkit.listeners.bending;

import com.github.manu585.fourelements.api.bending.Element;
import com.github.manu585.fourelements.bukkit.manager.BenderManager;
import com.github.manu585.fourelements.core.bender.OnlineBenderPlayer;
import java.util.UUID;
import lombok.AllArgsConstructor;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Color;
import org.bukkit.Particle;
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
    Player player = event.getPlayer();
    OnlineBenderPlayer benderPlayer = benderManager.getBender(uuid);

    if (!benderPlayer.isAvatar()) return;
    if (benderPlayer.activeElement().isEmpty()) return;

    Element nextElement = Element.nextElementInCycle(benderPlayer.activeElement().get());
    benderPlayer.chooseActiveElement(nextElement);
    playElementPickAnimation(nextElement, player);

    // Persist
    benderManager.save(benderPlayer.benderPlayer());
  }

  private void playElementPickAnimation(Element element, Player player) {
    player.playSound(Sound.sound(Key.key("ui.loom.select_pattern"), Sound.Source.NEUTRAL, 1F, 1.2F));

    switch (element) {
      case AIR: {
        Particle.CLOUD.builder()
            .location(player.getEyeLocation())
            .offset(0.4, 0.2, 0.4)
            .speed(0.2)
            .count(10)
            .receivers(player)
            .spawn();
        break;
      }

      case WATER: {
        Particle.SPLASH.builder()
            .location(player.getEyeLocation())
            .offset(0.4, 0.2, 0.4)
            .speed(0.2)
            .count(10)
            .receivers(player)
            .spawn();
        break;
      }

      case EARTH: {
        Particle.DUST.builder()
            .color(Color.GREEN)
            .location(player.getEyeLocation())
            .offset(0.4, 0.2, 0.4)
            .speed(0.2)
            .count(10)
            .receivers(player)
            .spawn();
        break;
      }

      case FIRE: {
        Particle.FLAME.builder()
            .location(player.getEyeLocation())
            .offset(0.4, 0.2, 0.4)
            .speed(0.2)
            .count(10)
            .receivers(player)
            .spawn();
        break;
      }
    }
  }

}
