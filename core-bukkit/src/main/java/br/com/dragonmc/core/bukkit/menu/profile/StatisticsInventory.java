/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.core.bukkit.menu.profile;

import java.util.Arrays;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.bukkit.BukkitCommon;
import br.com.dragonmc.core.bukkit.utils.item.ItemBuilder;
import br.com.dragonmc.core.bukkit.utils.menu.MenuInventory;
import br.com.dragonmc.core.common.member.status.Status;
import br.com.dragonmc.core.common.member.status.StatusType;
import br.com.dragonmc.core.common.member.status.types.BedwarsCategory;
import br.com.dragonmc.core.common.server.ServerType;
import br.com.dragonmc.core.common.utils.string.StringFormat;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class StatisticsInventory {
    public StatisticsInventory(Player player, StatusType statusType) {
        MenuInventory menuInventory = new MenuInventory("\u00a77Suas estat\u00edsticas", 3);
        if (statusType == null) {
            menuInventory.setItem(10, new ItemBuilder().type(Material.BED).name("\u00a7aBedwars").lore("\u00a77Suas estat\u00edsticas no Bedwars.").build(), (p, inv, t, stack, s) -> new StatisticsInventory(player, StatusType.BEDWARS));
            menuInventory.setItem(11, new ItemBuilder().type(Material.IRON_CHESTPLATE).name("\u00a7aPvP").lore("\u00a77Suas estat\u00edsticas no PvP.").build(), (p, inv, t, stack, s) -> new StatisticsInventory(player, StatusType.PVP));
            menuInventory.setItem(22, new ItemBuilder().type(Material.ARROW).name("\u00a7a\u00a7%back%\u00a7").lore("\u00a77Para Suas estat\u00edsticas").build(), (p, inv, type, stack, slot) -> new ProfileInventory(player));
        } else {
            Status status = CommonPlugin.getInstance().getStatusManager().loadStatus(player.getUniqueId(), statusType);
            switch (statusType) {
                case BEDWARS: {
                    menuInventory.setRows(5);
                    menuInventory.setItem(4, new ItemBuilder().name("\u00a7aBedwars Geral").type(Material.PAPER).lore("\u00a7fN\u00edvel: \u00a77" + BukkitCommon.getInstance().getColorByLevel(status.getInteger(BedwarsCategory.BEDWARS_LEVEL)), "", "\u00a7fPartidas: \u00a77" + status.getInteger(BedwarsCategory.BEDWARS_MATCH), "", "\u00a7fCamas quebradas: \u00a77" + status.getInteger(BedwarsCategory.BEDWARS_BED_BREAK), "\u00a7fCamas perdidas: \u00a77" + status.getInteger(BedwarsCategory.BEDWARS_BED_BROKEN), "", "\u00a7fKills: \u00a77" + status.getInteger(BedwarsCategory.BEDWARS_KILLS), "\u00a7fKills finais: \u00a77" + status.getInteger(BedwarsCategory.BEDWARS_FINAL_KILLS), "\u00a7fMortes: \u00a77" + status.getInteger(BedwarsCategory.BEDWARS_DEATHS), "\u00a7fMortes finais: \u00a77" + status.getInteger(BedwarsCategory.BEDWARS_FINAL_DEATHS), "", "\u00a7fWins: \u00a77" + status.getInteger(BedwarsCategory.BEDWARS_WINS), "\u00a7fWinstreak: \u00a77" + status.getInteger(BedwarsCategory.BEDWARS_WINSTREAK), "\u00a7fDerrotas: \u00a77" + status.getInteger(BedwarsCategory.BEDWARS_LOSES)).build());
                    int w = 10;
                    for (ServerType serverType : Arrays.asList(ServerType.values())) {
                        if (!serverType.name().contains("BW") || serverType.isLobby()) continue;
                        menuInventory.setItem(w, new ItemBuilder().type(Material.BED).name("\u00a7aBedwars " + StringFormat.formatString(serverType.name().split("_")[1])).lore("\u00a7fPartidas: \u00a77" + status.getInteger(BedwarsCategory.BEDWARS_MATCH.getSpecialServer(serverType)), "", "\u00a7fCamas quebradas: \u00a77" + status.getInteger(BedwarsCategory.BEDWARS_BED_BREAK.getSpecialServer(serverType)), "\u00a7fCamas perdidas: \u00a77" + status.getInteger(BedwarsCategory.BEDWARS_BED_BROKEN.getSpecialServer(serverType)), "", "\u00a7fKills: \u00a77" + status.getInteger(BedwarsCategory.BEDWARS_KILLS.getSpecialServer(serverType)), "\u00a7fKills finais: \u00a77" + status.getInteger(BedwarsCategory.BEDWARS_FINAL_KILLS.getSpecialServer(serverType)), "\u00a7fMortes: \u00a77" + status.getInteger(BedwarsCategory.BEDWARS_DEATHS.getSpecialServer(serverType)), "\u00a7fMortes finais: \u00a77" + status.getInteger(BedwarsCategory.BEDWARS_FINAL_DEATHS.getSpecialServer(serverType)), "", "\u00a7fWins: \u00a77" + status.getInteger(BedwarsCategory.BEDWARS_WINS.getSpecialServer(serverType)), "\u00a7fWinstreak: \u00a77" + status.getInteger(BedwarsCategory.BEDWARS_WINSTREAK.getSpecialServer(serverType)), "\u00a7fDerrotas: \u00a77" + status.getInteger(BedwarsCategory.BEDWARS_LOSES.getSpecialServer(serverType))).build());
                        if (w % 9 == 7) {
                            w += 12;
                            continue;
                        }
                        w += 2;
                    }
                    break;
                }
                case PVP: {
                    menuInventory.setItem(10, new ItemBuilder().type(Material.PAPER).name("\u00a7aGeral").lore("\u00a7fKills: \u00a77" + status.getInteger("kills", 0), "\u00a7fDeaths: \u00a77" + status.getInteger("deaths", 0), "\u00a7fKillstreak: \u00a77" + status.getInteger("killstreak", 0), "\u00a7fKillstream m\u00e1ximo: \u00a77" + status.getInteger("killstreak-max", 0)).build());
                    menuInventory.setItem(11, new ItemBuilder().type(Material.IRON_CHESTPLATE).name("\u00a7aArena").lore("\u00a7fKills: \u00a770", "\u00a7fDeaths: \u00a770", "\u00a7fKillstreak: \u00a770", "\u00a7fKillstream m\u00e1ximo: \u00a770", "").build());
                    menuInventory.setItem(12, new ItemBuilder().type(Material.GLASS).name("\u00a7aFps").lore("\u00a7fKills: \u00a77" + status.getInteger("fps-kills", 0), "\u00a7fDeaths: \u00a77" + status.getInteger("fps-deaths", 0), "\u00a7fKillstreak: \u00a77" + status.getInteger("fps-killstreak", 0), "\u00a7fKillstream m\u00e1ximo: \u00a77" + status.getInteger("fps-killstreak-max", 0)).build());
                    menuInventory.setItem(13, new ItemBuilder().type(Material.LAVA_BUCKET).name("\u00a7aLava").lore("").build());
                    break;
                }
            }
            menuInventory.setItem((menuInventory.getRows() - 1) * 9 + 4, new ItemBuilder().type(Material.ARROW).name("\u00a7a\u00a7%back%\u00a7").lore("\u00a77Para Suas estat\u00edsticas").build(), (p, inv, type, stack, slot) -> new StatisticsInventory(player, null));
        }
        menuInventory.open(player);
    }
}

