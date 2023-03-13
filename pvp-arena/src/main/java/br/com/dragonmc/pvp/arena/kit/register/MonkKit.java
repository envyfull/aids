/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.player.PlayerInteractEntityEvent
 *  org.bukkit.inventory.ItemStack
 */
package br.com.dragonmc.pvp.arena.kit.register;

import java.util.Arrays;
import java.util.Random;
import br.com.dragonmc.pvp.arena.GameMain;
import br.com.dragonmc.pvp.arena.kit.Kit;
import net.highmc.bukkit.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class MonkKit
extends Kit {
    public MonkKit() {
        super("Monk", "Bagunce o invent\u00e1rio de seus inimigos", Material.BLAZE_ROD, 11500, Arrays.asList(new ItemBuilder().name("\u00a7aMonk").type(Material.BLAZE_ROD).build()));
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent e) {
        if (!(e.getRightClicked() instanceof Player)) {
            return;
        }
        Player player = e.getPlayer();
        if (!this.hasAbility(player)) {
            return;
        }
        ItemStack item = player.getItemInHand();
        if (!this.isAbilityItem(item)) {
            return;
        }
        Player clicked = (Player)e.getRightClicked();
        if (GameMain.getInstance().getGamerManager().getGamer(clicked.getUniqueId()).isSpawnProtection()) {
            return;
        }
        if (!this.isCooldown(player)) {
            ItemStack random;
            this.addCooldown(player, 15L);
            int randomN = new Random().nextInt(36);
            ItemStack atual = clicked.getItemInHand() != null ? clicked.getItemInHand().clone() : null;
            ItemStack itemStack = random = clicked.getInventory().getItem(randomN) != null ? clicked.getInventory().getItem(randomN).clone() : null;
            if (random == null) {
                clicked.getInventory().setItem(randomN, atual);
                clicked.setItemInHand(null);
            } else {
                clicked.getInventory().setItem(randomN, atual);
                clicked.getInventory().setItemInHand(random);
            }
        }
    }
}

