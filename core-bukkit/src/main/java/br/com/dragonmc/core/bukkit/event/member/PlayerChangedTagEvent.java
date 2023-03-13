/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.core.bukkit.event.member;

import br.com.dragonmc.core.bukkit.event.PlayerCancellableEvent;
import br.com.dragonmc.core.common.member.Member;
import br.com.dragonmc.core.common.permission.Tag;
import org.bukkit.entity.Player;

public class PlayerChangedTagEvent
extends PlayerCancellableEvent {
    private Member member;
    private Tag oldTag;
    private Tag newTag;
    private boolean forced;

    public PlayerChangedTagEvent(Player player, Member member, Tag oldTag, Tag newTag, boolean forced) {
        super(player);
        this.member = member;
        this.oldTag = oldTag;
        this.newTag = newTag;
        this.forced = forced;
    }

    public Member getMember() {
        return this.member;
    }

    public Tag getOldTag() {
        return this.oldTag;
    }

    public Tag getNewTag() {
        return this.newTag;
    }

    public boolean isForced() {
        return this.forced;
    }

    public void setNewTag(Tag newTag) {
        this.newTag = newTag;
    }
}

