/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.game.engine.backend;

import java.util.Optional;
import java.util.UUID;
import br.com.dragonmc.game.engine.gamer.Gamer;

public interface GamerData {
    public <T extends Gamer> Optional<T> loadGamer(UUID var1);

    public void createGamer(Gamer var1);

    public void saveGamer(Gamer var1, String var2);
}

