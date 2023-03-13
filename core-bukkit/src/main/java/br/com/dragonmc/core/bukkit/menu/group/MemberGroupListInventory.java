/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.core.bukkit.menu.group;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import br.com.dragonmc.core.common.CommonConst;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.bukkit.utils.item.ItemBuilder;
import br.com.dragonmc.core.bukkit.utils.menu.MenuInventory;
import br.com.dragonmc.core.bukkit.utils.menu.MenuItem;
import br.com.dragonmc.core.common.language.Language;
import br.com.dragonmc.core.common.member.Member;
import br.com.dragonmc.core.common.permission.Group;
import br.com.dragonmc.core.common.permission.GroupInfo;
import br.com.dragonmc.core.common.utils.DateUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class MemberGroupListInventory {
    private Player player;
    private Group group;
    private List<MenuItem> items;
    private int page = 1;

    public MemberGroupListInventory(Player player, Group group, List<Member> memberList) {
        this.player = player;
        this.group = group;
        boolean skipMember = group != CommonPlugin.getInstance().getPluginInfo().getDefaultGroup();
        this.items = memberList.stream().sorted((o1, o2) -> o1.getPlayerName().compareTo(o2.getPlayerName())).map(member -> {
            ItemBuilder itemBuilder = new ItemBuilder().name(member.getDefaultTag().getRealPrefix() + member.getPlayerName()).type(Material.SKULL_ITEM).durability(3).skin(member.getName()).lore("");
            for (Map.Entry entry : member.getGroups().entrySet().stream().map(e -> new AbstractMap.SimpleEntry(CommonPlugin.getInstance().getPluginInfo().getGroupByName((String)e.getKey()), e.getValue())).sorted((o1, o2) -> (((Group)o1.getKey()).getId() - ((Group)o2.getKey()).getId()) * -1).collect(Collectors.toList())) {
                if (skipMember && entry.getKey() == CommonPlugin.getInstance().getPluginInfo().getDefaultGroup()) continue;
                itemBuilder.lore("\u00a77Grupo: " + CommonPlugin.getInstance().getPluginInfo().getTagByName(((Group)entry.getKey()).getGroupName()).getTagPrefix(), "\u00a77Desde de: \u00a7f" + CommonConst.DATE_FORMAT.format(((GroupInfo)entry.getValue()).getGivenDate()), "\u00a77Autor: \u00a7f" + ((GroupInfo)entry.getValue()).getAuthorName());
                if (!((GroupInfo)entry.getValue()).isPermanent()) {
                    if (((GroupInfo)entry.getValue()).hasExpired()) {
                        itemBuilder.lore("\u00a7cO cargo expirou.");
                    } else {
                        itemBuilder.lore("\u00a77Expira em: \u00a7f" + DateUtils.getTime(Language.getLanguage(player.getUniqueId()), ((GroupInfo)entry.getValue()).getExpireTime()));
                    }
                }
                itemBuilder.lore("");
            }
            return new MenuItem(itemBuilder.build(), (p, inv, type, stack, slot) -> new MemberGroupInventory(player, (Member)member, group, this.items, this.page));
        }).collect(Collectors.toList());
        this.create();
    }

    public MemberGroupListInventory(Player player, Group group, List<MenuItem> items, int page) {
        this.player = player;
        this.group = group;
        this.items = items;
        this.page = page;
        this.create();
    }

    public void create() {
        MenuInventory menuInventory = new MenuInventory("\u00a77Listando " + this.group.getGroupName() + " (" + this.items.size() + ")", 5);
        int pageStart = 0;
        int pageEnd = 21;
        if (this.page > 1) {
            pageStart = (this.page - 1) * 21;
            pageEnd = this.page * 21;
        }
        if (pageEnd > this.items.size()) {
            pageEnd = this.items.size();
        }
        int w = 10;
        for (int i = pageStart; i < pageEnd; ++i) {
            MenuItem item2 = this.items.get(i);
            menuInventory.setItem(item2, w);
            if (w % 9 == 7) {
                w += 3;
                continue;
            }
            ++w;
        }
        if (this.page != 1) {
            menuInventory.setItem(new MenuItem(new ItemBuilder().type(Material.ARROW).name("\u00a7a\u00a7%page%\u00a7 " + (this.page - 1)).build(), (p, inv, type, stack, s) -> new MemberGroupListInventory(this.player, this.group, this.items, this.page - 1)), 39);
        }
        if (Math.ceil(this.items.size() / 21) + 1.0 > (double)this.page) {
            menuInventory.setItem(new MenuItem(new ItemBuilder().type(Material.ARROW).name("\u00a7a\u00a7%page%\u00a7 " + (this.page + 1)).build(), (p, inventory, clickType, item, slot) -> new MemberGroupListInventory(this.player, this.group, this.items, this.page + 1)), 41);
        }
        menuInventory.open(this.player);
    }
}

