/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 *  net.md_5.bungee.api.plugin.Cancellable
 *  net.md_5.bungee.api.plugin.Event
 */
package br.com.dragonmc.core.bungee.event;

import lombok.NonNull;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

public class RedisMessageEvent
extends Event
implements Cancellable {
    @NonNull
    private String channel;
    @NonNull
    private String message;
    private boolean cancelled;

    @NonNull
    public String getChannel() {
        return this.channel;
    }

    @NonNull
    public String getMessage() {
        return this.message;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public RedisMessageEvent(@NonNull String channel, @NonNull String message) {
        if (channel == null) {
            throw new NullPointerException("channel is marked non-null but is null");
        }
        if (message == null) {
            throw new NullPointerException("message is marked non-null but is null");
        }
        this.channel = channel;
        this.message = message;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}

