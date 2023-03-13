/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.api.plugin.Event
 */
package br.com.dragonmc.core.bungee.event.player;

import br.com.dragonmc.core.common.command.CommandSender;
import br.com.dragonmc.core.common.member.Member;
import br.com.dragonmc.core.common.punish.Punish;
import net.md_5.bungee.api.plugin.Event;

public class PlayerPunishEvent
extends Event {
    private Member punished;
    private Punish punish;
    private CommandSender sender;

    public Member getPunished() {
        return this.punished;
    }

    public Punish getPunish() {
        return this.punish;
    }

    public CommandSender getSender() {
        return this.sender;
    }

    public PlayerPunishEvent(Member punished, Punish punish, CommandSender sender) {
        this.punished = punished;
        this.punish = punish;
        this.sender = sender;
    }
}

