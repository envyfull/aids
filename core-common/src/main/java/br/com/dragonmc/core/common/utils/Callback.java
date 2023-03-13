/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.core.common.utils;

public abstract class Callback<T> {
    private T callback;

    public abstract void callback(T var1);

    public T getCallback() {
        return this.callback;
    }
}

