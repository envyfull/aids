/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.core.common;

import java.util.UUID;
import java.util.logging.Logger;

public interface PluginPlatform {
    public UUID getUniqueId(String var1);

    public String getName(UUID var1);

    public void runAsync(Runnable var1);

    public void runAsync(Runnable var1, long var2);

    public void runAsync(Runnable var1, long var2, long var4);

    public void run(Runnable var1, long var2);

    public void run(Runnable var1, long var2, long var4);

    public void shutdown(String var1);

    public Logger getLogger();

    public void dispatchCommand(String var1);

    public void broadcast(String var1);

    public void broadcast(String var1, String var2);
}

