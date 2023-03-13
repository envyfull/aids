/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.minecraft.server.v1_8_R3.AttributeInstance
 *  net.minecraft.server.v1_8_R3.AttributeModifier
 *  net.minecraft.server.v1_8_R3.EntityLiving
 *  net.minecraft.server.v1_8_R3.GenericAttributes
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity
 *  org.bukkit.entity.Creature
 *  org.bukkit.entity.Damageable
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Projectile
 *  org.bukkit.entity.Silverfish
 *  org.bukkit.entity.Snowball
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.entity.EntityChangeBlockEvent
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 *  org.bukkit.event.entity.EntityDeathEvent
 *  org.bukkit.event.entity.EntityTargetEvent
 *  org.bukkit.event.entity.PlayerDeathEvent
 *  org.bukkit.event.entity.ProjectileHitEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.inventory.ItemStack
 */
package br.com.dragonmc.game.bedwars.listener;

import br.com.dragonmc.game.bedwars.GameMain;
import br.com.dragonmc.game.bedwars.gamer.Gamer;
import br.com.dragonmc.game.bedwars.island.Island;
import br.com.dragonmc.game.bedwars.island.IslandColor;
import br.com.dragonmc.game.bedwars.utils.ProgressBar;
import com.google.common.collect.ImmutableList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import br.com.dragonmc.core.bukkit.event.UpdateEvent;
import br.com.dragonmc.core.bukkit.utils.item.ItemBuilder;
import br.com.dragonmc.core.common.utils.string.StringFormat;
import net.minecraft.server.v1_8_R3.AttributeInstance;
import net.minecraft.server.v1_8_R3.AttributeModifier;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class DefenserListener
implements Listener {
    private static final long IRONGOLEM_TIME = 240000L;
    private static final long SILVERFISH_TIME = 30000L;
    private Map<Entity, Defenser> defenserMap = new HashMap<Entity, Defenser>();

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Snowball snowball;
        if (event.getEntity() instanceof Snowball && (snowball = (Snowball)event.getEntity()).getShooter() instanceof Player) {
            Player player = (Player)snowball.getShooter();
            Island island = GameMain.getInstance().getIslandManager().getIsland(player.getUniqueId());
            if (island == null) {
                return;
            }
            Location location = event.getEntity().getLocation();
            Silverfish silverfish = (Silverfish)location.getWorld().spawn(location, Silverfish.class);
            long time = System.currentTimeMillis() + 30000L;
            int leftTime = (int)(time % System.currentTimeMillis()) / 1000;
            silverfish.setCustomName("\u00a77[" + ProgressBar.getProgressBar(silverfish.getHealth(), silverfish.getMaxHealth(), 5, '\u25ae', ChatColor.AQUA, ChatColor.GRAY) + "\u00a77] \u00a7b" + StringFormat.formatTime(leftTime, StringFormat.TimeFormat.SHORT));
            silverfish.setCustomNameVisible(true);
            EntityLiving en = (EntityLiving)((CraftEntity)silverfish).getHandle();
            AttributeInstance speed = en.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);
            AttributeModifier speedModifier = new AttributeModifier(silverfish.getUniqueId(), "SpeedIncreaser", 1.4, 1);
            speed.b(speedModifier);
            speed.a(speedModifier);
            this.defenserMap.put((Entity)silverfish, new Defenser(island.getIslandColor(), time));
        }
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (event.getEntity().getType().equals((Object)EntityType.SILVERFISH)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() == null || event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getItem().getType() != Material.MONSTER_EGG) {
            return;
        }
        Player player = event.getPlayer();
        Island island = GameMain.getInstance().getIslandManager().getIsland(player.getUniqueId());
        if (island == null) {
            event.setCancelled(true);
            return;
        }
        ItemStack itemStack = event.getItem();
        player.setItemInHand(ItemBuilder.fromStack(itemStack).type(itemStack.getAmount() > 1 ? itemStack.getType() : Material.AIR).amount(itemStack.getAmount() - 1).build());
        long time = System.currentTimeMillis() + 240000L;
        int leftTime = (int)(time % System.currentTimeMillis()) / 1000;
        EntityType entityType = EntityType.IRON_GOLEM;
        Entity spawnEntity = event.getClickedBlock().getLocation().getWorld().spawnEntity(event.getClickedBlock().getLocation().clone().add(0.0, 1.5, 0.0), entityType);
        this.defenserMap.put(spawnEntity, new Defenser(island.getIslandColor(), System.currentTimeMillis() + 240000L));
        spawnEntity.setCustomName("\u00a77[" + ProgressBar.getProgressBar((int)((Damageable)spawnEntity).getHealth(), (int)((Damageable)spawnEntity).getMaxHealth(), 5, '\u25ae', ChatColor.AQUA, ChatColor.GRAY) + "\u00a77] \u00a7b" + StringFormat.formatTime(leftTime, StringFormat.TimeFormat.SHORT));
        spawnEntity.setCustomNameVisible(true);
        ((Damageable)spawnEntity).setMaxHealth(20.0);
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onEntityDamageEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player || event.getDamager() instanceof Projectile) {
            Entity entity = event.getEntity();
            Player player = event.getDamager() instanceof Player ? (Player)event.getDamager() : (Player)((Projectile)event.getDamager()).getShooter();
            Island island = GameMain.getInstance().getIslandManager().getIsland(player.getUniqueId());
            if (island != null && this.defenserMap.containsKey(entity) && this.defenserMap.get(entity).getIsland() == island.getIslandColor()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        ImmutableList.copyOf(this.defenserMap.keySet()).stream().map(entity -> (Creature)entity).filter(entity -> entity.getTarget().getUniqueId() == player.getUniqueId()).forEach(entity -> entity.setTarget(null));
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        Defenser defenser;
        if (!(event.getTarget() instanceof Player)) {
            return;
        }
        Player player = (Player)event.getTarget();
        Island island = GameMain.getInstance().getIslandManager().getIsland(player.getUniqueId());
        if (island == null) {
            event.setCancelled(true);
            return;
        }
        Entity entity = event.getEntity();
        if (this.defenserMap.containsKey(entity) && (defenser = this.defenserMap.get(entity)).getIsland() == island.getIslandColor()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (this.defenserMap.containsKey(entity)) {
            this.defenserMap.remove(entity);
        }
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (event.getType() == UpdateEvent.UpdateType.SECOND) {
            Iterator<Map.Entry<Entity, Defenser>> iterator = this.defenserMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Entity, Defenser> entry = iterator.next();
                Entity entity = entry.getKey();
                int leftTime = (int)(entry.getValue().getExpireTime() - System.currentTimeMillis()) / 1000;
                if (leftTime > 0) {
                    Creature creature = (Creature)entity;
                    if (creature.getTarget() != null && creature.getTarget().getLocation().distance(entity.getLocation()) > 20.0) {
                        creature.setTarget(null);
                    }
                    if (creature.getTarget() == null) {
                        for (Gamer gamer : GameMain.getInstance().getAlivePlayers()) {
                            if (!gamer.isOnline() || !gamer.isAlive()) continue;
                            Player player = gamer.getPlayer();
                            if (GameMain.getInstance().getIslandManager().getIsland(player.getUniqueId()).getIslandColor() == entry.getValue().getIsland() || !(player.getLocation().distance(entity.getLocation()) <= 10.0)) continue;
                            creature.setTarget((LivingEntity)player);
                        }
                    }
                    entity.setCustomName("\u00a77[" + ProgressBar.getProgressBar((int)((Damageable)entity).getHealth(), (int)((Damageable)entity).getMaxHealth(), 5, '\u25ae', ChatColor.AQUA, ChatColor.GRAY) + "\u00a77] \u00a7b" + StringFormat.formatTime(leftTime, StringFormat.TimeFormat.SHORT));
                    continue;
                }
                entity.remove();
                iterator.remove();
            }
        }
    }

    public class Defenser {
        private final IslandColor island;
        private long expireTime;

        public Defenser(IslandColor island, long expireTime) {
            this.island = island;
            this.expireTime = expireTime;
        }

        public IslandColor getIsland() {
            return this.island;
        }

        public long getExpireTime() {
            return this.expireTime;
        }
    }
}

