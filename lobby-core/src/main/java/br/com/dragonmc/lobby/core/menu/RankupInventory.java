/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.lobby.core.menu;

import java.util.HashSet;
import java.util.UUID;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.common.backend.data.DataServerMessage;
import br.com.dragonmc.core.bukkit.BukkitCommon;
import br.com.dragonmc.lobby.core.CoreMain;
import br.com.dragonmc.lobby.core.server.ServerWatcher;
import br.com.dragonmc.core.bukkit.utils.item.ItemBuilder;
import br.com.dragonmc.core.bukkit.utils.menu.MenuInventory;
import br.com.dragonmc.core.common.server.ServerType;
import br.com.dragonmc.core.common.server.loadbalancer.server.ProxiedServer;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class RankupInventory {
    private static final MenuInventory MENU_INVENTORY = new MenuInventory("\u00a77Rankup", 3);

    public RankupInventory(Player player) {
        MENU_INVENTORY.open(player);
    }

    private static void createRankup(RankupType rankupType, ProxiedServer server, DataServerMessage.Action action) {
        int slot = 11 + rankupType.ordinal() * 2;
        ItemBuilder itemBuilder = new ItemBuilder();
        itemBuilder.name("\u00a7%server.selector.rankup-server." + rankupType.name().toLowerCase() + "-name%\u00a7");
        itemBuilder.type(rankupType.getType());
        itemBuilder.lore("", "\u00a77\u00a7%server.selector.rankup-server." + rankupType.name().toLowerCase() + "-description%\u00a7", "");
        switch (action) {
            case STOP: {
                itemBuilder.lore("\u00a7cO servidor est\u00e1 indispon\u00edvel no momento.");
                break;
            }
            case JOIN_ENABLE: {
                if (!server.isJoinEnabled()) {
                    itemBuilder.lore("\u00a7cO servidor est\u00e1 em manuten\u00e7\u00e3o.");
                    break;
                }
            }
            default: {
                itemBuilder.lore("\u00a77" + server.getOnlinePlayers() + " jogando.");
            }
        }
        MENU_INVENTORY.setItem(slot, itemBuilder.build(), (p, inv, type, stack, s) -> {
            if (server == null) {
                p.sendMessage("\u00a7cO servidor n\u00e3o est\u00e1 dispon\u00edvel no momento.");
                return;
            }
            if (!server.canBeSelected()) {
                if (server.isFull() && !p.hasPermission("server.full")) {
                    p.sendMessage("\u00a7cO servidor est\u00e1 cheio.");
                    return;
                }
                if (!server.isJoinEnabled() && !p.hasPermission("command.admin")) {
                    p.sendMessage("\u00a7cO servidor est\u00e1 em manuten\u00e7\u00e3o, estamos trabalhando para sua divers\u00e3o.");
                    return;
                }
            }
            BukkitCommon.getInstance().sendPlayerToServer(p, server.getServerId());
        });
    }

    static {
        CoreMain.getInstance().getServerWatcherManager().watch(new ServerWatcher(){

            @Override
            public void onServerUpdate(ProxiedServer server, DataServerMessage<?> data) {
                String serverId = data.getSource();
                RankupType rankupType = RankupType.getByServerId(serverId);
                if (rankupType == null) {
                    CommonPlugin.getInstance().debug("The rankup type " + serverId + " not found " + data.getSource());
                } else {
                    RankupInventory.createRankup(rankupType, server, data.getAction());
                }
            }
        }.server(ServerType.RANKUP));
        for (RankupType rankupType : RankupType.values()) {
            RankupInventory.createRankup(rankupType, new ProxiedServer(rankupType.name().toLowerCase() + CommonPlugin.getInstance().getPluginInfo().getIp(), ServerType.RANKUP, new HashSet<UUID>(), 80, false), DataServerMessage.Action.START);
        }
        CoreMain.getInstance().getServerManager().getBalancer(ServerType.RANKUP).getList().forEach(server -> {
            RankupType rankupType = RankupType.getByServerId(server.getServerId());
            if (rankupType == null) {
                CommonPlugin.getInstance().debug("The rankup type " + (Object)((Object)rankupType) + " not found");
            } else {
                RankupInventory.createRankup(rankupType, server, DataServerMessage.Action.START);
            }
        });
    }

    public static enum RankupType {
        MITOLOGIC(Material.IRON_PICKAXE),
        ATLANTIC(Material.DIAMOND_PICKAXE),
        DARKNESS(Material.GOLD_PICKAXE);

        private Material type;

        public static RankupType getByServerId(String serverId) {
            for (RankupType type : RankupType.values()) {
                if (!serverId.toLowerCase().contains(type.name().toLowerCase())) continue;
                return type;
            }
            return null;
        }

        private RankupType(Material type) {
            this.type = type;
        }

        public Material getType() {
            return this.type;
        }
    }
}

