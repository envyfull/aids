/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.core.common.punish;

public enum PunishType {
    BAN("banido"),
    MUTE("mutado"),
    KICK("kickado");

    private String descriminator;

    private PunishType(String descriminator) {
        this.descriminator = descriminator;
    }

    public String getDescriminator() {
        return this.descriminator;
    }
}

