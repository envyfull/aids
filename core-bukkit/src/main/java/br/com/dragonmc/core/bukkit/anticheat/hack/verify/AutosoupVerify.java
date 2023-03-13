/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.inventory.InventoryAction
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 */
package br.com.dragonmc.core.bukkit.anticheat.hack.verify;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import br.com.dragonmc.core.bukkit.anticheat.hack.HackType;
import br.com.dragonmc.core.bukkit.anticheat.hack.Verify;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class AutosoupVerify
implements Verify {
    private Map<UUID, Long> time = new HashMap<UUID, Long>();

    @EventHandler
    private void onClick(InventoryClickEvent event) {
        if (!event.getAction().equals((Object)InventoryAction.MOVE_TO_OTHER_INVENTORY) || event.getCurrentItem() == null || !event.getCurrentItem().getType().equals((Object)Material.MUSHROOM_SOUP)) {
            return;
        }
        this.time.put(event.getWhoClicked().getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler(priority=EventPriority.LOWEST)
    private void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasItem() || !event.getItem().getType().equals((Object)Material.MUSHROOM_SOUP)) {
            return;
        }
        Player player = event.getPlayer();
        UUID uniqueId = player.getUniqueId();
        if (this.time.containsKey(uniqueId)) {
            Long spentTime = System.currentTimeMillis() - this.time.get(uniqueId);
            if (spentTime <= 10L) {
                this.alert(player);
            }
            this.time.remove(uniqueId);
        }
    }

    @Override
    public HackType getHackType() {
        return HackType.AUTOSOUP;
    }
}

