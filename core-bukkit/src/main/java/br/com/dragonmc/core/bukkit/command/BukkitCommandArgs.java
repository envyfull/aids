/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.core.bukkit.command;

import br.com.dragonmc.core.BukkitConst;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.bukkit.member.BukkitMember;
import br.com.dragonmc.core.common.command.CommandArgs;
import br.com.dragonmc.core.common.member.Member;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BukkitCommandArgs
extends CommandArgs {
    protected BukkitCommandArgs(CommandSender sender, String label, String[] args, int subCommand) {
        super(sender instanceof Player ? CommonPlugin.getInstance().getMemberManager().getMember(((Player)sender).getUniqueId()) : BukkitConst.CONSOLE_SENDER, label, args, subCommand);
    }

    @Override
    public boolean isPlayer() {
        return this.getSender() instanceof Member;
    }

    public BukkitMember getSenderAsBukkitMember() {
        return (BukkitMember)BukkitMember.class.cast(this.getSender());
    }

    public Player getPlayer() {
        if (!this.isPlayer()) {
            return null;
        }
        return ((BukkitMember)this.getSender()).getPlayer();
    }
}

