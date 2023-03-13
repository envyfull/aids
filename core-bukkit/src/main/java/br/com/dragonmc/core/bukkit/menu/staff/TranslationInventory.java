/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.core.bukkit.menu.staff;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.bukkit.utils.item.ItemBuilder;
import br.com.dragonmc.core.bukkit.utils.menu.MenuInventory;
import br.com.dragonmc.core.bukkit.utils.menu.MenuItem;
import br.com.dragonmc.core.common.language.Language;
import br.com.dragonmc.core.common.member.Member;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class TranslationInventory {
    private int itemsPerPage = 21;

    public TranslationInventory(Player player, Language language, int page) {
        MenuInventory menuInventory = new MenuInventory(Member.getLanguage(player.getUniqueId()).t("staff.inventory-translation", "%page%", page + "", "%language%", language.getLanguageName()), 5);
        ArrayList<MenuItem> items = new ArrayList<MenuItem>();
        for (Map.Entry skin : CommonPlugin.getInstance().getPluginInfo().getLanguageMap().get((Object)language).entrySet().stream().sorted((o1, o2) -> ((String)o1.getKey()).compareTo((String)o2.getKey())).collect(Collectors.toList())) {
            items.add(new MenuItem(new ItemBuilder().name("\u00a7a" + (String)skin.getKey()).lore("\n\u00a77" + (String)skin.getValue() + "\n\u00a7a\n\u00a7aClique para alterar.").type(Material.PAPER).build(), (p, inv, type, stack, s) -> {
                p.closeInventory();
                p.sendMessage("teste");
            }));
        }
        int pageStart = 0;
        int pageEnd = this.itemsPerPage;
        if (page > 1) {
            pageStart = (page - 1) * this.itemsPerPage;
            pageEnd = page * this.itemsPerPage;
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
            menuInventory.setItem(39, new ItemBuilder().name("\u00a7a\u00a7%back%\u00a7").type(Material.ARROW).build(), (p, inv, type, stack, slot) -> new LanguageInventory(player));
        } else {
            menuInventory.setItem(new MenuItem(new ItemBuilder().type(Material.ARROW).name("\u00a7a\u00a7%page%\u00a7 " + (page - 1)).build(), (p, inv, type, stack, s) -> new TranslationInventory(player, language, page - 1)), 39);
        }
        if (Math.ceil(items.size() / this.itemsPerPage) + 1.0 > (double)page) {
            menuInventory.setItem(new MenuItem(new ItemBuilder().type(Material.ARROW).name("\u00a7a\u00a7%page%\u00a7 " + (page + 1)).build(), (p, inventory, clickType, item, slot) -> new TranslationInventory(player, language, page + 1)), 41);
        }
        menuInventory.open(player);
    }
}

