/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.AsyncPlayerPreLoginEvent
 *  org.bukkit.event.player.AsyncPlayerPreLoginEvent$Result
 *  org.bukkit.event.player.PlayerLoginEvent
 *  org.bukkit.event.player.PlayerLoginEvent$Result
 */
package br.com.dragonmc.pvp.core.listener;

import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.pvp.core.GameAPI;
import br.com.dragonmc.pvp.core.gamer.Gamer;
import br.com.dragonmc.core.common.member.status.StatusType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class GamerListener
implements Listener {
    @EventHandler(priority=EventPriority.HIGHEST)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            return;
        }
        Gamer gamer = GameAPI.getInstance().getGamerData().loadGamer(event.getUniqueId(), GameAPI.getInstance().getGamerClass());
        if (gamer == null) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, CommonPlugin.getInstance().getPluginInfo().translate("gamer-not-loaded") + " [0]");
            return;
        }
        GameAPI.getInstance().getGamerManager().loadGamer(gamer);
        CommonPlugin.getInstance().getStatusManager().loadStatus(gamer.getUniqueId(), StatusType.PVP);
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            return;
        }
        if (GameAPI.getInstance().getGamerManager().getGamer(event.getPlayer().getUniqueId()) == null) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, CommonPlugin.getInstance().getPluginInfo().translate("gamer-not-loaded") + " [1]");
            return;
        }
    }
}

