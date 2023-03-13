/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.player.PlayerMoveEvent
 *  org.bukkit.potion.PotionEffect
 */
package br.com.dragonmc.core.bukkit.anticheat.hack.verify;

import br.com.dragonmc.core.bukkit.anticheat.gamer.UserData;
import br.com.dragonmc.core.bukkit.anticheat.hack.HackType;
import br.com.dragonmc.core.bukkit.anticheat.hack.Verify;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;

public class SpeedCheck
implements Verify {
    @EventHandler(priority=EventPriority.LOW)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (this.isIgnore(player)) {
            return;
        }
        UserData userData = this.getUserData(event.getPlayer());
        Location lastLocation = userData.getLastLocation() == null ? event.getFrom() : userData.getLastLocation();
        double distance = Math.pow(event.getFrom().getX() - lastLocation.getX(), 2.0) + Math.pow(event.getFrom().getZ() - lastLocation.getZ(), 2.0);
        if (player.getAllowFlight() || userData.getPing() > 150 || distance < Math.pow(2.5, 2.0)) {
            return;
        }
        float maxWalkSpeed = player.getWalkSpeed() * 1.2f;
        PotionEffect potion = player.getActivePotionEffects().stream().filter(potionEffect -> potionEffect.getType().getName().equals("SPEED")).findFirst().orElse(null);
        if (potion != null) {
            return;
        }
        if (distance > Math.pow(maxWalkSpeed, 2.0)) {
            this.alert(player, "(distance " + distance + ", maxSpeed: " + maxWalkSpeed + ")");
            this.ignore(player, 0.5);
        }
    }

    @Override
    public HackType getHackType() {
        return HackType.SPEED;
    }
}

