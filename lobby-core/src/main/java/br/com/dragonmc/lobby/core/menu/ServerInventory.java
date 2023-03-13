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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import br.com.dragonmc.core.common.backend.data.DataServerMessage;
import br.com.dragonmc.core.bukkit.BukkitCommon;
import br.com.dragonmc.core.bukkit.event.server.ServerEvent;
import br.com.dragonmc.core.bukkit.utils.item.ItemBuilder;
import br.com.dragonmc.core.bukkit.utils.menu.MenuInventory;
import br.com.dragonmc.core.common.server.ServerType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class ServerInventory {
    private static final MenuInventory MENU_INVENTORY = new MenuInventory("\u00a77\u00a7nEscolha um Modo de Jogo", 3);
    private static final Map<String, List<ServerType>> SERVER_MAP = new HashMap<String, List<ServerType>>();

    public ServerInventory(Player player) {
        MENU_INVENTORY.open(player);
    }

    static {
        SERVER_MAP.put("bedwars", Arrays.asList(ServerType.values()).stream().filter(type -> type.name().contains("BW")).collect(Collectors.toList()));
        SERVER_MAP.put("skywars", Arrays.asList(ServerType.values()).stream().filter(type -> type.name().contains("SW")).collect(Collectors.toList()));
        SERVER_MAP.put("pvp", Arrays.asList(ServerType.ARENA, ServerType.LAVA, ServerType.FPS, ServerType.PVP_LOBBY));
        SERVER_MAP.put("hardcoregames", Arrays.asList(ServerType.HG, ServerType.HG_LOBBY));
        SERVER_MAP.put("rankup", Arrays.asList(ServerType.RANKUP));
        Bukkit.getPluginManager().registerEvents(new Listener(){

            @EventHandler
            public void onServer(ServerEvent event) {
                if (event.getAction() == DataServerMessage.Action.JOIN_ENABLE) {
                    return;
                }
                ServerType serverType = event.getServerType();
                String name = null;
                ArrayList<ServerType> types = new ArrayList<ServerType>();
                if (serverType.name().contains("BW")) {
                    name = "bedwars";
                } else if (serverType.name().contains("SW")) {
                    name = "skywars";
                } else if (serverType.isPvP()) {
                    name = "pvp";
                } else if (serverType.isHG()) {
                    name = "hardcoregames";
                } else if (serverType == ServerType.RANKUP) {
                    name = "rankup";
                }
                if (name != null) {
                    types.addAll((Collection)SERVER_MAP.get(name));
                    ItemStack itemStack = null;
                    for (int i = 11; i < MENU_INVENTORY.getInventory().getContents().length; ++i) {
                        ItemStack item = MENU_INVENTORY.getInventory().getContents()[i];
                        if (item == null || !item.hasItemMeta() || !item.getItemMeta().getDisplayName().toLowerCase().contains(name.toLowerCase())) continue;
                        itemStack = item;
                        break;
                    }
                    if (itemStack == null) {
                        return;
                    }
                    ItemBuilder itemBuilder = ItemBuilder.fromStack(itemStack);
                    itemStack.setItemMeta(itemBuilder.clearLore().lore("\u00a77" + BukkitCommon.getInstance().getServerManager().getTotalNumber(types) + " jogando.").build().getItemMeta());
                }
            }
        }, (Plugin)BukkitCommon.getInstance());
        MENU_INVENTORY.setItem(11, new ItemBuilder().name("\u00a7aPvP").type(Material.IRON_CHESTPLATE).lore("\u00a77" + BukkitCommon.getInstance().getServerManager().getTotalNumber(SERVER_MAP.get("pvp")) + " jogando.").build(), (p, inv, type, stack, slot) -> BukkitCommon.getInstance().sendPlayerToServer(p, ServerType.PVP_LOBBY));
        MENU_INVENTORY.setItem(12, new ItemBuilder().name("\u00a7aHardcoreGames").type(Material.MUSHROOM_SOUP).lore("\u00a77" + BukkitCommon.getInstance().getServerManager().getTotalNumber(SERVER_MAP.get("hardcoregames")) + " jogando.").build(), (p, inv, type, stack, slot) -> {
            BukkitCommon.getInstance().sendPlayerToServer(p, ServerType.HG_LOBBY);
            p.closeInventory();
        });
        MENU_INVENTORY.setItem(13, new ItemBuilder().name("\u00a7aSkyWars").type(Material.EYE_OF_ENDER).lore("\u00a77" + BukkitCommon.getInstance().getServerManager().getTotalNumber(SERVER_MAP.get("skywars")) + " jogando.").build(), (p, inv, type, stack, slot) -> {
            BukkitCommon.getInstance().sendPlayerToServer(p, ServerType.SW_LOBBY);
            p.closeInventory();
        });
        MENU_INVENTORY.setItem(14, new ItemBuilder().name("\u00a7aBedWars").type(Material.BED).lore("\u00a77" + BukkitCommon.getInstance().getServerManager().getTotalNumber(SERVER_MAP.get("bedwars")) + " jogando.").build(), (p, inv, type, stack, slot) -> {
            BukkitCommon.getInstance().sendPlayerToServer(p, ServerType.BW_LOBBY);
            p.closeInventory();
        });
        MENU_INVENTORY.setItem(15, new ItemBuilder().name("\u00a7aRankUp").type(Material.DIAMOND_PICKAXE).lore("\u00a77" + BukkitCommon.getInstance().getServerManager().getTotalNumber(SERVER_MAP.get("rankup")) + " jogando.").build(), (p, inv, type, stack, slot) -> new RankupInventory(p));
    }
}

