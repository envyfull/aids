/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.core.bukkit.menu.report;

import java.util.Arrays;

import br.com.dragonmc.core.bukkit.utils.item.ItemBuilder;
import br.com.dragonmc.core.bukkit.utils.menu.MenuInventory;
import br.com.dragonmc.core.bukkit.utils.menu.click.ClickType;
import br.com.dragonmc.core.bukkit.utils.menu.confirm.ConfirmInventory;
import br.com.dragonmc.core.common.language.Language;
import br.com.dragonmc.core.common.report.Report;
import br.com.dragonmc.core.common.report.ReportInfo;
import br.com.dragonmc.core.common.utils.DateUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ReportInventory {
    public ReportInventory(Player player, Report report) {
        this(player, report, new ReportListInventory(player, 1));
    }

    public ReportInventory(Player player, Report report, MenuInventory backInventory) {
        MenuInventory menuInventory = new MenuInventory("\u00a77Report " + report.getPlayerName(), 3);
        Language language = Language.getLanguage(player.getUniqueId());
        ReportInfo lastReport = report.getLastReport();
        menuInventory.setItem(10, new ItemBuilder().name("\u00a7a" + report.getPlayerName()).lore(Arrays.asList("", "\u00a7eUltima den\u00fancia:", "\u00a7f  Autor: \u00a77" + lastReport.getPlayerName(), "\u00a7f  Motivo: \u00a77" + lastReport.getReason(), "\u00a7f  Criado h\u00e1: \u00a77" + DateUtils.formatDifference(language, (System.currentTimeMillis() - lastReport.getCreatedAt()) / 1000L), "", "\u00a7fExpira em: \u00a77" + DateUtils.getTime(language, report.getExpiresAt()))).type(Material.SKULL_ITEM).durability(3).skin(report.getPlayerName()).build());
        menuInventory.setItem(11, new ItemBuilder().name("\u00a7eTodas as den\u00fancias").lore("\u00a77Clique para ver todas as den\u00fancias feitas a esse jogador.").type(Material.BOOK).build(), (p, inv, type, stack, slot) -> new ReportInfoInventory(player, report, backInventory, 1));
        menuInventory.setItem(15, new ItemBuilder().name("\u00a7cDeletar report").lore("\u00a77Clique para excluir o report").type(Material.BARRIER).build(), (p, inv, type, stack, slot) -> {
            if (type == ClickType.SHIFT) {
                report.deleteReport();
                backInventory.open(p);
            } else {
                new ConfirmInventory(player, "\u00a77Deletar report " + report.getPlayerName(), confirm -> {
                    if (confirm) {
                        report.deleteReport();
                        backInventory.open(p);
                    }
                }, menuInventory);
            }
        });
        menuInventory.setItem(16, new ItemBuilder().name("\u00a7a\u00a7%back%\u00a7").lore("\u00a77Voltar para " + backInventory.getTitle()).type(Material.ARROW).build(), (p, inv, type, stack, slot) -> backInventory.open(p));
        menuInventory.open(player);
    }
}

