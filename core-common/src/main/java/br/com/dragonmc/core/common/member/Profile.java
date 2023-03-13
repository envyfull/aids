/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.core.common.member;

import java.util.UUID;

import br.com.dragonmc.core.common.command.CommandSender;

public class Profile {
    private final UUID uniqueId;
    private final String playerName;
    private final long createdAt;

    public Profile(UUID uniqueId, String playerName) {
        this(uniqueId, playerName, System.currentTimeMillis());
    }

    public Profile(CommandSender member) {
        this(member.getUniqueId(), member.getName(), System.currentTimeMillis());
    }

    public static Profile from(CommandSender sender) {
        return new Profile(sender);
    }

    public boolean equals(Object obj) {
        if (obj instanceof Profile) {
            Profile profile = (Profile)obj;
            return profile.getUniqueId().equals(this.uniqueId) || profile.getPlayerName().equals(this.playerName);
        }
        if (obj instanceof UUID) {
            UUID uuid = (UUID)obj;
            return uuid.equals(this.uniqueId);
        }
        if (obj instanceof String) {
            String string = (String)obj;
            return string.equals(this.playerName);
        }
        return super.equals(obj);
    }

    public Profile(UUID uniqueId, String playerName, long createdAt) {
        this.uniqueId = uniqueId;
        this.playerName = playerName;
        this.createdAt = createdAt;
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public long getCreatedAt() {
        return this.createdAt;
    }
}

