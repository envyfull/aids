/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 */
package br.com.dragonmc.core.bukkit.utils.helper;

import br.com.dragonmc.core.bukkit.BukkitCommon;
import br.com.dragonmc.core.bukkit.utils.hologram.Hologram;
import br.com.dragonmc.core.bukkit.utils.hologram.HologramBuilder;
import br.com.dragonmc.core.bukkit.utils.hologram.impl.SimpleHologram;
import org.bukkit.Location;

public class HologramHelper {
    public static Hologram createHologram(String text, Location location) {
        return BukkitCommon.getInstance().getHologramManager().createHologram(new HologramBuilder().setDisplayName(text).setLocation(location).setHologramClass(SimpleHologram.class));
    }

    public static Hologram createHologram(Hologram hologram) {
        return BukkitCommon.getInstance().getHologramManager().createHologram(hologram);
    }
}

