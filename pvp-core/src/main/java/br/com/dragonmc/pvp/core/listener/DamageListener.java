/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.PlayerDeathEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.metadata.MetadataValue
 *  org.bukkit.plugin.Plugin
 */
package br.com.dragonmc.pvp.core.listener;

import br.com.dragonmc.core.bukkit.event.player.PlayerCommandEvent;
import br.com.dragonmc.core.bukkit.event.player.PlayerDamagePlayerEvent;
import br.com.dragonmc.pvp.core.GameAPI;
import br.com.dragonmc.pvp.core.GameConst;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class DamageListener
implements Listener {
    @EventHandler
    public void onPlayerDamagePlayer(PlayerDamagePlayerEvent event) {
        Player player = event.getPlayer();
        Player damager = event.getDamager();
        if (GameAPI.getInstance().getGamerManager().getGamer(damager.getUniqueId()).isSpawnProtection() || GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId()).isSpawnProtection()) {
            return;
        }
        player.setMetadata("combatlog", GameAPI.getInstance().createMeta(System.currentTimeMillis() + 12000L));
        damager.setMetadata("combatlog", GameAPI.getInstance().createMeta(System.currentTimeMillis() + 12000L));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (this.isInCombatlog(player)) {
            player.damage(2.147483647E9);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (player.getKiller() instanceof Player) {
            player.getKiller().removeMetadata("combatlog", (Plugin)GameAPI.getInstance());
        }
        player.removeMetadata("combatlog", (Plugin)GameAPI.getInstance());
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandEvent event) {
        Player player = event.getPlayer();
        if (this.isInCombatlog(player) && GameConst.BLOCKED_COMMANDS.contains(event.getCommandLabel().toLowerCase())) {
            event.setCancelled(true);
            player.sendMessage("\u00a7cVoc\u00ea est\u00e1 em combate, aguarde para executar esse comando.");
        }
    }

    public boolean isInCombatlog(Player player) {
        if (player.hasMetadata("combatlog")) {
            MetadataValue metadataValue = player.getMetadata("combatlog").stream().findFirst().orElse(null);
            long expire = metadataValue.asLong();
            return expire > System.currentTimeMillis();
        }
        return false;
    }
}

