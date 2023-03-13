/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.core.bukkit.menu.staff.punish;

import java.util.ArrayList;
import java.util.stream.Collectors;
import br.com.dragonmc.core.common.CommonConst;
import br.com.dragonmc.core.bukkit.utils.item.ItemBuilder;
import br.com.dragonmc.core.bukkit.utils.menu.MenuInventory;
import br.com.dragonmc.core.bukkit.utils.menu.MenuItem;
import br.com.dragonmc.core.bukkit.utils.menu.click.ClickType;
import br.com.dragonmc.core.common.language.Language;
import br.com.dragonmc.core.common.member.Member;
import br.com.dragonmc.core.common.punish.Punish;
import br.com.dragonmc.core.common.punish.PunishType;
import br.com.dragonmc.core.common.utils.DateUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PunishInfoListInventory
extends MenuInventory {
    private Player player;
    private Member target;
    private PunishType punishType;
    private int page;
    private PunishOrdenator ordenator = PunishOrdenator.ALPHABETIC;
    private boolean asc = true;
    private long wait;
    private MenuInventory backInventory;

    public PunishInfoListInventory(Player player, Member target, PunishType punishType, int page, MenuInventory backInventory) {
        super("\u00a77Listando " + punishType.name().toLowerCase() + "s", 5);
        this.player = player;
        this.target = target;
        this.punishType = punishType;
        this.page = page;
        this.backInventory = backInventory;
        this.handleItems();
        this.open(player);
    }

    private void handleItems() {
        ArrayList<MenuItem> items = new ArrayList<MenuItem>();
        for (Punish punish : this.target.getPunishConfiguration().getPunish(this.punishType).stream().sorted((o1, o2) -> this.ordenator.compare(o1, o2) * (this.asc ? 1 : -1)).collect(Collectors.toList())) {
            items.add(new MenuItem(new ItemBuilder().name("\u00a7a" + punish.getPunisherName()).lore("\u00a7fAutor: \u00a77" + punish.getPunisherName() + "\n\u00a7fMotivo: \u00a77" + punish.getPunishReason() + "\n\u00a7fCriado \u00e0s: \u00a77" + CommonConst.DATE_FORMAT.format(punish.getCreatedAt()) + "\n" + (punish.getPunishType() == PunishType.KICK ? "" : (punish.isPermanent() ? "\u00a7cEssa puni\u00e7\u00e3o n\u00e3o tem prazo de expira\u00e7\u00e3o." : "\u00a7fExpira em: \u00a77" + DateUtils.formatDifference(Language.getLanguage(this.player.getUniqueId()), punish.getExpireAt() / 1000L)))).type(Material.SKULL_ITEM).durability(3).skin(punish.getPunisherName()).build(), (p, inv, type, stack, s) -> p.sendMessage("\u00a7eEm breve op\u00e7\u00f5es de intera\u00e7\u00e3o.")));
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
        for (int i = pageStart; i < pageEnd; ++i) {
            MenuItem item2 = (MenuItem)items.get(i);
            this.setItem(item2, w);
            if (w % 9 == 7) {
                w += 3;
                continue;
            }
            ++w;
        }
        this.setItem(40, new ItemBuilder().name("\u00a7a\u00a7%punish.order." + this.ordenator.name().toLowerCase().replace("_", "-") + "-name%\u00a7").type(Material.ITEM_FRAME).lore("\u00a77\u00a7%punish.order." + this.ordenator.name().toLowerCase().replace("_", "-") + "-description%\u00a7", this.asc ? "\u00a77Ordem crescente." : "\u00a77Ordem decrescente.").build(), (p, inv, type, stack, s) -> {
            if (this.wait > System.currentTimeMillis()) {
                p.sendMessage("\u00a7cAguarde para mudar a ordena\u00e7\u00e3o novamente.");
                return;
            }
            this.wait = System.currentTimeMillis() + 500L;
            if (type == ClickType.RIGHT || type == ClickType.SHIFT) {
                this.asc = !this.asc;
            } else {
                this.ordenator = PunishOrdenator.values()[this.ordenator.ordinal() == PunishOrdenator.values().length - 1 ? 0 : this.ordenator.ordinal() + 1];
            }
            this.handleItems();
        });
        if (this.page == 1) {
            if (this.backInventory == null) {
                this.removeItem(39);
            } else {
                this.setItem(new MenuItem(new ItemBuilder().type(Material.ARROW).name("\u00a7a\u00a7%back%\u00a7").lore("\u00a77Voltar para " + this.backInventory.getTitle()).build(), (p, inv, type, stack, s) -> this.backInventory.open(p)), 39);
            }
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
}

