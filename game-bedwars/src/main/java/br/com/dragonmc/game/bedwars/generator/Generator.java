/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_8_R3.Packet
 *  net.minecraft.server.v1_8_R3.PacketPlayOutEntity$PacketPlayOutEntityLook
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
 *  org.bukkit.entity.Item
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.util.Vector
 */
package br.com.dragonmc.game.bedwars.generator;

import java.util.List;
import java.util.stream.Collectors;
import br.com.dragonmc.core.bukkit.BukkitCommon;
import br.com.dragonmc.game.bedwars.GameMain;
import br.com.dragonmc.core.bukkit.utils.floatingitem.CustomItem;
import br.com.dragonmc.core.bukkit.utils.floatingitem.impl.HeadCustomItem;
import br.com.dragonmc.core.bukkit.utils.hologram.Hologram;
import br.com.dragonmc.core.bukkit.utils.hologram.impl.SimpleHologram;
import br.com.dragonmc.core.bukkit.utils.item.ItemBuilder;
import br.com.dragonmc.core.common.utils.string.StringFormat;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public abstract class Generator {
    private Location location;
    private GeneratorType generatorType;
    private int level = 1;
    private long lastGenerate;
    private long generateTime = 4000L;
    private Hologram hologram;
    private CustomItem customItem;
    private ItemStack itemStack;
    private List<Location> dropsLocation;
    protected int dropIndex;
    private float yaw;

    public Generator(Location location, GeneratorType generatorType, ItemStack itemStack) {
        this.location = location.getBlock().getLocation().add(0.5, 0.5, 0.5);
        this.generatorType = generatorType;
        this.itemStack = itemStack;
        this.generateTime = (generatorType.getTimer() - 1) * 1000;
        this.dropsLocation = this.findBlocks();
    }

    public Location getDropLocation() {
        return this.location;
    }

    public void animate() {
        if (this.customItem instanceof HeadCustomItem) {
            HeadCustomItem headCustomItem = (HeadCustomItem)this.customItem;
            this.yaw = this.yaw >= 180.0f || this.yaw + 5.0f >= 180.0f ? -180.0f : this.yaw + 5.0f;
            PacketPlayOutEntity.PacketPlayOutEntityLook packetPlayOutEntityLook = new PacketPlayOutEntity.PacketPlayOutEntityLook(headCustomItem.getArmorStand().getEntityId(), (byte)(this.yaw * 256.0f / 360.0f), (byte) 0, false);
            for (Player player : Bukkit.getOnlinePlayers()) {
                ((CraftPlayer)player).getHandle().playerConnection.sendPacket((Packet)packetPlayOutEntityLook);
            }
        }
    }

    public Generator handleHologram() {
        if (this.generatorType != GeneratorType.NORMAL) {
            this.hologram = new SimpleHologram(this.generatorType.getColor() + "\u00a7n\u00a7%" + this.generatorType.name().toLowerCase() + "%\u00a7 I", this.location.clone().add(0.0, 1.0, 0.0));
            this.hologram.line("\u00a7a-/-");
            this.hologram.spawn();
            BukkitCommon.getInstance().getHologramManager().registerHologram(this.hologram);
            this.customItem = new HeadCustomItem(this.location.clone().add(0.0, 0.7, 0.0), new ItemBuilder().type(Material.valueOf((String)(this.getGeneratorType().name() + "_BLOCK"))).build()).spawn();
        }
        return this;
    }

    public void setLocation(Location location) {
        this.location = location;
        if (this.hologram != null) {
            this.hologram.teleport(location.clone().add(0.0, 1.8, 0.0));
            this.customItem.teleport(location.clone().add(0.0, 1.5, 0.0));
            this.handleHologram();
        }
    }

    public void setLevel(int level) {
        this.level = level;
        if (this.generatorType != GeneratorType.NORMAL && this.hologram != null) {
            this.hologram.setDisplayName(this.generatorType.getColor() + "\u00a7n\u00a7%" + this.generatorType.name().toLowerCase() + "%\u00a7 " + StringFormat.formatRomane(level));
        }
    }

    public void updateHologram() {
        if (this.hologram != null) {
            int seconds = (int)(this.lastGenerate + this.generateTime - System.currentTimeMillis()) / 1000;
            if (this.generatorType != GeneratorType.NORMAL && this.hologram != null) {
                ((Hologram)this.hologram.getLines().stream().findFirst().orElse(null)).setDisplayName("\u00a7eGera em \u00a7c" + (seconds < 0 ? 0 : seconds) + "\u00a7e.");
            }
        }
    }

    public void generate() {
        Location location = this.getDropLocation();
        int items = 0;
        for (Item item : location.getWorld().getEntitiesByClass(Item.class).stream().filter(entity -> entity.getLocation().distance(location) <= 5.0).collect(Collectors.toList())) {
            if (item.getItemStack().getType() != this.itemStack.getType()) continue;
            items += item.getItemStack().getAmount();
        }
        int multiplier = this.getLevel() - 1;
        switch (this.itemStack.getType()) {
            case IRON_INGOT: {
                if (items < 48 + multiplier * 16) break;
                return;
            }
            case GOLD_INGOT: {
                if (items < 12 + multiplier * 4) break;
                return;
            }
            case DIAMOND: {
                if (items < 4 + multiplier * 2) break;
                return;
            }
            case EMERALD: {
                if (items < 2 + multiplier * 2) break;
                return;
            }
        }
        Item dropItem = location.getWorld().dropItem(location.clone(), this.itemStack);
        dropItem.setVelocity(new Vector(0, 0, 0));
        this.lastGenerate = System.currentTimeMillis();
    }

    private List<Location> findBlocks() {
        List<Location> dropsLocation = GameMain.getInstance().getNearestBlocksByMaterial(this.location, Material.STEP, 4, 1);
        if (dropsLocation.isEmpty()) {
            dropsLocation = GameMain.getInstance().getNearestBlocksByMaterial(this.location, Material.IRON_BLOCK, 4, 1);
        }
        return dropsLocation.stream().map(location -> location.add(0.5, location.getBlock().getType() == Material.STEP ? 0.5 : 1.25, 0.5)).collect(Collectors.toList());
    }

    public Location getLocation() {
        return this.location;
    }

    public GeneratorType getGeneratorType() {
        return this.generatorType;
    }

    public int getLevel() {
        return this.level;
    }

    public long getLastGenerate() {
        return this.lastGenerate;
    }

    public long getGenerateTime() {
        return this.generateTime;
    }

    public Hologram getHologram() {
        return this.hologram;
    }

    public CustomItem getCustomItem() {
        return this.customItem;
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public List<Location> getDropsLocation() {
        return this.dropsLocation;
    }

    public int getDropIndex() {
        return this.dropIndex;
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setGenerateTime(long generateTime) {
        this.generateTime = generateTime;
    }
}

