/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 */
package br.com.dragonmc.lobby.login.captcha.impl;

import br.com.dragonmc.core.bukkit.BukkitCommon;
import br.com.dragonmc.lobby.login.captcha.Captcha;
import br.com.dragonmc.core.common.utils.Callback;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class MoveCaptcha
implements Captcha {
    @Override
    public void verify(final Player player, final Callback<Boolean> callback) {
        final Location lastLocation = player.getLocation().clone();
        player.teleport(player.getLocation().add(0.0, 5.0, 0.0));
        final Location actualLocation = player.getLocation().clone();
        new BukkitRunnable(){

            public void run() {
                callback.callback(Math.abs(actualLocation.getY() - player.getLocation().getY()) > 2.0);
                player.teleport(lastLocation);
            }
        }.runTaskLater((Plugin)BukkitCommon.getInstance(), 100L);
    }
}

