/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package br.com.dragonmc.game.bedwars.menu;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import br.com.dragonmc.game.bedwars.GameMain;
import br.com.dragonmc.game.bedwars.gamer.Gamer;
import br.com.dragonmc.game.bedwars.island.Island;
import br.com.dragonmc.game.engine.GameAPI;
import br.com.dragonmc.core.bukkit.utils.item.ItemBuilder;
import br.com.dragonmc.core.bukkit.utils.menu.MenuInventory;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FinderInventory {
    public FinderInventory(Player player) {
        Island playerIsland = GameMain.getInstance().getIslandManager().getIsland(player.getUniqueId());
        if (playerIsland == null) {
            return;
        }
        Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId(), Gamer.class);
        if (!gamer.isAlive()) {
            return;
        }
        List<Island> islands = GameMain.getInstance().getIslandManager().getIslands().stream().filter(island -> island.getIslandColor() != playerIsland.getIslandColor() && island.getIslandStatus() != Island.IslandStatus.LOSER).collect(Collectors.toList());
        if (islands.stream().filter(island -> island.getIslandStatus() == Island.IslandStatus.ALIVE).count() > 0L) {
            player.sendMessage("\u00a7cVoc\u00ea s\u00f3 poder\u00e1 usar a b\u00fassola quando todas as camas foram quebradas.");
            return;
        }
        MenuInventory menuInventory = new MenuInventory("\u00a77Rastreador", 4);
        int slot = 10;
        for (Island island2 : islands) {
            if (playerIsland.getIslandColor() == island2.getIslandColor() || island2.getIslandStatus() == Island.IslandStatus.LOSER) continue;
            menuInventory.setItem(slot, new ItemBuilder().name(island2.getIslandColor().getColor() + "\u00a7%" + island2.getIslandColor().name().toLowerCase() + "-name%\u00a7").type(Material.WOOL).durability(island2.getIslandColor().getWoolId()).lore("\n\u00a77Clique para ativar o rastreador no time " + island2.getIslandColor().getColor() + "\u00a7%" + island2.getIslandColor().name().toLowerCase() + "-name%\u00a7").build(), (p, inv, type, stack, s) -> {
                int amount = (int)Arrays.asList(player.getInventory().getContents()).stream().filter(itemStack -> itemStack != null && itemStack.getType() == Material.COMPASS && itemStack.getEnchantmentLevel(Enchantment.DURABILITY) == 1).count();
                if (amount >= 1) {
                    player.sendMessage("\u00a7cCompre outro rastreador para poder marcar outro jogador.");
                    return;
                }
                Player nearPlayer = island2.stream(false).sorted((o1, o2) -> (int)(o1.getLocation().distance(p.getLocation()) - o2.getLocation().distance(p.getLocation()))).findFirst().orElse(null);
                if (nearPlayer == null) {
                    p.sendMessage("\u00a7cNenhum jogador deste time para rastrear no momento.");
                    return;
                }
                for (int i = 0; i < p.getInventory().getContents().length; ++i) {
                    ItemStack itemStack2 = p.getInventory().getContents()[i];
                    if (itemStack2 == null || itemStack2.getType() != Material.COMPASS) continue;
                    p.getInventory().setItem(i, ItemBuilder.fromStack(itemStack2).name(island2.getIslandColor().getColor() + "Time \u00a7%" + island2.getIslandColor().name().toLowerCase() + "-name%\u00a7").enchantment(Enchantment.DURABILITY, 1).build());
                    break;
                }
                p.setMetadata("player-target", GameMain.getInstance().createMeta(nearPlayer.getUniqueId().toString()));
            });
            if (slot % 9 == 7) {
                slot += 3;
                continue;
            }
            ++slot;
        }
        menuInventory.open(player);
    }
}

