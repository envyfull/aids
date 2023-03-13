/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.pvp.core.gamer;

import java.util.UUID;

public abstract class Gamer {
    private final UUID uniqueId;
    private boolean spawnProtection = true;

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public void setSpawnProtection(boolean spawnProtection) {
        this.spawnProtection = spawnProtection;
    }

    public boolean hasSpawnProtection() {
        return this.spawnProtection;
    }

    public boolean isSpawnProtection() {
        return this.spawnProtection;
    }

    public Gamer(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }
}

