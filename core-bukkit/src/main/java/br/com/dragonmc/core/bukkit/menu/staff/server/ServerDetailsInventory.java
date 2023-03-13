/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.core.bukkit.menu.staff.server;

import br.com.dragonmc.core.bukkit.manager.ChatManager;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.bukkit.BukkitCommon;
import br.com.dragonmc.core.bukkit.utils.item.ItemBuilder;
import br.com.dragonmc.core.bukkit.utils.menu.MenuInventory;
import br.com.dragonmc.core.common.command.CommandSender;
import br.com.dragonmc.core.common.server.loadbalancer.server.ProxiedServer;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ServerDetailsInventory
extends MenuInventory {
    public ServerDetailsInventory(final Player player, final ProxiedServer server, final MenuInventory backInventory) {
        super("\u00a77Detalhes", 3);
        this.setItem(10, new ItemBuilder().name("\u00a7a" + server.getServerId()).type(Material.BOOK).build());
        this.setItem(11, new ItemBuilder().name("\u00a7eAlterar nome").lore("\u00a77Clique para alterar o nome do servidor.").type(Material.BOOK).build(), (p, inv, type, stack, slot) -> {
            p.closeInventory();
            BukkitCommon.getInstance().getChatManager().loadChat((CommandSender)CommonPlugin.getInstance().getMemberManager().getMember(player.getUniqueId()), new ChatManager.Callback(){

                @Override
                public void callback(boolean cancel, String ... asks) {
                    if (cancel) {
                        ServerDetailsInventory.this.open(player);
                    } else {
                        player.sendMessage("\u00a7aNome do servidor alterado de " + server.getServerId() + " para " + asks[0] + ".");
                        new ServerDetailsInventory(player, server, backInventory);
                    }
                }
            }, "\u00a7aDigite o novo nome do servidor para altera-l\u00f3.");
        });
        this.setItem(12, new ItemBuilder().name("\u00a7eAlterar tipo").lore("\u00a77Clique para alterar o tipo do servidor.").type(Material.BOOK).build(), (p, inv, type, stack, slot) -> {
            player.sendMessage("\u00a7aO tipo do servidor foi alterado para " + server.getServerType().name() + ".");
            new ServerDetailsInventory(player, server, backInventory);
        });
        this.setItem(16, new ItemBuilder().name("\u00a7aVoltar").type(Material.ARROW).lore("\u00a77Voltar para " + backInventory.getTitle()).build(), (p, inv, type, stack, slot) -> backInventory.open(player));
        this.open(player);
    }
}

