package br.com.dragonmc.game.bedwars.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.dragonmc.game.bedwars.GameMain;
import br.com.dragonmc.game.bedwars.gamer.Gamer;
import br.com.dragonmc.game.bedwars.island.Island;
import br.com.dragonmc.game.bedwars.store.ShopCategory;
import br.com.dragonmc.game.bedwars.utils.GamerHelper;
import br.com.dragonmc.game.engine.GameAPI;
import br.com.dragonmc.core.bukkit.utils.item.ItemBuilder;
import br.com.dragonmc.core.bukkit.utils.menu.MenuInventory;
import br.com.dragonmc.core.bukkit.utils.menu.click.ClickType;
import br.com.dragonmc.core.common.language.Language;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

//TODO: Remove all comments
public class StoreInventory {
    public StoreInventory(Player player) {
        /*  31 */
        this(player, ShopCategory.FAVORITES);
    }

    public StoreInventory(final Player player, ShopCategory storeCategory) {
        int w, j;
        /*  35 */
        Island island = GameMain.getInstance().getIslandManager().getIsland(player.getUniqueId());

        /*  37 */
        if (island == null) {
            return;
        }
        /*  40 */
        Gamer gamer = (Gamer) GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId(), Gamer.class);

        /*  42 */
        if (!gamer.isAlive()) {
            return;
        }

        /*  46 */
        MenuInventory menuInventory = new MenuInventory("§7" + storeCategory.getName(), 6);
        int i;
        /*  48 */
        for (i = 0; i < (ShopCategory.values()).length; i++) {
            /*  49 */
            ShopCategory category = ShopCategory.values()[i];
            /*  50 */
            menuInventory.setItem(i, (new ItemBuilder())
/*  51 */.name("§a" + category.getName()).type(category.getMaterial())
/*  52 */.lore((category == storeCategory) ? "" : "§e§%click-to-see%§").build(), (p, inv, type, stack, slot) -> {
                new StoreInventory(player, category);

                player.playSound(player.getLocation(), Sound.CLICK, 1.0F, 1.0F);
            });
        }

        /*  59 */
        for (i = 0; i < 8; i++) {
            /*  60 */
            menuInventory.setItem(9 + i, (new ItemBuilder()).name(" ").type(Material.STAINED_GLASS_PANE)
/*  61 */.durability((storeCategory.ordinal() == i) ? 5 : 15).build());
        }

        /*  64 */
        List<ShopCategory.ShopItem> list = new ArrayList<>(storeCategory.getShopItem());

        /*  66 */
        switch (storeCategory) {
            case FAVORITES:
                /*  68 */
                for (Map.Entry<ShopCategory, Set<Integer>> entry : (Iterable<Map.Entry<ShopCategory, Set<Integer>>>) gamer.getFavoriteMap().entrySet()) {
                    /*  69 */
                    ShopCategory shopCategory = entry.getKey();
                    /*  70 */
                    List<ShopCategory.ShopItem> shopItems = shopCategory.getShopItem();

                    /*  72 */
                    for (Integer integer : entry.getValue()) {
                        /*  73 */
                        if (integer.intValue() >= 0 && integer.intValue() < shopItems.size())
                            /*  74 */ list.add(shopItems.get(integer.intValue()));
                    }
                }
                /*  77 */
                w = 19;

                /*  79 */
                for (j = 0; (storeCategory == ShopCategory.FAVORITES) ? (j < 21) : (j < list.size()); j++) {
                    /*  80 */
                    if (j < list.size()) {
                        /*  81 */
                        ShopCategory.ShopItem item = list.get(j);

                        /*  83 */
                        if (item.getStack().getType() == Material.GOLD_AXE) {
                            /*  84 */
                            item = gamer.getAxeLevel().getNext().getAsShopItem();
                            /*  85 */
                        } else if (item.getStack().getType() == Material.GOLD_PICKAXE) {
                            /*  86 */
                            item = gamer.getPickaxeLevel().getNext().getAsShopItem();
                        }
                        /*  88 */
                        handleItem(menuInventory, gamer, storeCategory, list.indexOf(item), item, w);
                    } else {
                        /*  90 */
                        menuInventory.setItem(w, (new ItemBuilder()).name(" ").type(Material.STAINED_GLASS_PANE)
/*  91 */.durability(15).build());
                    }

                    /*  94 */
                    if (w % 9 == 7) {
                        /*  95 */
                        w += 3;
                    } else {

                        /*  99 */
                        w++;
                    }
                }
                break;

            default:
                /* 105 */
                w = 19;

                /* 107 */
                for (j = 0; j < list.size(); j++) {
                    /* 108 */
                    ShopCategory.ShopItem item = list.get(j);

                    /* 110 */
                    if (item.getStack().getType() == Material.GOLD_AXE) {
                        /* 111 */
                        item = gamer.getAxeLevel().getNext().getAsShopItem();
                        /* 112 */
                    } else if (item.getStack().getType() == Material.GOLD_PICKAXE) {
                        /* 113 */
                        item = gamer.getPickaxeLevel().getNext().getAsShopItem();
                    }
                    /* 115 */
                    handleItem(menuInventory, gamer, storeCategory, list.indexOf(item), item, w);

                    /* 117 */
                    if (w % 9 == 7) {
                        /* 118 */
                        w += 3;
                    } else {

                        /* 122 */
                        w++;
                    }
                }
                break;
        }


        /* 129 */
        menuInventory.open(player);
        /* 130 */
        (new BukkitRunnable() {
            public void run() {
                /* 134 */
                player.updateInventory();
            }
            /* 136 */
        }).runTaskLater((Plugin) GameAPI.getInstance(), 1L);
    }


    public void handleItem(MenuInventory menuInventory, Gamer gamer, ShopCategory storeCategory, int index, ShopCategory.ShopItem shopItem, int slot) {
        /* 141 */
        menuInventory.setItem(slot, createItem(gamer.getPlayer(), shopItem), (p, inv, type, stack, s) -> {
            if (type == ClickType.SHIFT) {
                if (storeCategory == ShopCategory.FAVORITES) {
                    if (gamer.removeFavorite(shopItem)) {
                        p.sendMessage("§aO item " + ChatColor.stripColor(stack.getItemMeta().getDisplayName()) + " foi removido dos favoritos.");
                        new StoreInventory(p, storeCategory);
                    }
                } else {
                    new FavoriteConfigInventory(p, storeCategory, index, shopItem);
                }
            } else {
                buy(p, shopItem);
                new StoreInventory(p, storeCategory);
            }
        });
    }


    private ItemStack createItem(Player player, ShopCategory.ShopItem shopItem) {
        /* 161 */
        Language language = Language.getLanguage(player.getUniqueId());







        /* 169 */
        ItemBuilder itemBuilder = ItemBuilder.fromStack(shopItem.getStack()).name((player.getInventory().contains(shopItem.getPrice().getMaterial(), shopItem.getPrice().getAmount()) ? "§a" : "§c") + (shopItem.getStack().getItemMeta().hasDisplayName() ? shopItem.getStack().getItemMeta().getDisplayName() : language.t(shopItem.getStack().getType().name().toLowerCase().replace("_", "-"), new String[0]))).clearLore().flag(ItemFlag.HIDE_POTION_EFFECTS).lore("§7Preço: §7" + getColor(shopItem.getPrice().getMaterial()) + shopItem.getPrice().getAmount() + " " + language
/* 170 */.t("bedwars.buy." + shopItem
/* 171 */.getPrice().getMaterial().name().toLowerCase().replace("_", "-"), new String[0]));

        /* 173 */
        if (shopItem.getStack().getItemMeta().getLore() != null &&
                /* 174 */       !shopItem.getStack().getItemMeta().getLore().isEmpty()) {
            /* 175 */
            itemBuilder.lore(shopItem.getStack().getItemMeta().getLore());
        }
        /* 177 */
        itemBuilder.lore("").lore(language.t("bedwars.store-inventory." + shopItem
/* 178 */.getStack().getType().name().toLowerCase().replace("_", "-") + ".description", new String[0]));

        /* 180 */
        return itemBuilder.build();
    }

    public ChatColor getColor(Material material) {
        /* 184 */
        return material.name().contains("EMERALD") ? ChatColor.DARK_GREEN : (
                /* 185 */       material.name().contains("GOLD") ? ChatColor.GOLD : (
                /* 186 */       material.name().contains("DIAMOND") ? ChatColor.AQUA : ChatColor.WHITE));
    }

    public void buy(Player player, ShopCategory.ShopItem shopItem) {
        /* 190 */
        if (player.getInventory().contains(shopItem.getPrice().getMaterial(), shopItem.getPrice().getAmount())) {
            /* 191 */
            GamerHelper.buyItem(player, shopItem);
        } else {
            /* 193 */
            player.sendMessage("§cVocê não possui material suficiente.");
            /* 194 */
            player.playSound(player.getLocation(), Sound.NOTE_BASS, 1.0F, 1.0F);
        }
    }
}

