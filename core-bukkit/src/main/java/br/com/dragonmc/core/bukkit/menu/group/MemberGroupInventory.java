/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.core.bukkit.menu.group;

import java.util.List;

import br.com.dragonmc.core.bukkit.utils.item.ItemBuilder;
import br.com.dragonmc.core.bukkit.utils.menu.MenuInventory;
import br.com.dragonmc.core.bukkit.utils.menu.MenuItem;
import br.com.dragonmc.core.common.member.Member;
import br.com.dragonmc.core.common.permission.Group;
import br.com.dragonmc.core.common.utils.string.StringFormat;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class MemberGroupInventory {
    public MemberGroupInventory(Player player, Member target, Group group, List<MenuItem> items, int page) {
        MenuInventory menuInventory = new MenuInventory("\u00a77" + target.getName(), 3);
        menuInventory.setItem(10, new ItemBuilder().name("\u00a7a" + target.getName()).type(Material.SKULL_ITEM).durability(3).skin(target.getName()).build());
        menuInventory.setItem(11, new ItemBuilder().name("\u00a7eTodos os grupos").lore("\u00a77Clique para ver todos os grupos desse jogador.").type(Material.PAPER).build());
        menuInventory.setItem(12, new ItemBuilder().name("\u00a7eTodos os grupos").lore("\u00a77Clique para ver todas as permiss\u00f5es desse jogador.").type(Material.BOOK).build());
        if (group.isDefaultGroup()) {
            menuInventory.setItem(15, new ItemBuilder().name("\u00a7cRemover " + StringFormat.formatString(group.getGroupName())).lore("\u00a77Remova o grupo da conta desse jogador.").type(Material.BARRIER).build(), (p, inv, type, stack, slot) -> p.sendMessage("\u00a7cO grupo n\u00e3o pode ser removido!"));
        } else {
            menuInventory.setItem(15, new ItemBuilder().name("\u00a7cRemover " + StringFormat.formatString(group.getGroupName())).lore("\u00a77Remova o grupo da conta desse jogador.").type(Material.BARRIER).build(), (p, inv, type, stack, slot) -> p.sendMessage("\u00a7cUse /group " + target.getName() + " remove " + group.getGroupName() + " para remover esse cargo."));
        }
        menuInventory.setItem(16, new ItemBuilder().name("\u00a77Voltar").type(Material.ARROW).build(), (p, inv, type, stack, slot) -> new MemberGroupListInventory(player, group, items, page));
        menuInventory.open(player);
    }
}

