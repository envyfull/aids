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

public class ServerActionsInventory
extends MenuInventory {
    public ServerActionsInventory(Player player, ProxiedServer server, MenuInventory backInventory) {
        super(server.getServerId(), 6);
        this.setItem(10, new ItemBuilder().type(Material.PAPER).name("\u00a7aDesligar servidor.").build());
        this.setItem(11, new ItemBuilder().type(Material.PAPER).name("\u00a7aVerificar atualiza\u00e7\u00e3o.").build());
        this.setItem(12, new ItemBuilder().type(Material.PAPER).name(server.isJoinEnabled() ? "\u00a7cDesativar jogadores." : "\u00a7aAtivar jogadores.").build());
        this.setItem(13, new ItemBuilder().type(Material.PAPER).name("\u00a7aExecutar comando.").build());
        this.setItem(14, new ItemBuilder().type(Material.PAPER).name("\u00a7aListar jogadores.").build());
        this.setItem(48, new ItemBuilder().name("\u00a7aVoltar").type(Material.ARROW).lore("\u00a77Voltar para " + backInventory.getTitle()).build(), (p, inv, type, stack, slot) -> backInventory.open(player));
        this.open(player);
    }
}

