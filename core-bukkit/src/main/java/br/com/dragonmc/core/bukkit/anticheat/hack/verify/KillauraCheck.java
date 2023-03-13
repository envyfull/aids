/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.core.bukkit.anticheat.hack.verify;

import br.com.dragonmc.core.bukkit.anticheat.hack.HackType;
import br.com.dragonmc.core.bukkit.anticheat.hack.Verify;

public class KillauraCheck
implements Verify {
    @Override
    public HackType getHackType() {
        return HackType.KILLAURA;
    }
}

