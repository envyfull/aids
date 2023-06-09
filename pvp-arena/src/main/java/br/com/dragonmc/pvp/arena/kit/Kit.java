/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.HandlerList
 *  org.bukkit.event.Listener
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 */
package br.com.dragonmc.pvp.arena.kit;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import br.com.dragonmc.core.bukkit.utils.cooldown.Cooldown;
import br.com.dragonmc.core.common.language.Language;
import br.com.dragonmc.core.common.utils.DateUtils;
import br.com.dragonmc.core.common.utils.string.StringFormat;
import br.com.dragonmc.pvp.arena.GameMain;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public abstract class Kit
implements Listener {
    private String kitName;
    private String kitDescription;
    private Material kitType;
    private int price;
    private List<ItemStack> itemList;
    private boolean registred;
    private Set<UUID> playerSet;

    public Kit(String kitName, String kitDescription, Material kitType, int price, List<ItemStack> itemList) {
        this.kitName = kitName;
        this.kitDescription = kitDescription;
        this.kitType = kitType;
        this.price = price;
        this.itemList = itemList;
        this.playerSet = new HashSet<UUID>();
    }

    public void register() {
        if (this.registred) {
            return;
        }
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin) GameMain.getInstance());
        this.registred = true;
    }

    public void unregister() {
        if (!this.registred) {
            return;
        }
        HandlerList.unregisterAll((Listener)this);
        this.registred = false;
    }

    public String getName() {
        return this.kitName;
    }

    public void addPlayer(UUID playerId) {
        this.playerSet.add(playerId);
        this.register();
    }

    public void removePlayer(UUID playerId) {
        this.playerSet.remove(playerId);
        if (this.playerSet.isEmpty()) {
            this.unregister();
        }
    }

    public boolean hasAbility(Player player) {
        return this.playerSet.contains(player.getUniqueId());
    }

    public boolean isAbilityItem(ItemStack item) {
        if (item == null) {
            return false;
        }
        return this.itemList.contains(item);
    }

    public boolean isCooldown(Player player) {
        if (GameMain.getInstance().getCooldownManager().hasCooldown(player.getUniqueId(), "Kit " + StringFormat.formatString(this.getName()))) {
            Cooldown cooldown = GameMain.getInstance().getCooldownManager().getCooldown(player.getUniqueId(), "Kit " + StringFormat.formatString(this.getName()));
            if (cooldown == null) {
                return false;
            }
            String message = "\u00a7cVoc\u00ea precisa esperar " + DateUtils.formatDifference(Language.getLanguage(player.getUniqueId()), (long)cooldown.getRemaining()) + " para usar o Kit " + StringFormat.formatString(this.getName()) + " novamente!";
            player.sendMessage(message);
            return true;
        }
        return false;
    }

    public void addCooldown(Player player, long time) {
        GameMain.getInstance().getCooldownManager().addCooldown(player.getUniqueId(), "Kit " + StringFormat.formatString(this.getName()), time);
    }

    public void addCooldown(UUID uniqueId, long time) {
        GameMain.getInstance().getCooldownManager().addCooldown(uniqueId, "Kit " + StringFormat.formatString(this.getName()), time);
    }

    public void applyKit(Player player) {
        for (ItemStack item : this.itemList) {
            player.getInventory().addItem(new ItemStack[]{item});
        }
    }

    public String getKitName() {
        return this.kitName;
    }

    public String getKitDescription() {
        return this.kitDescription;
    }

    public Material getKitType() {
        return this.kitType;
    }

    public int getPrice() {
        return this.price;
    }

    public List<ItemStack> getItemList() {
        return this.itemList;
    }

    public boolean isRegistred() {
        return this.registred;
    }

    public Set<UUID> getPlayerSet() {
        return this.playerSet;
    }
}

