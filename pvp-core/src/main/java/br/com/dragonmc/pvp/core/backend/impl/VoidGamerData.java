/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.pvp.core.backend.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import br.com.dragonmc.pvp.core.backend.GamerData;
import br.com.dragonmc.pvp.core.gamer.Gamer;

public class VoidGamerData
implements GamerData {
    @Override
    public <T extends Gamer> T loadGamer(UUID uniqueId, Class<T> clazz) {
        try {
            return (T)((Gamer)clazz.getConstructor(UUID.class).newInstance(uniqueId));
        }
        catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean deleteGamer(UUID uniqueId) {
        return true;
    }

    @Override
    public boolean createGamer(Gamer gamer) {
        return true;
    }
}

