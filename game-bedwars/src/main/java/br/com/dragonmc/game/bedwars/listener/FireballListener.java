/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Fireball
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.TNTPrimed
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.block.BlockExplodeEvent
 *  org.bukkit.event.block.BlockIgniteEvent
 *  org.bukkit.event.block.BlockPlaceEvent
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 *  org.bukkit.event.entity.EntityDamageEvent
 *  org.bukkit.event.entity.EntityDamageEvent$DamageCause
 *  org.bukkit.event.entity.EntityExplodeEvent
 *  org.bukkit.event.entity.ExplosionPrimeEvent
 *  org.bukkit.event.entity.ProjectileHitEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.metadata.FixedMetadataValue
 *  org.bukkit.metadata.MetadataValue
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.util.Vector
 */
package br.com.dragonmc.game.bedwars.listener;

import java.util.Arrays;
import java.util.List;
import br.com.dragonmc.core.common.CommonConst;
import br.com.dragonmc.game.bedwars.GameMain;
import br.com.dragonmc.game.bedwars.utils.GamerHelper;
import br.com.dragonmc.game.engine.GameAPI;
import br.com.dragonmc.core.bukkit.utils.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class FireballListener
implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() == null) {
            return;
        }
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            Player player = event.getPlayer();
            if (event.getItem().getType() == Material.FIREBALL) {
                player.setItemInHand(event.getItem().getAmount() > 1 ? new ItemBuilder().type(Material.FIREBALL).amount(event.getItem().getAmount() - 1).build() : new ItemStack(Material.AIR));
                Fireball fireball = (Fireball)player.launchProjectile(Fireball.class);
                fireball.setYield(0.0f);
                fireball.setFireTicks(-1);
                fireball.setIsIncendiary(false);
                fireball.setMetadata("boost", GameAPI.getInstance().createMeta(player.getName()));
                player.setMetadata("no-damage-by-entity", GameAPI.getInstance().createMeta(fireball.getEntityId()));
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.TNT) {
            block.setType(Material.AIR);
            Player player = event.getPlayer();
            player.setItemInHand(player.getItemInHand().getAmount() > 1 ? new ItemBuilder().type(Material.TNT).amount(player.getItemInHand().getAmount() - 1).build() : new ItemStack(Material.AIR));
            TNTPrimed tntPrimed = (TNTPrimed)block.getWorld().spawn(event.getBlock().getLocation().clone().add(0.5, 0.5, 0.5), TNTPrimed.class);
            player.setMetadata("no-damage-by-entity", GameAPI.getInstance().createMeta(tntPrimed.getEntityId()));
            tntPrimed.setFuseTicks(48);
            Bukkit.getScheduler().runTaskLater((Plugin) GameMain.getInstance(), () -> tntPrimed.setMetadata("location", (MetadataValue)new FixedMetadataValue((Plugin)GameMain.getInstance(), (Object)CommonConst.GSON.toJson((Object)event.getPlayer().getLocation().serialize()))), 40L);
            tntPrimed.setMetadata("boost", GameAPI.getInstance().createMeta(event.getPlayer().getName()));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Fireball) {
            Fireball entity = (Fireball)event.getEntity();
            Location location = entity.getLocation();
            if (entity.hasMetadata("boost")) {
                Player player = Bukkit.getPlayer((String)((MetadataValue)entity.getMetadata("boost").get(0)).asString());
                if (player == null) {
                    player = GamerHelper.getMoreNearbyPlayers(location, 6.0);
                }
                if (player == null) {
                    return;
                }
                if (entity.getType() == EntityType.FIREBALL || entity.getType() == EntityType.SMALL_FIREBALL) {
                    this.makeExplosion(location, EntityType.FIREBALL);
                    for (Player ps : GamerHelper.getPlayersNear(location, 3.0)) {
                        if (ps.hasMetadata("onlyboost-onground") && ((MetadataValue)ps.getMetadata("onlyboost-onground").get(0)).asLong() > System.currentTimeMillis()) {
                            ps.removeMetadata("onlyboost-onground", (Plugin)GameMain.getInstance());
                            if (!ps.isOnGround()) continue;
                        }
                        ps.setVelocity(ps == player ? this.fireballBoost(ps, (Entity)entity, true) : this.fireballBoost(ps, (Entity)entity, false));
                    }
                }
            }
        }
    }

    public Vector tntBoost(Location location, Player player) {
        double Y;
        boolean onGround = player.isOnGround();
        double multiplier = onGround ? 1.5 : 1.5;
        double d = Y = onGround ? 0.5 : 0.9;
        if (player.getLocation().distance(location) <= 2.7 && (GamerHelper.forwardLocationByPlayerRotation(player, location, 3).add(0.0, 1.0, 0.0).getBlock().getType() != Material.AIR || GamerHelper.forwardLocationByPlayerRotation(player, location, 3).add(0.0, 2.0, 0.0).getBlock().getType() != Material.AIR)) {
            location = GamerHelper.forwardLocationByPlayerRotation(player, location, 3);
        }
        return player.getLocation().subtract(location).toVector().normalize().multiply(multiplier).setY(Y);
    }

    public Vector fireballBoost(Player player, Entity entity, boolean moreBoost) {
        Location entityLocation = entity.getLocation();
        boolean onGround = player.isOnGround();
        double Y = moreBoost ? (onGround ? 0.6D : 0.8D) : (onGround ? 1.2D : 0.3D);
        double multiplier = moreBoost ? (onGround ? 5.8D : 2.8D) : (onGround ? 0.5D : 1.0D);

        double d = moreBoost ? (onGround ? 5.8 : 2.8) : (multiplier = onGround ? 0.5 : 1.0);
        double d2 = moreBoost ? (onGround ? 0.6 : 0.8) : (Y = onGround ? 1.2 : 0.3);
        if (player.getLocation().distance(entityLocation) <= 2.7 && (GamerHelper.forwardLocationByPlayerRotation(player, entityLocation, 3).add(0.0, 1.0, 0.0).getBlock().getType() != Material.AIR || GamerHelper.forwardLocationByPlayerRotation(player, entityLocation, 3).add(0.0, 2.0, 0.0).getBlock().getType() != Material.AIR)) {
            entityLocation = GamerHelper.forwardLocationByPlayerRotation(player, entityLocation, 3);
        }
        return player.getLocation().subtract(entityLocation).toVector().normalize().multiply(multiplier).setY(Y);
    }

    @EventHandler
    public void onExplosion(BlockExplodeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onExplosion(EntityExplodeEvent event) {
        event.setCancelled(true);
    }

    public void makeExplosion(Location explosionLocation, EntityType entityType) {
        explosionLocation.getWorld().createExplosion(explosionLocation, 1.0f);
        for (Block block : GamerHelper.getNearbyBlocks(explosionLocation, entityType == EntityType.PRIMED_TNT || entityType == EntityType.MINECART_TNT ? 3 : 2)) {
            if (!GameMain.getInstance().getPlayersBlock().contains(block.getLocation()) || block.getType() == Material.OBSIDIAN || this.checkIfBlockIsProtected(block) || entityType != EntityType.PRIMED_TNT && entityType != EntityType.MINECART_TNT && block.getType() == Material.ENDER_STONE) continue;
            block.setType(Material.AIR);
        }
    }

    public boolean checkIfBlockIsProtected(Block block) {
        List<Block> blocks = Arrays.asList(block.getRelative(BlockFace.NORTH), block.getRelative(BlockFace.SOUTH), block.getRelative(BlockFace.WEST), block.getRelative(BlockFace.EAST), block.getRelative(BlockFace.UP), block.getRelative(BlockFace.DOWN));
        for (Block b : blocks) {
            if (!b.getType().name().contains("GLASS")) continue;
            return true;
        }
        return false;
    }

    @EventHandler
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        event.setCancelled(true);
        Entity entity = event.getEntity();
        if (entity.getType() == EntityType.FIREBALL || entity.getType() == EntityType.SMALL_FIREBALL) {
            return;
        }
        Location loc = event.getEntity().getLocation();
        this.makeExplosion(loc, EntityType.PRIMED_TNT);
        if (entity.hasMetadata("boost")) {
            Player player = Bukkit.getPlayer((String)((MetadataValue)entity.getMetadata("boost").get(0)).asString());
            for (Player ps : GamerHelper.getPlayersNear(loc, 6.0)) {
                if (ps != player) continue;
                ps.setVelocity(this.tntBoost(loc, ps));
            }
        }
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player)event.getEntity();
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL || event.getCause() == EntityDamageEvent.DamageCause.FALLING_BLOCK) {
                if (player.hasMetadata("onlyboost-onground") && ((MetadataValue)player.getMetadata("onlyboost-onground").get(0)).asLong() > System.currentTimeMillis()) {
                    event.setDamage(event.getDamage() / 2.0);
                }
            } else if (event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION || event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION || event.getCause() == EntityDamageEvent.DamageCause.CUSTOM || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
                player.setFireTicks(-1);
                if (player.hasMetadata("no-damage-by-entity")) {
                    event.setDamage(0.0);
                    event.setCancelled(true);
                    player.removeMetadata("no-damage-by-entity", (Plugin)GameMain.getInstance());
                }
            }
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if ((event.getDamager() instanceof Fireball || event.getDamager() instanceof TNTPrimed) && event.getEntity() instanceof Player) {
            Player player = (Player)event.getEntity();
            if (player.hasMetadata("no-damage-by-entity")) {
                player.setFireTicks(-1);
                event.setDamage(0.0);
                event.setCancelled(true);
                player.removeMetadata("no-damage-by-entity", (Plugin)GameMain.getInstance());
                player.setMetadata("nofall", GameAPI.getInstance().createMeta(System.currentTimeMillis() + 3000L));
            }
            return;
        }
    }
}

