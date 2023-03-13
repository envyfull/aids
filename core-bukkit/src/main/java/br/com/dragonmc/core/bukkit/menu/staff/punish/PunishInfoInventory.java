/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.core.bukkit.menu.staff.punish;

import java.util.List;

import br.com.dragonmc.core.bukkit.utils.item.ItemBuilder;
import br.com.dragonmc.core.bukkit.utils.menu.MenuInventory;
import br.com.dragonmc.core.common.member.Member;
import br.com.dragonmc.core.common.punish.PunishType;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PunishInfoInventory
extends MenuInventory {
    public PunishInfoInventory(Player player, Member target) {
        super("\u00a77Puni\u00e7\u00f5es " + target.getName(), 3);
        this.setItem(10, new ItemBuilder().name("\u00a7a" + target.getName()).lore("\u00a77Total de puni\u00e7\u00f5es: \u00a7a" + target.getPunishConfiguration().getPunishMap().values().stream().mapToInt(List::size).sum()).type(Material.SKULL_ITEM).durability(3).skin(target.getName()).build());
        this.setItem(11, new ItemBuilder().name("\u00a7aTodos os banimentos").lore("\u00a77Clique para Listar banimentos").type(Material.BOOK).build(), (p, inv, type, stack, slot) -> new PunishInfoListInventory(player, target, PunishType.BAN, 1, this));
        this.setItem(12, new ItemBuilder().name("\u00a7aTodos os mutes").lore("\u00a77Clique para Listar mutes").type(Material.BOOK).build(), (p, inv, type, stack, slot) -> new PunishInfoListInventory(player, target, PunishType.MUTE, 1, this));
        this.setItem(13, new ItemBuilder().name("\u00a7aTodos os kicks").lore("\u00a77Clique para Listar kicks").type(Material.BOOK).build(), (p, inv, type, stack, slot) -> new PunishInfoListInventory(player, target, PunishType.KICK, 1, this));
        this.open(player);
    }
}

