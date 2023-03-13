/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.core.bukkit.menu.profile;

import java.util.Date;

import br.com.dragonmc.core.bukkit.menu.LanguageInventory;
import br.com.dragonmc.core.common.CommonConst;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.bukkit.utils.item.ItemBuilder;
import br.com.dragonmc.core.bukkit.utils.menu.MenuInventory;
import br.com.dragonmc.core.bukkit.utils.menu.MenuUpdateHandler;
import br.com.dragonmc.core.common.language.Language;
import br.com.dragonmc.core.common.member.Member;
import br.com.dragonmc.core.common.utils.DateUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ProfileInventory {
    public ProfileInventory(Player player) {
        final Member member = CommonPlugin.getInstance().getMemberManager().getMember(player.getUniqueId());
        MenuInventory menuInventory = new MenuInventory(member.getLanguage().t("inventory-profile", "%player%", member.getPlayerName()), 5);
        this.updateProfileItem(menuInventory, member);
        menuInventory.setItem(29, new ItemBuilder().name("\u00a7a\u00a7%inventory-profile-your-stats%\u00a7").lore("\u00a77\u00a7%inventory-profile-your-stats-description%\u00a7").type(Material.PAPER).build(), (p, inv, type, stack, slot) -> new StatisticsInventory(player, null));
        menuInventory.setItem(30, new ItemBuilder().name("\u00a7a\u00a7%inventory-profile-your-medals%\u00a7").lore("\u00a77\u00a7%inventory-profile-your-medals-description%\u00a7").type(Material.NAME_TAG).build(), (p, inv, type, stack, slot) -> {
            p.closeInventory();
            p.performCommand("medals");
        });
        menuInventory.setItem(31, new ItemBuilder().name("\u00a7a\u00a7%inventory-profile-select-language%\u00a7").lore("\u00a77\u00a7%inventory-profile-select-language-description%\u00a7").type(Material.SKULL_ITEM).durability(3).skin("e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTc5ZTIyNDhiZDc5OGViNjFmOTdhZWVlY2MyYWZkZGViYWQ1MmJmNDA1MmM3MjYxYjYxODBhNDU3N2Y4NjkzYSJ9fX0==", "").build(), (p, inv, type, stack, slot) -> new LanguageInventory(player));
        menuInventory.setItem(32, new ItemBuilder().name("\u00a7a\u00a7%inventory-profile-preferences%\u00a7").lore("\u00a77\u00a7%inventory-profile-preferences-description%\u00a7").type(Material.REDSTONE_COMPARATOR).build(), (p, inv, type, stack, slot) -> new PreferencesInventory(player));
        menuInventory.setItem(33, new ItemBuilder().name("\u00a7a\u00a7%inventory-profile-skin%\u00a7").lore("\u00a77\u00a7%inventory-profile-skin-description%\u00a7").type(Material.ITEM_FRAME).build(), (p, inv, type, stack, slot) -> new SkinInventory(player));
        menuInventory.setUpdateHandler(new MenuUpdateHandler(){

            @Override
            public void onUpdate(Player player, MenuInventory menu) {
                if (menu.hasItem(13)) {
                    ProfileInventory.this.updateProfileItem(menu, member);
                }
            }
        });
        menuInventory.open(player);
    }

    private void updateProfileItem(MenuInventory menuInventory, Member member) {
        Language language = member.getLanguage();
        if (member.isOnline()) {
            menuInventory.setItem(13, new ItemBuilder().name("\u00a7a" + member.getPlayerName()).type(Material.SKULL_ITEM).lore("", "\u00a77\u00a7%inventory-profile-first-login%\u00a7: \u00a7f" + CommonConst.DATE_FORMAT.format(new Date(member.getFirstLogin())), "\u00a77\u00a7%inventory-profile-last-login%\u00a7: \u00a7f" + CommonConst.DATE_FORMAT.format(new Date(member.getLastLogin())), "\u00a77\u00a7%inventory-profile-total-logged-time%\u00a7: \u00a7f" + DateUtils.formatDifference(language, member.getOnlineTime() / 1000L), "\u00a77\u00a7%inventory-profile-actual-logged-time%\u00a7: \u00a7f" + DateUtils.formatDifference(language, member.getSessionTime() / 1000L), "", "\u00a7a\u00a7%inventory-profile-user-online%\u00a7").durability(3).skin(member.getPlayerName()).build());
        } else {
            menuInventory.setItem(13, new ItemBuilder().name("\u00a7a" + member.getPlayerName()).type(Material.SKULL_ITEM).lore("", "\u00a77\u00a7%inventory-profile-first-login%\u00a7: \u00a7f" + CommonConst.DATE_FORMAT.format(new Date(member.getFirstLogin())), "\u00a77\u00a7%inventory-profile-last-login%\u00a7: \u00a7f" + CommonConst.DATE_FORMAT.format(new Date(member.getLastLogin())), "\u00a77\u00a7%inventory-profile-total-logged-time%\u00a7: \u00a7f" + DateUtils.formatDifference(language, member.getOnlineTime() / 1000L)).durability(3).skin(member.getPlayerName()).build());
        }
    }
}

