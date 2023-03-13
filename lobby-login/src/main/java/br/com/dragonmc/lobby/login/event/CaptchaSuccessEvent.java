/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.lobby.login.event;

import br.com.dragonmc.core.bukkit.event.PlayerCancellableEvent;
import org.bukkit.entity.Player;

public class CaptchaSuccessEvent
extends PlayerCancellableEvent {
    public CaptchaSuccessEvent(Player player) {
        super(player);
    }
}

