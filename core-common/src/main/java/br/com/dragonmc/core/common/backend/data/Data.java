/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 */
package br.com.dragonmc.core.common.backend.data;

import com.google.gson.JsonElement;
import br.com.dragonmc.core.common.backend.Query;

public interface Data<T extends Query<JsonElement>> {
    public T getQuery();
}

