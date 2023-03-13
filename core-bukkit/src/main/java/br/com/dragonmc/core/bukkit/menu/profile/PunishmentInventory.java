/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.InventoryType
 */
package br.com.dragonmc.core.bukkit.menu.profile;

import java.util.ArrayList;
import br.com.dragonmc.core.common.CommonConst;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.common.PluginInfo;
import br.com.dragonmc.core.bukkit.utils.item.ItemBuilder;
import br.com.dragonmc.core.bukkit.utils.menu.MenuInventory;
import br.com.dragonmc.core.bukkit.utils.menu.MenuItem;
import br.com.dragonmc.core.common.member.Member;
import br.com.dragonmc.core.common.punish.Punish;
import br.com.dragonmc.core.common.punish.PunishType;
import br.com.dragonmc.core.common.utils.DateUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

public class PunishmentInventory {
    public PunishmentInventory(Player player) {
        Member member = CommonPlugin.getInstance().getMemberManager().getMember(player.getUniqueId());
        MenuInventory menuInventory = new MenuInventory(PluginInfo.t(member, "inventory.punishment.title", "%player%", member.getPlayerName()), InventoryType.HOPPER);
        menuInventory.setItem(0, new ItemBuilder().name("\u00a7%inventory.punishment.item.ban-name%\u00a7").type(Material.PAPER).lore("\u00a7%inventory.punishment.item.ban-description%\u00a7").build(), (p, inv, type, stack, slot) -> new PunishmentInventory(player, PunishType.BAN, 1));
        menuInventory.setItem(1, new ItemBuilder().name("\u00a7%inventory.punishment.item.mute-name%\u00a7").type(Material.PAPER).lore("\u00a7%inventory.punishment.item.mute-description%\u00a7").build(), (p, inv, type, stack, slot) -> new PunishmentInventory(player, PunishType.MUTE, 1));
        menuInventory.setItem(2, new ItemBuilder().name("\u00a7%inventory.punishment.item.kick-name%\u00a7").type(Material.PAPER).lore("\u00a7%inventory.punishment.item.kick-description%\u00a7").build(), (p, inv, type, stack, slot) -> new PunishmentInventory(player, PunishType.KICK, 1));
        menuInventory.setItem(4, new ItemBuilder().name("\u00a7a\u00a7%back%\u00a7").type(Material.ARROW).build(), (p, inv, type, stack, slot) -> new ProfileInventory(player));
        menuInventory.open(player);
    }

    public PunishmentInventory(Player player, Punish punish, int page) {
        Member member = CommonPlugin.getInstance().getMemberManager().getMember(player.getUniqueId());
        MenuInventory menuInventory = new MenuInventory(PluginInfo.t(member, "inventory.punishment.title", "%player%", member.getPlayerName()), 3);
        menuInventory.setItem(10, new ItemBuilder().name("\u00a7a" + punish.getId()).lore(CommonConst.GSON_PRETTY.toJson((Object)punish)).type(Material.PAPER).build());
        menuInventory.setItem(11, new ItemBuilder().name(PluginInfo.t(member, "inventory.punishment.item.reason-name")).lore(PluginInfo.t(member, "inventory.punishment.item.reason-description", "%reason%", punish.getPunishReason())).type(Material.PAPER).build());
        menuInventory.setItem(12, new ItemBuilder().name(PluginInfo.t(member, "inventory.punishment.item.date-name")).lore(PluginInfo.t(member, "inventory.punishment.item.date-description", "%createAt%", CommonConst.DATE_FORMAT.format(punish.getCreatedAt()), "%expireAt%", punish.isPermanent() ? "Never" : CommonConst.DATE_FORMAT.format(punish.getExpireAt()), "%duration%", punish.isPermanent() ? "Permanent" : DateUtils.getTime(member.getLanguage(), punish.getExpireAt()))).type(Material.WATCH).build());
        if (punish.isUnpunished() || punish.hasExpired()) {
            menuInventory.setItem(13, new ItemBuilder().name("\u00a7aOK").type(Material.BARRIER).build());
        }
        menuInventory.setItem(16, new ItemBuilder().name("\u00a7a\u00a7%back%\u00a7").type(Material.ARROW).build(), (p, inv, type, stack, slot) -> new PunishmentInventory(player, punish.getPunishType(), page));
        menuInventory.open(player);
    }

    public PunishmentInventory(Player player, PunishType punishType, int page) {
        Member member = CommonPlugin.getInstance().getMemberManager().getMember(player.getUniqueId());
        MenuInventory menuInventory = new MenuInventory(PluginInfo.t(member, "inventory.punishment.title", "%player%", member.getPlayerName()), 5);
        ArrayList<MenuItem> items = new ArrayList<MenuItem>();
        for (Punish punish : member.getPunishConfiguration().getPunish(punishType)) {
            items.add(new MenuItem(new ItemBuilder().name(PluginInfo.t(member, "inventory.punishment.item.info-name", "%id%", punish.getId().replace("#", ""))).lore(PluginInfo.t(member, "inventory.punishment.item.info-description", "%punisher%", punish.getPunisherName(), "%reason%", punish.getPunishReason(), "%createAt%", CommonConst.DATE_FORMAT.format(punish.getCreatedAt()), "%expireAt%", punish.isPermanent() ? "Never" : CommonConst.DATE_FORMAT.format(punish.getExpireAt()), "%duration%", punish.isPermanent() ? "Permanent" : DateUtils.getTime(member.getLanguage(), punish.getExpireAt()), "%id%", punish.getId().replace("#", "")) + (punish.isUnpunished() ? "\u00a7%inventory.punishment.item.info-description-pardoned%\u00a7" : (punish.hasExpired() ? "\u00a7%inventory.punishment.item.info-description-expired%\u00a7" : ""))).type(Material.PAPER).build(), (p, inv, type, stack, s) -> new PunishmentInventory(player, punish, page)));
        }
        int itemsPerPage = 21;
        int pageStart = 0;
        int pageEnd = itemsPerPage;
        if (page > 1) {
            pageStart = (page - 1) * itemsPerPage;
            pageEnd = page * itemsPerPage;
        }
        if (pageEnd > items.size()) {
            pageEnd = items.size();
        }
        int w = 10;
        for (int i = pageStart; i < pageEnd; ++i) {
            MenuItem item2 = (MenuItem)items.get(i);
            menuInventory.setItem(item2, w);
            if (w % 9 == 7) {
                w += 3;
                continue;
            }
            ++w;
        }
        if (page == 1) {
            menuInventory.setItem(39, new ItemBuilder().name("\u00a7a\u00a7%back%\u00a7").type(Material.ARROW).build(), (p, inv, type, stack, slot) -> new PunishmentInventory(player));
        } else {
            menuInventory.setItem(new MenuItem(new ItemBuilder().type(Material.ARROW).name("\u00a7a\u00a7%page%\u00a7 " + (page - 1)).build(), (p, inv, type, stack, s) -> new PunishmentInventory(player, punishType, page - 1)), 39);
        }
        if (Math.ceil(items.size() / itemsPerPage) + 1.0 > (double)page) {
            menuInventory.setItem(new MenuItem(new ItemBuilder().type(Material.ARROW).name("\u00a7a\u00a7%page%\u00a7 " + (page + 1)).build(), (p, inventory, clickType, item, slot) -> new PunishmentInventory(player, punishType, page + 1)), 41);
        }
        menuInventory.open(player);
    }
}

