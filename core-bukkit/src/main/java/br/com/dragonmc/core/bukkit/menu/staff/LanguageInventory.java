/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 */
package br.com.dragonmc.core.bukkit.menu.staff;

import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.bukkit.utils.helper.SkullHelper;
import br.com.dragonmc.core.bukkit.utils.item.ItemBuilder;
import br.com.dragonmc.core.bukkit.utils.menu.MenuInventory;
import br.com.dragonmc.core.bukkit.utils.menu.click.ClickType;
import br.com.dragonmc.core.bukkit.utils.menu.click.MenuClickHandler;
import br.com.dragonmc.core.common.language.Language;
import br.com.dragonmc.core.common.member.Member;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class LanguageInventory {
    public LanguageInventory(final Player player) {
        Member member = CommonPlugin.getInstance().getMemberManager().getMember(player.getUniqueId());
        MenuInventory menuInventory = new MenuInventory("\u00a77\u00a7%staff.inventory-language%\u00a7", 3);
        for (int i = 1; i <= Language.values().length; ++i) {
            final Language language = Language.values()[i - 1];
            menuInventory.setItem(9 + i, new ItemBuilder().type(Material.SKULL_ITEM).durability(3).skin(SkullHelper.getLanguageSkin(language), "").name("\u00a7a" + language.getLanguageName()).lore("\u00a77" + member.getLanguage().t("staff.inventory-language-" + language.name().toLowerCase() + "-description", new String[0]) + "\n\n\u00a7e" + member.getLanguage().t("staff.inventory-language-click-to-modify", new String[0])).build(), new MenuClickHandler(){

                @Override
                public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                    new TranslationInventory(player, language, 1);
                }
            });
        }
        menuInventory.open(player);
    }
}

