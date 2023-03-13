/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.core.common.backend.data;

import java.util.Optional;
import java.util.UUID;
import br.com.dragonmc.core.common.utils.skin.Skin;

public interface SkinData {
    public Optional<Skin> loadData(String var1);

    public void save(Skin var1, int var2);

    public String[] loadSkinById(UUID var1);
}

