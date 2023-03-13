/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.Sound
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 */
package br.com.dragonmc.game.bedwars.menu;

import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.game.bedwars.GameMain;
import br.com.dragonmc.game.bedwars.gamer.Gamer;
import br.com.dragonmc.game.bedwars.island.Island;
import br.com.dragonmc.game.bedwars.island.IslandUpgrade;
import br.com.dragonmc.game.engine.GameAPI;
import br.com.dragonmc.core.bukkit.utils.item.ItemBuilder;
import br.com.dragonmc.core.bukkit.utils.menu.MenuInventory;
import br.com.dragonmc.core.bukkit.utils.menu.click.ClickType;
import br.com.dragonmc.core.bukkit.utils.menu.click.MenuClickHandler;
import br.com.dragonmc.core.common.language.Language;
import br.com.dragonmc.core.common.utils.string.StringFormat;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class UpgradeInventory {
    public UpgradeInventory(final Player player) {
        Island island = GameMain.getInstance().getIslandManager().getIsland(player.getUniqueId());
        if (island == null) {
            return;
        }
        Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId(), Gamer.class);
        if (!gamer.isAlive()) {
            return;
        }
        MenuInventory menuInventory = new MenuInventory("\u00a77Loja do Time", 3);
        for (int i = 0; i < IslandUpgrade.values().length; ++i) {
            IslandUpgrade upgrade = IslandUpgrade.values()[i];
            this.handleUpgrade(player, CommonPlugin.getInstance().getPluginInfo().getDefaultLanguage(), island, 10 + i, menuInventory, upgrade);
        }
        menuInventory.open(player);
        new BukkitRunnable(){

            public void run() {
                player.updateInventory();
            }
        }.runTaskLater((Plugin)GameAPI.getInstance(), 1L);
    }

    private void handleUpgrade(final Player player, final Language language, final Island island, int slot, MenuInventory menuInventory, final IslandUpgrade upgrade) {
        boolean enoughDiamonds;
        String lore = "\u00a77" + language.t("inventory-upgrade-" + upgrade.name().toLowerCase().replace("_", "-") + "-description", new String[0]);
        for (int k = 1; k <= upgrade.getMaxLevel(); ++k) {
            lore = lore.replace("%price-" + k + "%", "" + upgrade.getLevelsCost()[k - 1]).replace("%check-" + k + "%", island.getUpgradeLevel(upgrade) < k ? "\u00a7c\u2717" : "\u00a7a\u2713");
        }
        boolean maxLevel = island.getUpgradeLevel(upgrade).intValue() == upgrade.getMaxLevel();
        boolean bl = enoughDiamonds = maxLevel ? true : player.getInventory().contains(Material.DIAMOND, upgrade.getLevelsCost()[Math.min(island.getUpgradeLevel(upgrade), upgrade.getLevelsCost().length - 1)]);
        lore = lore.replace("%buy%", maxLevel ? language.t("inventory-upgrade-max-level-reach-buy", new String[0]) : (player.getInventory().contains(Material.DIAMOND, upgrade.getLevelsCost()[island.getUpgradeLevel(upgrade)]) ? language.t("inventory-upgrade-diamond-enough", new String[0]) : language.t("inventory-upgrade-not-diamond-enough", new String[0])));
        String level = upgrade.getMaxLevel() == 1 ? "" : "" + (maxLevel ? island.getUpgradeLevel(upgrade) : island.getUpgradeLevel(upgrade) + 1);
        menuInventory.setItem(slot, new ItemBuilder().type(upgrade.getIcon()).name((maxLevel ? "\u00a7a" : (enoughDiamonds ? "\u00a7e" : "\u00a7c")) + language.t("inventory-upgrade-" + upgrade.name().toLowerCase().replace("_", "-"), "%level%", level)).lore(lore).build(), new MenuClickHandler(){

            @Override
            public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                if (island.getUpgradeLevel(upgrade).intValue() == upgrade.getMaxLevel()) {
                    player.sendMessage(language.t("inventory-upgrade-max-level-reach", "%upgrade%", StringFormat.formatToCamelCase(upgrade.name().replace("_", " "))));
                    player.playSound(player.getLocation(), Sound.NOTE_BASS, 1.0f, 1.0f);
                } else if (player.getInventory().contains(Material.DIAMOND, upgrade.getLevelsCost()[Math.min(island.getUpgradeLevel(upgrade), upgrade.getLevelsCost().length - 1)])) {
                    player.getInventory().removeItem(new ItemStack[]{new ItemStack(Material.DIAMOND, upgrade.getLevelsCost()[island.getUpgradeLevel(upgrade)])});
                    island.upgrade(player, upgrade);
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
                    new UpgradeInventory(player);
                } else {
                    player.sendMessage("\u00a7%inventory-upgrade-you-doesnt-have-enough-diamond%\u00a7");
                    player.playSound(player.getLocation(), Sound.NOTE_BASS, 1.0f, 1.0f);
                }
            }
        });
    }
}

