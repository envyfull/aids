/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  org.bukkit.GameMode
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.inventory.InventoryAction
 *  org.bukkit.event.inventory.InventoryClickEvent
 */
package br.com.dragonmc.core.bukkit.anticheat.hack.verify;

import br.com.dragonmc.core.bukkit.anticheat.hack.Clicks;
import br.com.dragonmc.core.bukkit.anticheat.hack.HackType;
import br.com.dragonmc.core.bukkit.anticheat.hack.Verify;
import br.com.dragonmc.core.bukkit.event.UpdateEvent;
import com.google.common.collect.ImmutableList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MacroVerify
implements Verify {
    private Map<Player, Clicks> clicksPerSecond = new HashMap<Player, Clicks>();

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    private void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player)event.getWhoClicked();
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.ADVENTURE) {
            return;
        }
        if (event.isShiftClick() && event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            return;
        }
        Clicks click = this.clicksPerSecond.computeIfAbsent(player, v -> new Clicks());
        if (click.getExpireTime() < System.currentTimeMillis()) {
            if (click.getClicks() >= 25) {
                this.alert(player);
            }
            this.clicksPerSecond.remove(player);
            return;
        }
        click.addClick();
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        ImmutableList.copyOf(this.clicksPerSecond.entrySet()).stream().filter(entry -> ((Clicks)entry.getValue()).getExpireTime() < System.currentTimeMillis()).forEach(entry -> {
            if (((Clicks)entry.getValue()).getClicks() >= 20) {
                this.alert((Player)entry.getKey(), "( " + ((Clicks)entry.getValue()).getClicks() + " cps)");
            }
            this.clicksPerSecond.remove(entry.getKey());
        });
    }

    @Override
    public HackType getHackType() {
        return HackType.MACRO;
    }
}

