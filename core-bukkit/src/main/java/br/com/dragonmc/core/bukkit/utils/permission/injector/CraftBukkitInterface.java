/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 */
package br.com.dragonmc.core.bukkit.utils.permission.injector;

import org.bukkit.Bukkit;

public class CraftBukkitInterface {
    private static final String VERSION;

    public static String getCBClassName(String simpleName) {
        if (VERSION == null) {
            return null;
        }
        return "org.bukkit.craftbukkit" + VERSION + simpleName;
    }

    public static Class<?> getCBClass(String name) {
        if (VERSION == null) {
            return null;
        }
        try {
            return Class.forName(CraftBukkitInterface.getCBClassName(name));
        }
        catch (ClassNotFoundException classNotFoundException) {
            return null;
        }
    }

    static {
        Class<?> serverClass = Bukkit.getServer().getClass();
        if (!serverClass.getSimpleName().equals("CraftServer")) {
            VERSION = null;
        } else if (serverClass.getName().equals("org.bukkit.craftbukkit.CraftServer")) {
            VERSION = ".";
        } else {
            String name = serverClass.getName();
            name = name.substring("org.bukkit.craftbukkit".length());
            VERSION = name = name.substring(0, name.length() - "CraftServer".length());
        }
    }
}

