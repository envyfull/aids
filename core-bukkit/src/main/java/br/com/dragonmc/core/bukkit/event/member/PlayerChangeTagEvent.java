/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.core.bukkit.event.member;

import br.com.dragonmc.core.bukkit.event.PlayerCancellableEvent;
import br.com.dragonmc.core.common.permission.Tag;
import org.bukkit.entity.Player;

public class PlayerChangeTagEvent
extends PlayerCancellableEvent {
    private Tag oldTag;
    private Tag newTag;
    private boolean forced;

    public PlayerChangeTagEvent(Player p, Tag oldTag, Tag newTag, boolean forced) {
        super(p);
        this.oldTag = oldTag;
        this.newTag = newTag;
        this.forced = forced;
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

