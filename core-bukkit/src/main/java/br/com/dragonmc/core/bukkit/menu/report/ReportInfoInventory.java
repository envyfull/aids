/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.core.bukkit.menu.report;

import java.util.ArrayList;

import br.com.dragonmc.core.bukkit.utils.item.ItemBuilder;
import br.com.dragonmc.core.bukkit.utils.menu.MenuInventory;
import br.com.dragonmc.core.bukkit.utils.menu.MenuItem;
import br.com.dragonmc.core.common.language.Language;
import br.com.dragonmc.core.common.report.Report;
import br.com.dragonmc.core.common.report.ReportInfo;
import br.com.dragonmc.core.common.utils.DateUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ReportInfoInventory {
    public ReportInfoInventory(Player player, Report report) {
        this(player, report, new ReportListInventory(player, 1), 1);
    }

    public ReportInfoInventory(Player player, Report report, MenuInventory backInventory, int page) {
        MenuInventory menuInventory = new MenuInventory("\u00a77Report " + report.getPlayerName(), 5);
        Language language = Language.getLanguage(player.getUniqueId());
        ArrayList<MenuItem> items = new ArrayList<MenuItem>();
        for (ReportInfo reportInfo : report.getReportMap().values()) {
            items.add(new MenuItem(new ItemBuilder().name("\u00a7a" + reportInfo.getPlayerName()).lore("", "\u00a7eInforma\u00e7\u00f5es:", "\u00a7f  Autor: \u00a77" + reportInfo.getPlayerName(), "\u00a7f  Motivo: \u00a77" + reportInfo.getReason(), "\u00a7f  Criado h\u00e1: \u00a77" + DateUtils.formatDifference(language, (System.currentTimeMillis() - reportInfo.getCreatedAt()) / 1000L)).type(Material.SKULL_ITEM).durability(3).skin(reportInfo.getPlayerName()).build()));
        }
        int pageStart = 0;
        int pageEnd = 21;
        if (page > 1) {
            pageStart = (page - 1) * 21;
            pageEnd = page * 21;
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
            menuInventory.setItem(new MenuItem(new ItemBuilder().type(Material.ARROW).name("\u00a7aVoltar").build(), (p, inv, type, stack, s) -> new ReportInventory(player, report, backInventory)), 39);
        } else {
            menuInventory.setItem(new MenuItem(new ItemBuilder().type(Material.ARROW).name("\u00a7a\u00a7%page%\u00a7 " + (page - 1)).build(), (p, inv, type, stack, s) -> new ReportInfoInventory(player, report, backInventory, page - 1)), 39);
        }
        if (Math.ceil(items.size() / 21) + 1.0 > (double)page) {
            menuInventory.setItem(new MenuItem(new ItemBuilder().type(Material.ARROW).name("\u00a7a\u00a7%page%\u00a7 " + (page + 1)).build(), (p, inventory, clickType, item, slot) -> new ReportInfoInventory(player, report, backInventory, page + 1)), 41);
        }
        menuInventory.open(player);
    }
}

