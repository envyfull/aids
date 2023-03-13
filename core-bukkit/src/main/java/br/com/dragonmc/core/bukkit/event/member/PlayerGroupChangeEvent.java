/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.core.bukkit.event.member;

import br.com.dragonmc.core.bukkit.event.PlayerEvent;
import lombok.NonNull;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.common.member.Member;
import br.com.dragonmc.core.common.permission.Group;
import org.bukkit.entity.Player;

public class PlayerGroupChangeEvent
extends PlayerEvent {
    private Member member;
    private String groupName;
    private long newTime;
    private Action action;

    public PlayerGroupChangeEvent(@NonNull Player player, Member member, String groupName, long newTime, Action action) {
        super(player);
        if (player == null) {
            throw new NullPointerException("player is marked non-null but is null");
        }
        this.member = member;
        this.groupName = groupName;
        this.newTime = newTime;
        this.action = action;
    }

    public PlayerGroupChangeEvent(@NonNull Player player, Member member, Group group, long newTime, Action action) {
        super(player);
        if (player == null) {
            throw new NullPointerException("player is marked non-null but is null");
        }
        this.member = member;
        this.groupName = group.getGroupName();
        this.newTime = newTime;
        this.action = action;
    }

    public Group getGroup() {
        return this.groupName == null ? null : CommonPlugin.getInstance().getPluginInfo().getGroupByName(this.groupName);
    }

    public Member getMember() {
        return this.member;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public long getNewTime() {
        return this.newTime;
    }

    public Action getAction() {
        return this.action;
    }

    public static enum Action {
        ADD,
        SET,
        REMOVE,
        CLEAR,
        UNKNOWN;

    }
}

