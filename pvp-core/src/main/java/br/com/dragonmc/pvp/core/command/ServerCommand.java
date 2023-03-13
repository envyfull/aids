/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 */
package br.com.dragonmc.pvp.core.command;

import br.com.dragonmc.core.common.CommonConst;
import br.com.dragonmc.core.bukkit.BukkitCommon;
import br.com.dragonmc.core.bukkit.member.BukkitMember;
import br.com.dragonmc.pvp.core.GameAPI;
import br.com.dragonmc.pvp.core.event.PlayerProtectionEvent;
import br.com.dragonmc.pvp.core.event.PlayerSpawnEvent;
import br.com.dragonmc.core.common.command.CommandArgs;
import br.com.dragonmc.core.common.command.CommandClass;
import br.com.dragonmc.core.common.command.CommandFramework;
import br.com.dragonmc.core.common.command.CommandSender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class ServerCommand
implements CommandClass {
    @CommandFramework.Command(name="spawn")
    public void spawnCommand(CommandArgs cmdArgs) {
        if (!cmdArgs.isPlayer()) {
            return;
        }
        Player player = cmdArgs.getSenderAsMember(BukkitMember.class).getPlayer();
        Bukkit.getPluginManager().callEvent((Event)new PlayerSpawnEvent(player));
        player.teleport(BukkitCommon.getInstance().getLocationManager().getLocation("spawn"));
        player.sendMessage("\u00a7aTeletransportado para o spawn.");
        GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId()).setSpawnProtection(true);
        Bukkit.getPluginManager().callEvent((Event)new PlayerProtectionEvent(player, true));
    }

    @CommandFramework.Command(name="setfulliron", permission="command.setfulliron")
    public void setfullironCommand(CommandArgs cmdArgs) {
        CommandSender sender = cmdArgs.getSender();
        GameAPI.getInstance().setFullIron(!GameAPI.getInstance().isFullIron());
        sender.sendMessage("\u00a7aO modo do servidor foi alterado para " + (GameAPI.getInstance().isFullIron() ? "FullIron" : "Simulator") + ".");
    }

    @CommandFramework.Command(name="setprotection", permission="command.setprotection")
    public void setprotectionCommand(CommandArgs cmdArgs) {
        CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();
        if (args.length == 0) {
            sender.sendMessage(" \u00a7e\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " <radius>\u00a7f para alterar o raio de prote\u00e7\u00e3o do spawn.");
            return;
        }
        Double integer = null;
        try {
            integer = Double.valueOf(args[0]);
        }
        catch (NumberFormatException numberFormatException) {
            // empty catch block
        }
        GameAPI.getInstance().setProtectionRadius(integer);
        sender.sendMessage("\u00a7aO raio de prote\u00e7\u00e3o do spawn foi alterado para " + CommonConst.DECIMAL_FORMAT.format(integer) + ".");
    }
}

