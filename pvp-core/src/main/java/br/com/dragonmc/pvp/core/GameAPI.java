/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.event.Listener
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.Recipe
 *  org.bukkit.inventory.ShapelessRecipe
 *  org.bukkit.plugin.Plugin
 */
package br.com.dragonmc.pvp.core;

import br.com.dragonmc.core.bukkit.BukkitCommon;
import br.com.dragonmc.pvp.core.gamer.Gamer;
import br.com.dragonmc.pvp.core.backend.GamerData;
import br.com.dragonmc.pvp.core.backend.impl.VoidGamerData;
import br.com.dragonmc.pvp.core.listener.DamageListener;
import br.com.dragonmc.pvp.core.listener.GamerListener;
import br.com.dragonmc.pvp.core.listener.PlayerListener;
import br.com.dragonmc.pvp.core.listener.SignListener;
import br.com.dragonmc.pvp.core.listener.WorldListener;
import br.com.dragonmc.pvp.core.manager.GamerManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.Plugin;

public class GameAPI
extends BukkitCommon {
    private static GameAPI instance;
    private GamerData gamerData;
    private Class<? extends Gamer> gamerClass;
    private GamerManager gamerManager;
    private boolean dropItems;
    private double protectionRadius;
    private boolean fullIron;
    private boolean fallDamageProtection = false;

    @Override
    public void onEnable() {
        instance = this;
        super.onEnable();
        this.protectionRadius = this.getConfig().getDouble("protectionRadius", 30.0);
        this.fullIron = this.getConfig().getBoolean("fullIron", true);
        this.gamerData = new VoidGamerData();
        this.gamerManager = new GamerManager();
        Bukkit.getPluginManager().registerEvents((Listener)new DamageListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new GamerListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new PlayerListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new SignListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new WorldListener(), (Plugin)this);
        this.loadSoups();
    }

    private void loadSoups() {
        ItemStack soup = new ItemStack(Material.MUSHROOM_SOUP);
        ShapelessRecipe cocoa = new ShapelessRecipe(soup);
        ShapelessRecipe cactus = new ShapelessRecipe(soup);
        ShapelessRecipe pumpkin = new ShapelessRecipe(soup);
        ShapelessRecipe melon = new ShapelessRecipe(soup);
        ShapelessRecipe flower = new ShapelessRecipe(soup);
        ShapelessRecipe nether = new ShapelessRecipe(soup);
        cocoa.addIngredient(Material.BOWL);
        cocoa.addIngredient(Material.INK_SACK, 3);
        cactus.addIngredient(Material.BOWL);
        cactus.addIngredient(Material.CACTUS);
        pumpkin.addIngredient(Material.BOWL);
        pumpkin.addIngredient(1, Material.PUMPKIN_SEEDS);
        melon.addIngredient(Material.BOWL);
        melon.addIngredient(1, Material.MELON_SEEDS);
        nether.addIngredient(Material.BOWL);
        nether.addIngredient(Material.getMaterial((int)372));
        flower.addIngredient(Material.BOWL);
        flower.addIngredient(Material.RED_ROSE);
        flower.addIngredient(Material.YELLOW_FLOWER);
        Bukkit.addRecipe((Recipe)cocoa);
        Bukkit.addRecipe((Recipe)cactus);
        Bukkit.addRecipe((Recipe)pumpkin);
        Bukkit.addRecipe((Recipe)melon);
        Bukkit.addRecipe((Recipe)nether);
        Bukkit.addRecipe((Recipe)flower);
    }

    public void setProtectionRadius(double protectionRadius) {
        this.protectionRadius = protectionRadius;
        this.getConfig().set("protectionRadius", (Object)protectionRadius);
        this.saveDefaultConfig();
    }

    public void setFullIron(boolean fullIron) {
        this.fullIron = fullIron;
        this.getConfig().set("fullIron", (Object)fullIron);
        this.saveDefaultConfig();
    }

    public GamerData getGamerData() {
        return this.gamerData;
    }

    public Class<? extends Gamer> getGamerClass() {
        return this.gamerClass;
    }

    public GamerManager getGamerManager() {
        return this.gamerManager;
    }

    public boolean isDropItems() {
        return this.dropItems;
    }

    public double getProtectionRadius() {
        return this.protectionRadius;
    }

    public boolean isFullIron() {
        return this.fullIron;
    }

    public boolean isFallDamageProtection() {
        return this.fallDamageProtection;
    }

    public static GameAPI getInstance() {
        return instance;
    }

    public void setGamerClass(Class<? extends Gamer> gamerClass) {
        this.gamerClass = gamerClass;
    }

    public void setDropItems(boolean dropItems) {
        this.dropItems = dropItems;
    }

    public void setFallDamageProtection(boolean fallDamageProtection) {
        this.fallDamageProtection = fallDamageProtection;
    }
}

