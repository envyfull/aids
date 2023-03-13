/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 */
package br.com.dragonmc.lobby.core.menu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.bukkit.BukkitCommon;
import br.com.dragonmc.core.bukkit.event.server.ServerEvent;
import br.com.dragonmc.lobby.core.CoreMain;
import br.com.dragonmc.core.bukkit.utils.item.ItemBuilder;
import br.com.dragonmc.core.bukkit.utils.menu.MenuInventory;
import br.com.dragonmc.core.common.server.loadbalancer.server.ProxiedServer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class LobbyInventory {
    private static final MenuInventory MENU_INVENTORY = new MenuInventory("\u00a77Lobbys", 3);
    private static final Map<String, Integer> SERVER_MAP = new HashMap<String, Integer>();

    public LobbyInventory(Player player) {
        MENU_INVENTORY.open(player);
    }

    static {
        Bukkit.getPluginManager().registerEvents(new Listener(){

            @EventHandler
            public void onServer(ServerEvent event) {
                if (event.getServerType() != CommonPlugin.getInstance().getServerType()) {
                    return;
                }
                switch (event.getAction()) {
                    case STOP: 
                    case START: {
                        MENU_INVENTORY.clear();
                        List serverList = BukkitCommon.getInstance().getServerManager().getBalancer(CommonPlugin.getInstance().getServerType()).getList().stream().sorted((s1, s2) -> s1.getServerId().compareTo(s2.getServerId())).collect(Collectors.toList());
                        for (int i = 0; i < serverList.size(); ++i) {
                            ProxiedServer server = (ProxiedServer)serverList.get(i);
                            int slot = 11 + i;
                            MENU_INVENTORY.setItem(slot, new ItemBuilder().name("\u00a7aLobby #" + CoreMain.getInstance().getServerId(server.getServerId())).lore("\u00a77" + server.getOnlinePlayers() + " jogando.").type(Material.STAINED_GLASS_PANE).durability(5).build(), (player, inventory, clickType, itemStack, index) -> {
                                if (player.hasPermission("lobby.join")) {
                                    BukkitCommon.getInstance().sendPlayerToServer(player, server.getServerId());
                                } else {
                                    player.sendMessage("\u00a7cSomente jogadores pagantes podem transitar livremente entre os lobbies.");
                                }
                            });
                            SERVER_MAP.put(server.getServerId(), slot);
                        }
                        break;
                    }
                    case JOIN: 
                    case LEAVE: {
                        int slot = (Integer)SERVER_MAP.get(event.getServerId());
                        ItemStack itemStack2 = MENU_INVENTORY.getInventory().getItem(slot);
                        ItemBuilder itemBuilder = ItemBuilder.fromStack(itemStack2);
                        itemStack2.setItemMeta(itemBuilder.clearLore().lore("\u00a77" + event.getProxiedServer().getOnlinePlayers() + " jogando.").build().getItemMeta());
                        break;
                    }
                }
            }
        }, (Plugin)BukkitCommon.getInstance());
        List<ProxiedServer> serverList = BukkitCommon.getInstance().getServerManager().getBalancer(CommonPlugin.getInstance().getServerType()).getList();
        for (int i = 0; i < serverList.size(); ++i) {
            ProxiedServer server = serverList.get(i);
            int slot = 11 + i;
            MENU_INVENTORY.setItem(slot, new ItemBuilder().name("\u00a7aLobby #" + (i + 1)).lore("\u00a77" + server.getOnlinePlayers() + " jogando.").type(Material.STAINED_GLASS_PANE).durability(5).build(), (p, inv, type, stack, s) -> BukkitCommon.getInstance().sendPlayerToServer(p, server.getServerId()));
            SERVER_MAP.put(server.getServerId(), slot);
        }
    }
}

