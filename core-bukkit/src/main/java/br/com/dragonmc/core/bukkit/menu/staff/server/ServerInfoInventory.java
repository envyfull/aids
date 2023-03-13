/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.core.bukkit.menu.staff.server;

import br.com.dragonmc.core.bukkit.utils.item.ItemBuilder;
import br.com.dragonmc.core.bukkit.utils.menu.MenuInventory;
import br.com.dragonmc.core.common.server.loadbalancer.server.ProxiedServer;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ServerInfoInventory
extends MenuInventory {
    public ServerInfoInventory(Player player, ProxiedServer server, MenuInventory backInventory) {
        super("\u00a77" + server.getServerId(), 3);
        this.setItem(10, new ItemBuilder().name("\u00a7a" + server.getServerId()).type(Material.BOOK).build());
        this.setItem(11, new ItemBuilder().name("\u00a7aAlterar detalhes").type(Material.PAPER).build(), (p, inv, type, stack, slot) -> new ServerDetailsInventory(player, server, this));
        this.setItem(12, new ItemBuilder().name("\u00a7aExecutar a\u00e7\u00e3o").type(Material.IRON_CHESTPLATE).build(), (p, inv, type, stack, slot) -> new ServerActionsInventory(player, server, this));
        this.setItem(13, new ItemBuilder().name("\u00a7aListar jogadores").type(Material.NAME_TAG).build(), (p, inv, type, stack, slot) -> new ServerPlayerListInventory(player, server, 1, this));
        this.setItem(16, new ItemBuilder().name("\u00a7aVoltar").lore("\u00a77Voltar para " + backInventory.getTitle()).type(Material.ARROW).build(), (p, inv, type, stack, slot) -> backInventory.open(player));
        this.open(player);
    }
}

