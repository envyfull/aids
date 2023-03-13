/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.Sound
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 */
package br.com.dragonmc.lobby.login.captcha.impl;

import br.com.dragonmc.core.common.CommonConst;
import br.com.dragonmc.lobby.login.captcha.Captcha;
import br.com.dragonmc.core.bukkit.utils.item.ItemBuilder;
import br.com.dragonmc.core.bukkit.utils.menu.MenuInventory;
import br.com.dragonmc.core.bukkit.utils.menu.click.ClickType;
import br.com.dragonmc.core.bukkit.utils.menu.click.MenuClickHandler;
import br.com.dragonmc.core.common.utils.Callback;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ItemCaptcha
implements Captcha {
    private static final String WRONG_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmQzY2ZjMjM5MDA2YjI1N2I4YjIwZjg1YTdiZjQyMDI2YzRhZGEwODRjMTQ0OGQwNGUwYzQwNmNlOGEyZWEzMSJ9fX0=";
    private static final String RIGHT_HEAD = "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzExYjkyMjVkMTEyOTE2ZDk1ZmM1NGZiN2Q0YjNlM2ZlNGMzMGFiZTAzZmQzMmMwNTdjZWVmYjFlYzk3OTQwYiJ9fX0=";

    @Override
    public void verify(Player player, final Callback<Boolean> callback) {
        final MenuInventory menuInventory = new MenuInventory("", 3);
        menuInventory.setReopenInventory(true);
        ItemStack item = new ItemBuilder().name("\u00a77Cabe\u00e7a errada, escolha outra").type(Material.SKULL_ITEM).durability(3).skin(WRONG_HEAD, "").build();
        MenuClickHandler wrongClick = new MenuClickHandler(){
            int wrongClicks = 0;

            @Override
            public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                if (++this.wrongClicks >= 3) {
                    callback.callback(false);
                    menuInventory.setReopenInventory(false);
                    p.closeInventory();
                }
                p.playSound(p.getLocation(), Sound.ANVIL_BREAK, 0.2f, 1.0f);
            }
        };
        for (int x = 0; x < menuInventory.getInventory().getSize(); ++x) {
            menuInventory.setItem(x, item, wrongClick);
        }
        MenuClickHandler rightClick = new MenuClickHandler(){

            @Override
            public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                callback.callback(true);
                menuInventory.setReopenInventory(false);
                p.closeInventory();
                p.playSound(p.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
            }
        };
        menuInventory.setItem(CommonConst.RANDOM.nextInt(menuInventory.getInventory().getSize()), new ItemBuilder().name("\u00a7aClique para escolher este").type(Material.SKULL_ITEM).durability(3).skin(RIGHT_HEAD, "").build(), rightClick);
        menuInventory.open(player);
    }
}

