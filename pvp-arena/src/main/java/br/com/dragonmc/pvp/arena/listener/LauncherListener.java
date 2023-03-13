/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.Sound
 *  org.bukkit.block.BlockFace
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.metadata.FixedMetadataValue
 *  org.bukkit.metadata.MetadataValue
 *  org.bukkit.plugin.Plugin
 */
package br.com.dragonmc.pvp.arena.listener;

import br.com.dragonmc.core.bukkit.event.player.PlayerMoveUpdateEvent;
import br.com.dragonmc.pvp.arena.GameMain;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class LauncherListener
implements Listener {
    @EventHandler
    public void onPlayerMoveUpdate(PlayerMoveUpdateEvent event) {
        Player player = event.getPlayer();
        Material type = event.getTo().getBlock().getRelative(BlockFace.DOWN).getType();
        boolean noFall = false;
        if (type == Material.DIAMOND_BLOCK) {
            player.setVelocity(player.getLocation().getDirection().multiply(0).setY(2.5));
            noFall = true;
        } else if (type == Material.SPONGE) {
            player.setVelocity(player.getLocation().getDirection().multiply(0).setY(4));
            noFall = true;
        }
        if (noFall) {
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 6.0f, 1.0f);
            player.setMetadata("nofall", (MetadataValue)new FixedMetadataValue((Plugin) GameMain.getInstance(), (Object)(System.currentTimeMillis() + 5000L)));
            player.setMetadata("anticheat-bypass", (MetadataValue)new FixedMetadataValue((Plugin)GameMain.getInstance(), (Object)(System.currentTimeMillis() + 5000L)));
        }
    }
}

