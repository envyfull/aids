/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_8_R3.MinecraftServer
 *  org.bukkit.GameMode
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 */
package br.com.dragonmc.core.bukkit.anticheat.hack.verify;

import br.com.dragonmc.core.bukkit.anticheat.gamer.UserData;
import br.com.dragonmc.core.bukkit.anticheat.hack.HackType;
import br.com.dragonmc.core.bukkit.anticheat.hack.Verify;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class ReachVerify
implements Verify {
    @EventHandler(priority=EventPriority.LOWEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        int ping;
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        if (MinecraftServer.getServer().recentTps[0] <= 19.98) {
            return;
        }
        Player player = (Player)event.getDamager();
        UserData userData = this.getUserData(player);
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
            return;
        }
        if (player.getAllowFlight()) {
            return;
        }
        double distance = Math.sqrt(player.getLocation().distanceSquared(event.getEntity().getLocation())) - 0.55;
        double maxDistance = 5.5;
        if (player.isSprinting()) {
            distance -= 0.25;
        }
        if ((ping = userData.getPing()) >= 25) {
            distance -= (double)(ping / 500);
        }
        if (distance >= maxDistance) {
            this.alert(player, "distance: " + distance + ", max: " + maxDistance + ", ping: " + ping);
        }
    }

    @Override
    public HackType getHackType() {
        return HackType.REACH;
    }
}

