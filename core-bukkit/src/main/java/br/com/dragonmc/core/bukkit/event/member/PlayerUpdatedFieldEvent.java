/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.core.bukkit.event.member;

import br.com.dragonmc.core.bukkit.event.PlayerEvent;
import br.com.dragonmc.core.bukkit.member.BukkitMember;
import org.bukkit.entity.Player;

public class PlayerUpdatedFieldEvent
extends PlayerEvent {
    private BukkitMember bukkitMember;
    private String field;
    private Object oldObject;
    private Object object;

    public PlayerUpdatedFieldEvent(Player p, BukkitMember player, String field, Object oldObject, Object object) {
        super(p);
        this.bukkitMember = player;
        this.field = field;
        this.oldObject = oldObject;
        this.object = object;
    }

    public BukkitMember getBukkitMember() {
        return this.bukkitMember;
    }

    public String getField() {
        return this.field;
    }

    public Object getOldObject() {
        return this.oldObject;
    }

    public Object getObject() {
        return this.object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}

