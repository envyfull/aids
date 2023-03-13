/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 *  org.bukkit.event.entity.PlayerDeathEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.event.player.PlayerToggleSneakEvent
 *  org.bukkit.inventory.ItemStack
 */
package br.com.dragonmc.pvp.arena.kit.register;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import br.com.dragonmc.pvp.arena.kit.Kit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

public class NinjaKit
extends Kit {
    private HashMap<String, NinjaHit> ninjaHits = new HashMap();

    public NinjaKit() {
        super("Ninja", "Como um ninja teletransporte-se para as costas de seus inimigos", Material.EMERALD, 17000, new ArrayList<ItemStack>());
    }

    @EventHandler
    public void onNinjaHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player damager = (Player)event.getDamager();
            Player damaged = (Player)event.getEntity();
            if (this.hasAbility(damager)) {
                NinjaHit ninjaHit = this.ninjaHits.get(damager.getName());
                if (ninjaHit == null) {
                    ninjaHit = new NinjaHit(damaged);
                } else {
                    ninjaHit.setTarget(damaged);
                }
                this.ninjaHits.put(damager.getName(), ninjaHit);
            }
        }
    }

    @EventHandler
    public void onShift(PlayerToggleSneakEvent event) {
        Player p = event.getPlayer();
        if (!event.isSneaking()) {
            return;
        }
        if (!this.hasAbility(p)) {
            return;
        }
        if (!this.ninjaHits.containsKey(p.getName())) {
            return;
        }
        NinjaHit ninjaHit = this.ninjaHits.get(p.getName());
        Player target = ninjaHit.getTarget();
        if (target.isDead()) {
            return;
        }
        if (ninjaHit.getTargetExpires() < System.currentTimeMillis()) {
            return;
        }
        if (p.getLocation().distance(target.getLocation()) > 50.0) {
            p.sendMessage("\u00a7a\u00a7l> \u00a7fO jogador est\u00e1 muito longe\u00a7f!");
            return;
        }
        if (this.isCooldown(p)) {
            return;
        }
        p.teleport(target.getLocation());
        p.sendMessage("\u00a7a\u00a7l> \u00a7fTeletransportado at\u00e9 o \u00a7a" + target.getName() + "\u00a7f!");
        this.addCooldown(p, 6L);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player p = event.getEntity();
        if (p.getKiller() != null) {
            Iterator<Map.Entry<String, NinjaHit>> iterator = this.ninjaHits.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, NinjaHit> entry = iterator.next();
                if (entry.getValue().target != p.getKiller()) continue;
                iterator.remove();
            }
        }
        if (!this.ninjaHits.containsKey(p.getName())) {
            return;
        }
        this.ninjaHits.remove(p.getName());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        if (!this.ninjaHits.containsKey(p.getName())) {
            return;
        }
        this.ninjaHits.remove(p.getName());
    }

    private static class NinjaHit {
        private Player target;
        private long targetExpires;

        public NinjaHit(Player target) {
            this.target = target;
            this.targetExpires = System.currentTimeMillis() + 15000L;
        }

        public Player getTarget() {
            return this.target;
        }

        public long getTargetExpires() {
            return this.targetExpires;
        }

        public void setTarget(Player player) {
            this.target = player;
            this.targetExpires = System.currentTimeMillis() + 20000L;
        }
    }
}

