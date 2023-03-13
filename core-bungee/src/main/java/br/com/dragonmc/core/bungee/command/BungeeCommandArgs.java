/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.api.CommandSender
 *  net.md_5.bungee.api.connection.ProxiedPlayer
 */
package br.com.dragonmc.core.bungee.command;

import br.com.dragonmc.core.bungee.BungeeConst;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.bungee.member.BungeeMember;
import br.com.dragonmc.core.common.command.CommandArgs;
import br.com.dragonmc.core.common.member.Member;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeCommandArgs
extends CommandArgs {
    protected BungeeCommandArgs(CommandSender sender, String label, String[] args, int subCommand) {
        super(sender instanceof ProxiedPlayer ? CommonPlugin.getInstance().getMemberManager().getMember(((ProxiedPlayer)sender).getUniqueId()) : BungeeConst.CONSOLE_SENDER, label, args, subCommand);
    }

    @Override
    public boolean isPlayer() {
        return this.getSender() instanceof Member;
    }

    public ProxiedPlayer getPlayer() {
        if (!this.isPlayer()) {
            return null;
        }
        return ((BungeeMember)this.getSender()).getProxiedPlayer();
    }
}

