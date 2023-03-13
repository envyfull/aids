/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.HandlerList
 *  org.bukkit.event.Listener
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 */
package br.com.dragonmc.core.bukkit.manager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import br.com.dragonmc.core.bukkit.event.UpdateEvent;
import br.com.dragonmc.core.bukkit.event.cooldown.CooldownFinishEvent;
import br.com.dragonmc.core.bukkit.event.cooldown.CooldownStartEvent;
import br.com.dragonmc.core.bukkit.event.cooldown.CooldownStopEvent;
import br.com.dragonmc.core.bukkit.BukkitCommon;
import br.com.dragonmc.core.bukkit.utils.cooldown.Cooldown;
import br.com.dragonmc.core.bukkit.utils.cooldown.ItemCooldown;
import br.com.dragonmc.core.bukkit.utils.player.PlayerHelper;
import br.com.dragonmc.core.common.utils.string.StringFormat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class CooldownManager
implements Listener {
    private static final char CHAR = '|';
    private Map<UUID, List<Cooldown>> map = new ConcurrentHashMap<UUID, List<Cooldown>>();
    private Listener listener = new CooldownListener();

    public void addCooldown(Player player, Cooldown cooldown) {
        CooldownStartEvent event = new CooldownStartEvent(player, cooldown);
        Bukkit.getServer().getPluginManager().callEvent((Event)event);
        if (!event.isCancelled()) {
            List<Cooldown> list = this.map.computeIfAbsent(player.getUniqueId(), v -> new ArrayList());
            boolean add = true;
            for (Cooldown cool : list) {
                if (!cool.getName().equals(cooldown.getName())) continue;
                cool.update(cooldown.getDuration(), cooldown.getStartTime());
                add = false;
            }
            if (add) {
                list.add(cooldown);
            }
            if (!this.map.isEmpty()) {
                this.registerListener();
            }
        }
    }

    public void addCooldown(UUID uuid, String name, long duration) {
        Player player = Bukkit.getPlayer((UUID)uuid);
        if (player == null) {
            return;
        }
        Cooldown cooldown = new Cooldown(name, duration);
        CooldownStartEvent event = new CooldownStartEvent(player, cooldown);
        Bukkit.getServer().getPluginManager().callEvent((Event)event);
        if (!event.isCancelled()) {
            List<Cooldown> list = this.map.computeIfAbsent(player.getUniqueId(), v -> new ArrayList());
            boolean add = true;
            for (Cooldown cool : list) {
                if (!cool.getName().equals(cooldown.getName())) continue;
                cool.update(cooldown.getDuration(), cooldown.getStartTime());
                add = false;
            }
            if (add) {
                list.add(cooldown);
            }
            if (!this.map.isEmpty()) {
                this.registerListener();
            }
        }
    }

    private void registerListener() {
        if (this.listener == null) {
            this.listener = new CooldownListener();
            Bukkit.getPluginManager().registerEvents(this.listener, (Plugin)BukkitCommon.getInstance());
        }
    }

    public boolean removeCooldown(Player player, String name) {
        if (this.map.containsKey(player.getUniqueId())) {
            List<Cooldown> list = this.map.get(player.getUniqueId());
            Iterator<Cooldown> it = list.iterator();
            while (it.hasNext()) {
                Cooldown cooldown = it.next();
                if (!cooldown.getName().equals(name)) continue;
                it.remove();
                Bukkit.getPluginManager().callEvent((Event)new CooldownStopEvent(player, cooldown));
                return true;
            }
        }
        return false;
    }

    public boolean hasCooldown(Player player, String name) {
        if (this.map.containsKey(player.getUniqueId())) {
            List<Cooldown> list = this.map.get(player.getUniqueId());
            for (Cooldown cooldown : list) {
                if (!cooldown.getName().equals(name)) continue;
                return true;
            }
        }
        return false;
    }

    public boolean hasCooldown(UUID uniqueId, String name) {
        if (this.map.containsKey(uniqueId)) {
            List<Cooldown> list = this.map.get(uniqueId);
            for (Cooldown cooldown : list) {
                if (!cooldown.getName().equals(name)) continue;
                return true;
            }
        }
        return false;
    }

    public Cooldown getCooldown(UUID uniqueId, String name) {
        if (this.map.containsKey(uniqueId)) {
            List<Cooldown> list = this.map.get(uniqueId);
            for (Cooldown cooldown : list) {
                if (!cooldown.getName().equals(name)) continue;
                return cooldown;
            }
        }
        return null;
    }

    public void clearCooldown(Player player) {
        if (this.map.containsKey(player.getUniqueId())) {
            this.map.remove(player.getUniqueId());
        }
    }

    public class CooldownListener
    implements Listener {
        @EventHandler
        public void onUpdate(UpdateEvent event) {
            if (event.getType() != UpdateEvent.UpdateType.TICK) {
                return;
            }
            if (event.getCurrentTick() % 5L == 0L) {
                return;
            }
            for (UUID uuid : CooldownManager.this.map.keySet()) {
                ItemCooldown item;
                Cooldown cooldown;
                Player player = Bukkit.getPlayer((UUID)uuid);
                if (player == null) continue;
                List list = (List)CooldownManager.this.map.get(uuid);
                Iterator it = list.iterator();
                Cooldown found = null;
                while (it.hasNext()) {
                    cooldown = (Cooldown)it.next();
                    if (!cooldown.expired()) {
                        if (cooldown instanceof ItemCooldown) {
                            ItemCooldown item2;
                            ItemStack hand = player.getItemInHand();
                            if (hand == null || hand.getType() == Material.AIR || !hand.equals((Object)(item2 = (ItemCooldown)cooldown).getItem())) continue;
                            item2.setSelected(true);
                            found = item2;
                            break;
                        }
                        found = cooldown;
                        continue;
                    }
                    it.remove();
                    CooldownFinishEvent e = new CooldownFinishEvent(player, cooldown);
                    Bukkit.getServer().getPluginManager().callEvent((Event)e);
                }
                if (found != null) {
                    this.display(player, found);
                    continue;
                }
                if (list.isEmpty()) {
                    PlayerHelper.actionbar(player, " ");
                    CooldownManager.this.map.remove(uuid);
                    continue;
                }
                cooldown = (Cooldown)list.get(0);
                if (!(cooldown instanceof ItemCooldown) || !(item = (ItemCooldown)cooldown).isSelected()) continue;
                item.setSelected(false);
                PlayerHelper.actionbar(player, " ");
            }
        }

        @EventHandler
        public void onCooldown(CooldownStopEvent event) {
            if (CooldownManager.this.map.isEmpty()) {
                HandlerList.unregisterAll((Listener)CooldownManager.this.listener);
                CooldownManager.this.listener = null;
            }
        }

        private void display(Player player, Cooldown cooldown) {
            StringBuilder bar = new StringBuilder();
            double percentage = cooldown.getPercentage();
            double count = 20.0 - Math.max(percentage > 0.0 ? 1.0 : 0.0, percentage / 5.0);
            int a = 0;
            while ((double)a < count) {
                bar.append("\u00a7a|");
                ++a;
            }
            a = 0;
            while ((double)a < 20.0 - count) {
                bar.append("\u00a7c|");
                ++a;
            }
            PlayerHelper.actionbar(player, "\u00a7f" + cooldown.getName() + " " + bar.toString() + " \u00a7f" + StringFormat.formatTime((int)cooldown.getRemaining(), StringFormat.TimeFormat.NORMAL));
        }
    }
}

