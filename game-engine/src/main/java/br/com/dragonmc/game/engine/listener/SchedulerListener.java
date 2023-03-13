/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 */
package br.com.dragonmc.game.engine.listener;

import br.com.dragonmc.core.bukkit.event.UpdateEvent;
import br.com.dragonmc.game.engine.GameAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SchedulerListener
implements Listener {
    @EventHandler
    public void update(UpdateEvent event) {
        if (event.getType() == UpdateEvent.UpdateType.SECOND) {
            GameAPI.getInstance().getSchedulerManager().pulse();
        }
    }
}

