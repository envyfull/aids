/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 */
package br.com.dragonmc.core.bukkit.menu;

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
        final Member member = CommonPlugin.getInstance().getMemberManager().getMember(player.getUniqueId());
        MenuInventory menuInventory = new MenuInventory("\u00a77\u00a7%inventory-language%\u00a7", 3);
        for (int i = 1; i <= Language.values().length; ++i) {
            final Language language = Language.values()[i - 1];
            if (language != Language.PORTUGUESE && !player.hasPermission("command.language")) continue;
            ItemBuilder itemBuilder = new ItemBuilder().type(Material.SKULL_ITEM).durability(3).skin(SkullHelper.getLanguageSkin(language), "").name("\u00a7a" + language.getLanguageName()).lore("\u00a77" + language.t("inventory-language-" + language.name().toLowerCase() + "-description", new String[0]) + "\n\n\u00a7e" + language.t("inventory-language-click-to-change", new String[0]));
            if (member.getLanguage() == language) {
                itemBuilder.glow();
            }
            menuInventory.setItem(9 + i, itemBuilder.build(), new MenuClickHandler(){

                @Override
                public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                    if (member.getLanguage() == language) {
                        player.sendMessage(language.t("inventory-language-already", "%language%", language.getLanguageName()));
                        return;
                    }
                    member.setLanguage(language);
                    member.sendMessage(language.t("inventory-language-changed", "%language%", language.getLanguageName()));
                    new LanguageInventory(player);
                }
            });
        }
        menuInventory.open(player);
    }
}

