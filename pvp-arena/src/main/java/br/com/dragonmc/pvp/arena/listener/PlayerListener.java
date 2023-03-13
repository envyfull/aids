/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerDropItemEvent
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.inventory.ItemStack
 */
package br.com.dragonmc.pvp.arena.listener;

import br.com.dragonmc.core.bukkit.BukkitCommon;
import br.com.dragonmc.core.bukkit.utils.item.ActionItemStack;
import br.com.dragonmc.core.bukkit.utils.item.ItemBuilder;
import br.com.dragonmc.pvp.arena.GameMain;
import br.com.dragonmc.pvp.arena.gamer.Gamer;
import br.com.dragonmc.pvp.arena.kit.Kit;
import br.com.dragonmc.pvp.arena.menu.AbilityInventory;
import br.com.dragonmc.pvp.core.event.PlayerProtectionEvent;
import br.com.dragonmc.pvp.core.event.PlayerSpawnEvent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerListener
implements Listener {
    private static final ActionItemStack SELECT_PRIMARY = new ActionItemStack(new ItemBuilder().name("\u00a7aSelecionar kit 1").type(Material.CHEST).build(), new ActionItemStack.Interact(){

        @Override
        public boolean onInteract(Player player, Entity entity, Block block, ItemStack item, ActionItemStack.ActionType action) {
            new AbilityInventory(player, AbilityInventory.InventoryType.PRIMARY);
            return false;
        }
    });
    private static final ActionItemStack SELECT_SECONDARY = new ActionItemStack(new ItemBuilder().name("\u00a7aSelecionar kit 2").type(Material.CHEST).build(), new ActionItemStack.Interact(){

        @Override
        public boolean onInteract(Player player, Entity entity, Block block, ItemStack item, ActionItemStack.ActionType action) {
            new AbilityInventory(player, AbilityInventory.InventoryType.SECONDARY);
            return false;
        }
    });

    @EventHandler(priority=EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.handlePlayer(event.getPlayer());
        event.getPlayer().teleport(BukkitCommon.getInstance().getLocationManager().getLocation("spawn"));
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onPlayerSpawn(PlayerSpawnEvent event) {
        this.handlePlayer(event.getPlayer());
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onPlayerProtection(PlayerProtectionEvent event) {
        if (event.getNewState()) {
            Gamer gamer = GameMain.getInstance().getGamerManager().getGamer(event.getPlayer().getUniqueId(), Gamer.class);
            gamer.setPrimaryKit(null);
            gamer.setSecondaryKit(null);
        } else {
            Player player = event.getPlayer();
            this.handleCombatPlayer(player);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItemDrop().getItemStack();
        if (itemStack.isSimilar(SELECT_PRIMARY.getItemStack()) || itemStack.isSimilar(SELECT_SECONDARY.getItemStack())) {
            event.setCancelled(true);
            return;
        }
        Kit kit = GameMain.getInstance().getGamerManager().getGamer(player.getUniqueId(), Gamer.class).getPrimaryKit();
        if (kit != null && kit.isAbilityItem(itemStack)) {
            event.setCancelled(true);
            return;
        }
        kit = GameMain.getInstance().getGamerManager().getGamer(player.getUniqueId(), Gamer.class).getSecondaryKit();
        if (kit != null && kit.isAbilityItem(itemStack)) {
            event.setCancelled(true);
        }
    }

    private void resetPlayer(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setFireTicks(0);
        player.setExp(0.0f);
        player.setLevel(0);
        player.setFallDistance(-1.0f);
    }

    public void handleCombatPlayer(Player player) {
        this.resetPlayer(player);
        player.getInventory().setItem(0, new ItemBuilder().type(Material.STONE_SWORD).build());
        Kit kit = GameMain.getInstance().getGamerManager().getGamer(player.getUniqueId(), Gamer.class).getPrimaryKit();
        if (kit != null) {
            kit.applyKit(player);
        }
        if ((kit = GameMain.getInstance().getGamerManager().getGamer(player.getUniqueId(), Gamer.class).getSecondaryKit()) != null) {
            kit.applyKit(player);
        }
        for (int x = 0; x < 36; ++x) {
            player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.MUSHROOM_SOUP)});
        }
        player.getInventory().setItem(13, new ItemBuilder().amount(64).type(Material.BOWL).build());
        player.getInventory().setItem(14, new ItemBuilder().amount(64).type(Material.RED_MUSHROOM).build());
        player.getInventory().setItem(15, new ItemBuilder().amount(64).type(Material.BROWN_MUSHROOM).build());
    }

    private void handlePlayer(Player player) {
        this.resetPlayer(player);
        player.getInventory().setItem(0, SELECT_PRIMARY.getItemStack());
        player.getInventory().setItem(1, SELECT_SECONDARY.getItemStack());
    }
}

