/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.core.common.backend;

public interface Database {
    public void connect() throws Exception;

    public boolean isConnected();

    public void close();
}

