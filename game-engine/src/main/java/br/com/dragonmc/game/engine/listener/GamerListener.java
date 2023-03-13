/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerLoginEvent
 *  org.bukkit.event.player.PlayerLoginEvent$Result
 *  org.bukkit.event.player.PlayerQuitEvent
 */
package br.com.dragonmc.game.engine.listener;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.UUID;

import br.com.dragonmc.game.engine.GameAPI;
import br.com.dragonmc.game.engine.event.GamerLoadEvent;
import br.com.dragonmc.game.engine.gamer.Gamer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GamerListener
implements Listener {
    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (event.getResult() == PlayerLoginEvent.Result.ALLOWED) {
            Player player = event.getPlayer();
            Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId());
            if (gamer == null) {
                Optional optional = GameAPI.getInstance().getGamerData().loadGamer(player.getUniqueId());
                if (optional.isPresent()) {
                    gamer = (Gamer)optional.get();
                } else {
                    try {
                        gamer = GameAPI.getInstance().getGamerClass().getConstructor(String.class, UUID.class).newInstance(player.getName(), player.getUniqueId());
                        GameAPI.getInstance().getGamerData().createGamer(gamer);
                    }
                    catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
                        event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
                        event.setKickMessage("\u00a7c\u00a7%gamer-not-loaded%\u00a7");
                        e.printStackTrace();
                        return;
                    }
                }
                gamer.setPlayer(event.getPlayer());
                gamer.loadGamer();
                GamerLoadEvent gamerLoadEvent = new GamerLoadEvent(player, gamer);
                Bukkit.getPluginManager().callEvent((Event)gamerLoadEvent);
                if (gamerLoadEvent.isCancelled()) {
                    event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
                    event.setKickMessage(gamerLoadEvent.getReason());
                } else {
                    GameAPI.getInstance().getGamerManager().loadGamer(gamer);
                    GameAPI.getInstance().debug("The gamer " + player.getName() + "(" + player.getUniqueId() + ") has been loaded.");
                }
            } else {
                gamer.setPlayer(event.getPlayer());
                GameAPI.getInstance().debug("The gamer " + player.getName() + "(" + player.getUniqueId() + ") has already been loaded.");
            }
            gamer.setOnline(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId());
        if (gamer == null) {
            return;
        }
        gamer.setPlayer(null);
        gamer.setOnline(false);
        if (GameAPI.getInstance().isUnloadGamer()) {
            GameAPI.getInstance().getGamerManager().unloadGamer(gamer.getUniqueId());
            GameAPI.getInstance().debug("The gamer " + player.getName() + "(" + player.getUniqueId() + ") has been unloaded.");
        }
    }
}

