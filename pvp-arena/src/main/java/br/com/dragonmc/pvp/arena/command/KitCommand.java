/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 */
package br.com.dragonmc.pvp.arena.command;

import br.com.dragonmc.core.bukkit.member.BukkitMember;
import br.com.dragonmc.core.bukkit.utils.player.PlayerHelper;
import br.com.dragonmc.core.common.command.CommandArgs;
import br.com.dragonmc.core.common.command.CommandClass;
import br.com.dragonmc.core.common.command.CommandFramework;
import br.com.dragonmc.pvp.arena.GameMain;
import br.com.dragonmc.pvp.arena.event.PlayerSelectedKitEvent;
import br.com.dragonmc.pvp.arena.gamer.Gamer;
import br.com.dragonmc.pvp.arena.kit.Kit;
import br.com.dragonmc.pvp.arena.menu.AbilityInventory;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class KitCommand
implements CommandClass {
    @CommandFramework.Command(name="kit")
    public void kitCommand(CommandArgs cmdArgs) {
        AbilityInventory.InventoryType inventoryType;
        if (!cmdArgs.isPlayer()) {
            return;
        }
        String[] args = cmdArgs.getArgs();
        Player player = ((BukkitMember)cmdArgs.getSender()).getPlayer();
        Gamer gamer = GameMain.getInstance().getGamerManager().getGamer(player.getUniqueId(), Gamer.class);
        if (!gamer.isSpawnProtection()) {
            player.sendMessage("\u00a7cVoc\u00ea n\u00e3o pode usar kits fora da \u00e1rea de prote\u00e7\u00e3o!");
            return;
        }
        if (args.length == 0) {
            player.sendMessage("\u00a7eUse /" + cmdArgs.getLabel() + " para selecionar um kit");
            return;
        }
        Kit kit = GameMain.getInstance().getKitManager().getKit(args[0]);
        if (kit == null) {
            player.sendMessage("\u00a7cO kit " + args[0] + " n\u00e3o existe!");
            return;
        }
        AbilityInventory.InventoryType inventoryType2 = args.length >= 2 ? (args[1].equalsIgnoreCase("1") ? AbilityInventory.InventoryType.PRIMARY : AbilityInventory.InventoryType.SECONDARY) : (inventoryType = AbilityInventory.InventoryType.PRIMARY);
        if ((inventoryType2 == AbilityInventory.InventoryType.PRIMARY ? gamer.getSecondary() : gamer.getPrimary()).equalsIgnoreCase(kit.getName())) {
            player.sendMessage("\u00a7cVoc\u00ea j\u00e1 est\u00e1 usando esse kit!");
            return;
        }
        if (inventoryType2 == AbilityInventory.InventoryType.PRIMARY) {
            gamer.setPrimaryKit(kit);
        } else {
            gamer.setSecondaryKit(kit);
        }
        Bukkit.getPluginManager().callEvent((Event)new PlayerSelectedKitEvent(player, kit, inventoryType2));
        player.sendMessage("\u00a7aVoc\u00ea selecionou o kit " + kit.getName());
        PlayerHelper.title(player, "\u00a7a" + kit.getName(), "\u00a7fselecionado!");
        player.closeInventory();
    }
}

