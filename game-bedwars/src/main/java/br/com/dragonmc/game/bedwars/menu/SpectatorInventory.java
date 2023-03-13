/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.game.bedwars.menu;

import java.util.List;
import java.util.stream.Collectors;
import br.com.dragonmc.core.common.CommonConst;
import br.com.dragonmc.game.bedwars.GameMain;
import br.com.dragonmc.game.bedwars.gamer.Gamer;
import br.com.dragonmc.game.bedwars.island.Island;
import br.com.dragonmc.core.bukkit.utils.item.ItemBuilder;
import br.com.dragonmc.core.bukkit.utils.menu.MenuInventory;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class SpectatorInventory {
    public SpectatorInventory(Player player) {
        MenuInventory menuInventory = new MenuInventory("\u00a77Espectadores", 5);
        int w = 10;
        List<Gamer> gamerList = GameMain.getInstance().getGamerManager().getGamers(Gamer.class).stream().collect(Collectors.toList());
        for (Gamer gamer : gamerList) {
            Island island = GameMain.getInstance().getIslandManager().getIsland(gamer.getUniqueId());
            if (island == null || island.getIslandStatus() == Island.IslandStatus.LOSER) continue;
            Player playerGamer = gamer.getPlayer();
            menuInventory.setItem(w, new ItemBuilder().name((gamer.isOnline() && gamer.isAlive() ? "\u00a7a" : "\u00a7e") + gamer.getPlayerName()).type(Material.SKULL_ITEM).durability(3).lore("\u00a7fVida: \u00a77" + (!gamer.isOnline() ? "0" : CommonConst.DECIMAL_FORMAT.format(playerGamer.getHealth() / playerGamer.getMaxHealth() * 100.0)) + "%", "\u00a7fTime: " + island.getIslandColor().getColor() + "\u00a7%" + island.getIslandColor().name().toLowerCase() + "-name%\u00a7", "", "\u00a7eClique para teletransportar.").build(), (p, inv, type, stack, slot) -> {
                p.teleport((Entity)playerGamer);
                p.closeInventory();
            });
            if (w % 9 == 7) {
                w += 3;
                continue;
            }
            ++w;
        }
        menuInventory.open(player);
    }
}

