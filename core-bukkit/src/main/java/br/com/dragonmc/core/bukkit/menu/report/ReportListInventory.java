/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.core.bukkit.menu.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.bukkit.utils.item.ItemBuilder;
import br.com.dragonmc.core.bukkit.utils.menu.MenuInventory;
import br.com.dragonmc.core.bukkit.utils.menu.MenuItem;
import br.com.dragonmc.core.bukkit.utils.menu.click.ClickType;
import br.com.dragonmc.core.bukkit.utils.menu.confirm.ConfirmInventory;
import br.com.dragonmc.core.common.language.Language;
import br.com.dragonmc.core.common.packet.types.staff.TeleportToTarget;
import br.com.dragonmc.core.common.report.Report;
import br.com.dragonmc.core.common.report.ReportInfo;
import br.com.dragonmc.core.common.utils.DateUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ReportListInventory
extends MenuInventory {
    private static final int ITEMS_PER_PAGE = 21;
    private Language language;
    private Ordenator ordenator;
    private boolean asc;
    private int page;
    private long wait;

    public ReportListInventory(Player player, Ordenator ordenator, boolean asc, int page) {
        super("\u00a77Reports", 5);
        this.language = Language.getLanguage(player.getUniqueId());
        this.ordenator = ordenator;
        this.asc = asc;
        this.page = page;
        this.handleItems();
        this.setUpdateHandler((p, menu) -> this.handleItems());
        this.open(player);
    }

    public ReportListInventory(Player player, int page) {
        this(player, Ordenator.values()[0], true, page);
    }

    private void handleItems() {
        ArrayList<MenuItem> items = new ArrayList<MenuItem>();
        for (Report report : CommonPlugin.getInstance().getReportManager().getReports().stream().sorted((o1, o2) -> this.ordenator.compare(o1, o2) * (this.asc ? 1 : -1)).collect(Collectors.toList())) {
            if (report.hasExpired()) {
                report.deleteReport();
                break;
            }
            ReportInfo lastReport = report.getLastReport();
            items.add(new MenuItem(new ItemBuilder().name("\u00a7a" + report.getPlayerName()).lore(Arrays.asList("", "\u00a7eUltima den\u00fancia:", "\u00a7f  Autor: \u00a77" + lastReport.getPlayerName(), "\u00a7f  Motivo: \u00a77" + lastReport.getReason(), "\u00a7f  Criado h\u00e1: \u00a77" + DateUtils.formatDifference(this.language, (System.currentTimeMillis() - lastReport.getCreatedAt()) / 1000L), "", "\u00a7fExpira em: \u00a77" + DateUtils.getTime(this.language, report.getExpiresAt()), report.isOnline() ? "\u00a7aO jogador est\u00e1 online no momento." : "")).type(Material.SKULL_ITEM).durability(3).skin(report.getPlayerName()).build(), (p, inv, type, stack, s) -> {
                if (type == ClickType.RIGHT) {
                    new ConfirmInventory(p, "\u00a77Deletar report " + report.getPlayerName(), confirm -> {
                        if (confirm) {
                            report.deleteReport();
                            new ReportListInventory(p, this.page);
                        }
                    }, this);
                    return;
                }
                if (report.isOnline() ? type == ClickType.LEFT : type == ClickType.SHIFT) {
                    CommonPlugin.getInstance().getServerData().sendPacket(new TeleportToTarget(p.getUniqueId(), report.getReportId(), report.getPlayerName()));
                } else {
                    new ReportInventory(p, report, this);
                }
            }));
        }
        int pageStart = 0;
        int pageEnd = 21;
        if (this.page > 1) {
            pageStart = (this.page - 1) * 21;
            pageEnd = this.page * 21;
        }
        if (pageEnd > items.size()) {
            pageEnd = items.size();
        }
        int w = 10;
        for (int i = pageStart; i < this.page * 21; ++i) {
            if (i < pageEnd) {
                MenuItem item2 = (MenuItem)items.get(i);
                this.setItem(item2, w);
            } else {
                this.removeItem(w);
            }
            if (w % 9 == 7) {
                w += 3;
                continue;
            }
            ++w;
        }
        this.setItem(40, new ItemBuilder().name("\u00a7a\u00a7%report.order." + this.ordenator.name().toLowerCase().replace("_", "-") + "-name%\u00a7").type(Material.ITEM_FRAME).lore("\u00a77\u00a7%report.order." + this.ordenator.name().toLowerCase().replace("_", "-") + "-description%\u00a7", this.asc ? "\u00a77Ordem crescente." : "\u00a77Ordem decrescente.").build(), (p, inv, type, stack, s) -> {
            if (this.wait > System.currentTimeMillis()) {
                p.sendMessage("\u00a7cAguarde para mudar a ordena\u00e7\u00e3o novamente.");
                return;
            }
            this.wait = System.currentTimeMillis() + 500L;
            if (type == ClickType.RIGHT || type == ClickType.SHIFT) {
                this.asc = !this.asc;
            } else {
                this.ordenator = Ordenator.values()[this.ordenator.ordinal() == Ordenator.values().length - 1 ? 0 : this.ordenator.ordinal() + 1];
            }
            this.handleItems();
        });
        if (this.page == 1) {
            this.removeItem(39);
        } else {
            this.setItem(new MenuItem(new ItemBuilder().type(Material.ARROW).name("\u00a7a\u00a7%page%\u00a7 " + (this.page - 1)).build(), (p, inv, type, stack, s) -> {
                --this.page;
                this.handleItems();
            }), 39);
        }
        if (Math.ceil(items.size() / 21) + 1.0 > (double)this.page) {
            this.setItem(new MenuItem(new ItemBuilder().type(Material.ARROW).name("\u00a7a\u00a7%page%\u00a7 " + (this.page + 1)).build(), (p, inventory, clickType, item, slot) -> {
                ++this.page;
                this.handleItems();
            }), 41);
        } else {
            this.removeItem(41);
        }
    }

    public static enum Ordenator implements Comparator<Report>
    {
        ALPHABETIC{

            @Override
            public int compare(Report o1, Report o2) {
                return o1.getPlayerName().compareTo(o2.getPlayerName());
            }
        }
        ,
        EXPIRE_TIME{

            @Override
            public int compare(Report o1, Report o2) {
                return Long.compare(o1.getExpiresAt(), o2.getExpiresAt()) * -1;
            }
        }
        ,
        CREATION_TIME{

            @Override
            public int compare(Report o1, Report o2) {
                return Long.compare(o1.getCreatedAt(), o2.getCreatedAt());
            }
        }
        ,
        ONLINE{

            @Override
            public int compare(Report o1, Report o2) {
                return Boolean.compare(o1.isOnline(), o2.isOnline());
            }
        };

    }
}

