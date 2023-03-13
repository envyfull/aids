/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.pvp.core.backend;

import java.util.UUID;

import br.com.dragonmc.pvp.core.gamer.Gamer;

public interface GamerData {
    public <T extends Gamer> T loadGamer(UUID var1, Class<T> var2);

    public boolean deleteGamer(UUID var1);

    public boolean createGamer(Gamer var1);
}

