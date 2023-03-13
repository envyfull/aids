/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.core.bukkit.menu.staff.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import br.com.dragonmc.core.bukkit.BukkitCommon;
import br.com.dragonmc.core.bukkit.utils.item.ItemBuilder;
import br.com.dragonmc.core.bukkit.utils.menu.MenuInventory;
import br.com.dragonmc.core.bukkit.utils.menu.MenuItem;
import br.com.dragonmc.core.bukkit.utils.menu.click.ClickType;
import br.com.dragonmc.core.common.language.Language;
import br.com.dragonmc.core.common.server.loadbalancer.server.ProxiedServer;
import br.com.dragonmc.core.common.utils.DateUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ServerListInventory
extends MenuInventory {
    private Player player;
    private int page;
    private List<ProxiedServer> serverList = new ArrayList<ProxiedServer>();
    private boolean loading;
    private ServerOrdenator ordenator = ServerOrdenator.ALPHABETIC;
    private boolean asc;
    private long wait;

    public ServerListInventory(Player player, int page) {
        super("\u00a77Lista de servidores", 5);
        this.player = player;
        this.page = page;
        if (!BukkitCommon.getInstance().isServerLog()) {
            this.loading = true;
        }
        this.handleItems();
        this.open(player);
    }

    private void handleItems() {
        if (this.loading) {
            this.setItem(13, new ItemBuilder().name("\u00a7aCarregando...").type(Material.BARRIER).lore("\u00a77Estamos carregando as informa\u00e7\u00f5es do servidores, aguarde...").build());
            return;
        }
        this.removeItem(13);
        ArrayList<MenuItem> items = new ArrayList<MenuItem>();
        for (ProxiedServer server : this.getServerList().stream().sorted((o1, o2) -> this.ordenator.compare(o1, o2) * (this.asc ? 1 : -1)).collect(Collectors.toList())) {
            items.add(new MenuItem(new ItemBuilder().name("\u00a7a" + server.getServerId()).type(Material.BOOK).lore("", "\u00a7fTipo: \u00a77" + server.getServerType().name(), "", "\u00a7fPlayers: \u00a77" + server.getOnlinePlayers(), "\u00a7fM\u00e1ximo de players: \u00a77" + server.getPlayersRecord(), "\u00a7fPing m\u00e9dio: \u00a770ms", "\u00a7fLigado h\u00e1: \u00a77" + DateUtils.formatDifference(Language.getLanguage(this.player.getUniqueId()), (System.currentTimeMillis() - server.getStartTime()) / 1000L), "", "\u00a7aClique para executar a\u00e7\u00f5es.").build(), (p, inv, type, stack, slot) -> new ServerInfoInventory(this.player, server, this)));
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
        this.setItem(40, new ItemBuilder().name("\u00a7a\u00a7%server.order." + this.ordenator.name().toLowerCase().replace("_", "-") + "-name%\u00a7").type(Material.ITEM_FRAME).lore("\u00a77\u00a7%server.order." + this.ordenator.name().toLowerCase().replace("_", "-") + "-description%\u00a7", this.asc ? "\u00a77Ordem crescente." : "\u00a77Ordem decrescente.").build(), (p, inv, type, stack, s) -> {
            if (this.wait > System.currentTimeMillis()) {
                p.sendMessage("\u00a7cAguarde para mudar a ordena\u00e7\u00e3o novamente.");
                return;
            }
            this.wait = System.currentTimeMillis() + 500L;
            if (type == ClickType.RIGHT || type == ClickType.SHIFT) {
                this.asc = !this.asc;
            } else {
                this.ordenator = ServerOrdenator.values()[this.ordenator.ordinal() == ServerOrdenator.values().length - 1 ? 0 : this.ordenator.ordinal() + 1];
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

    public Collection<ProxiedServer> getServerList() {
        return BukkitCommon.getInstance().isServerLog() ? BukkitCommon.getInstance().getServerManager().getActiveServers().values() : this.serverList;
    }
}

