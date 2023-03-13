/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
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
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;

public class FlyCheck
implements Verify {
    @EventHandler(priority=EventPriority.LOW)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (this.isIgnore(player)) {
            return;
        }
        UserData userData = this.getUserData(event.getPlayer());
        Location lastLocation = userData.getLastLocation();
        double distance = Math.pow(event.getFrom().getX() - lastLocation.getX(), 2.0) + Math.pow(event.getFrom().getZ() - lastLocation.getZ(), 2.0);
        if (player.getAllowFlight() || userData.getPing() > 150) {
            return;
        }
        if (userData.isFalling()) {
            return;
        }
        Block feetBlock = lastLocation.clone().subtract(0.0, 1.0, 0.0).getBlock();
        for (Block block : new Block[]{feetBlock, feetBlock.getRelative(BlockFace.NORTH), feetBlock.getRelative(BlockFace.SOUTH), feetBlock.getRelative(BlockFace.EAST), feetBlock.getRelative(BlockFace.WEST)}) {
            if (block.getType() == Material.AIR) continue;
            return;
        }
        feetBlock = event.getTo().clone().subtract(0.0, 1.0, 0.0).getBlock();
        for (Block block : new Block[]{feetBlock, feetBlock.getRelative(BlockFace.NORTH), feetBlock.getRelative(BlockFace.SOUTH), feetBlock.getRelative(BlockFace.EAST), feetBlock.getRelative(BlockFace.WEST)}) {
            if (block.getType() == Material.AIR) continue;
            return;
        }
        double maxJump = 2.5;
        PotionEffect potion = player.getActivePotionEffects().stream().filter(potionEffect -> potionEffect.getType().getName().equals("JUMP")).findFirst().orElse(null);
        if (potion != null) {
            maxJump += (double)potion.getAmplifier() * 0.75;
        }
        if (userData.isGoingUp() && userData.getDistanceY() < maxJump) {
            return;
        }
        this.alert(player);
        this.ignore(player, 0.5);
    }

    @Override
    public HackType getHackType() {
        return HackType.FLY;
    }
}

