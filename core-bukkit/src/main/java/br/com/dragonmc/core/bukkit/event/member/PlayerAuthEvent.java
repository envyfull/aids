/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.core.bukkit.event.member;

import br.com.dragonmc.core.bukkit.event.PlayerEvent;
import br.com.dragonmc.core.common.member.Member;
import org.bukkit.entity.Player;

public class PlayerAuthEvent
extends PlayerEvent {
    private Member member;

    public PlayerAuthEvent(Player player, Member member) {
        super(player);
        this.member = member;
    }

    public Member getMember() {
        return this.member;
    }
}

