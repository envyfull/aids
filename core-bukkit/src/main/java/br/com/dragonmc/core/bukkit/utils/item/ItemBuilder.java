/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.utility.MinecraftReflection
 *  com.comphenix.protocol.wrappers.nbt.NbtCompound
 *  com.comphenix.protocol.wrappers.nbt.NbtFactory
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.properties.Property
 *  org.bukkit.Bukkit
 *  org.bukkit.Color
 *  org.bukkit.Material
 *  org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemFlag
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.inventory.meta.LeatherArmorMeta
 *  org.bukkit.inventory.meta.PotionMeta
 *  org.bukkit.inventory.meta.SkullMeta
 *  org.bukkit.potion.PotionEffect
 */
package br.com.dragonmc.core.bukkit.utils.item;

import br.com.dragonmc.core.bukkit.utils.StringLoreUtils;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import br.com.dragonmc.core.common.utils.skin.Skin;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;

public class ItemBuilder {
    private Material material = Material.STONE;
    private int amount = 1;
    private short durability = 0;
    private boolean useMeta = false;
    private boolean glow = false;
    private String displayName;
    private Map<Enchantment, Integer> enchantments;
    private List<PotionEffect> potions;
    private List<String> lore;
    private Color color;
    private String skinOwner;
    private Skin skin;
    private String skinUrl;
    private boolean hideAttributes = false;
    private boolean unbreakable = false;
    private List<ItemFlag> itemFlags;

    public ItemBuilder flag(ItemFlag itemFlag) {
        if (this.itemFlags == null) {
            this.itemFlags = new ArrayList<ItemFlag>();
        }
        this.itemFlags.add(itemFlag);
        if (!this.useMeta) {
            this.useMeta = true;
        }
        return this;
    }

    public ItemBuilder flag(Set<ItemFlag> itemFlags) {
        if (this.itemFlags == null) {
            this.itemFlags = new ArrayList<ItemFlag>();
        }
        this.itemFlags.addAll(itemFlags);
        if (!this.useMeta) {
            this.useMeta = true;
        }
        return this;
    }

    public ItemBuilder type(Material material) {
        this.material = material;
        return this;
    }

    public ItemBuilder amount(int amount) {
        if (amount <= 0) {
            amount = 1;
        }
        this.amount = amount;
        return this;
    }

    public ItemBuilder durability(short durability) {
        this.durability = durability;
        return this;
    }

    public ItemBuilder durability(int durability) {
        this.durability = (short)durability;
        return this;
    }

    public ItemBuilder name(String text) {
        if (!this.useMeta) {
            this.useMeta = true;
        }
        this.displayName = text.replace("&", "\u00a7");
        return this;
    }

    public ItemBuilder enchantment(Enchantment enchantment) {
        return this.enchantment(enchantment, 1);
    }

    public ItemBuilder enchantment(Enchantment enchantment, Integer level) {
        if (this.enchantments == null) {
            this.enchantments = new HashMap<Enchantment, Integer>();
        }
        if (level == 0) {
            return this;
        }
        this.enchantments.put(enchantment, level);
        return this;
    }

    public ItemBuilder enchantment(Map<Enchantment, Integer> enchantments) {
        if (this.enchantments == null) {
            this.enchantments = new HashMap<Enchantment, Integer>();
        }
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            this.enchantments.put(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public ItemBuilder clearLore() {
        if (!this.useMeta) {
            this.useMeta = true;
        }
        if (this.lore != null) {
            this.lore.clear();
        }
        return this;
    }

    public ItemBuilder lore(String text) {
        if (!this.useMeta) {
            this.useMeta = true;
        }
        if (this.lore == null) {
            this.lore = new ArrayList<String>(StringLoreUtils.getLore(30, text));
        } else {
            this.lore.addAll(StringLoreUtils.getLore(30, text));
        }
        return this;
    }

    public ItemBuilder lore(String ... lore) {
        return this.lore(Arrays.asList(lore));
    }

    public ItemBuilder lore(List<String> text) {
        if (!this.useMeta) {
            this.useMeta = true;
        }
        if (this.lore == null) {
            this.lore = new ArrayList<String>();
        }
        for (String str : text) {
            this.lore.add(str.replace("&", "\u00a7"));
        }
        return this;
    }

    public ItemBuilder potion(PotionEffect potionEffect) {
        if (!this.useMeta) {
            this.useMeta = true;
        }
        if (this.potions == null) {
            this.potions = new ArrayList<PotionEffect>();
        }
        this.potions.add(potionEffect);
        return this;
    }

    public ItemBuilder potion(List<PotionEffect> potions) {
        if (potions == null) {
            return this;
        }
        if (!this.useMeta) {
            this.useMeta = true;
        }
        if (this.potions == null) {
            this.potions = new ArrayList<PotionEffect>();
        }
        this.potions.addAll(potions);
        return this;
    }

    public ItemBuilder glow() {
        this.glow = true;
        return this;
    }

    public ItemBuilder color(Color color) {
        this.useMeta = true;
        this.color = color;
        return this;
    }

    public ItemBuilder skin(String skin) {
        this.useMeta = true;
        this.skinOwner = skin;
        return this;
    }

    public ItemBuilder skin(Skin skin) {
        this.useMeta = true;
        this.skin = skin;
        return this;
    }

    public ItemBuilder skin(Player player) {
        this.useMeta = true;
        GameProfile gameProfile = ((CraftPlayer)player).getHandle().getProfile();
        Property property = gameProfile.getProperties().get("textures").stream().findFirst().orElse(null);
        this.skin = new Skin(player.getName(), property.getValue(), property.getSignature());
        return this;
    }

    public ItemBuilder skin(String value, String signature) {
        this.useMeta = true;
        this.skin = new Skin("none", value, signature);
        return this;
    }

    public ItemBuilder skinURL(String skinURL) {
        this.useMeta = true;
        this.skinUrl = skinURL;
        return this;
    }

    public ItemBuilder hideAttributes() {
        this.useMeta = true;
        this.hideAttributes = true;
        return this;
    }

    public ItemBuilder showAttributes() {
        this.useMeta = true;
        this.hideAttributes = false;
        return this;
    }

    public ItemBuilder unbreakable() {
        this.unbreakable = true;
        return this;
    }

    public ItemStack build() {
        ItemStack stack = new ItemStack(this.material, this.amount, this.durability);
        if (this.enchantments != null && !this.enchantments.isEmpty()) {
            for (Map.Entry<Enchantment, Integer> entry : this.enchantments.entrySet()) {
                stack.addUnsafeEnchantment(entry.getKey(), entry.getValue().intValue());
            }
        }
        if (this.useMeta) {
            ItemMeta meta = stack.getItemMeta();
            if (this.displayName != null) {
                meta.setDisplayName(this.displayName.replace("&", "\u00a7"));
            }
            if (this.lore != null && !this.lore.isEmpty()) {
                meta.setLore(this.lore);
            }
            if (this.color != null && meta instanceof LeatherArmorMeta) {
                ((LeatherArmorMeta)meta).setColor(this.color);
            }
            if (this.potions != null && meta instanceof PotionMeta) {
                PotionMeta potionMeta = (PotionMeta)meta;
                for (PotionEffect potionEffect : this.potions) {
                    potionMeta.addCustomEffect(potionEffect, true);
                }
            }
            if (meta instanceof SkullMeta) {
                Field field;
                GameProfile profile;
                SkullMeta skullMeta = (SkullMeta)meta;
                if (this.skin != null) {
                    profile = new GameProfile(this.skin.getUniqueId() == null ? UUID.randomUUID() : this.skin.getUniqueId(), this.skin.getPlayerName());
                    profile.getProperties().put("textures", new Property("textures", this.skin.getValue(), this.skin.getSignature()));
                    try {
                        field = skullMeta.getClass().getDeclaredField("profile");
                        field.setAccessible(true);
                        field.set(skullMeta, profile);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (this.skinUrl != null) {
                    profile = new GameProfile(this.skin.getUniqueId() == null ? UUID.randomUUID() : this.skin.getUniqueId(), this.skin.getPlayerName());
                    profile.getProperties().put("textures", new Property("textures", Base64.getEncoder().encodeToString(String.format("{textures:{SKIN:{url:\"%s\"}}}", this.skinUrl).getBytes(StandardCharsets.UTF_8))));
                    try {
                        field = skullMeta.getClass().getDeclaredField("profile");
                        field.setAccessible(true);
                        field.set(skullMeta, profile);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (this.skinOwner != null) {
                    Player player = Bukkit.getPlayer((String)this.skinOwner);
                    if (player == null) {
                        skullMeta.setOwner(this.skinOwner);
                    } else {
                        try {
                            field = skullMeta.getClass().getDeclaredField("profile");
                            field.setAccessible(true);
                            field.set(skullMeta, ((CraftPlayer)player).getHandle().getProfile());
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            meta.spigot().setUnbreakable(this.unbreakable);
            if (this.hideAttributes) {
                meta.addItemFlags(ItemFlag.values());
            } else {
                meta.removeItemFlags(ItemFlag.values());
            }
            if (this.itemFlags != null) {
                meta.addItemFlags((ItemFlag[])this.itemFlags.stream().toArray(ItemFlag[]::new));
            }
            stack.setItemMeta(meta);
        }
        if (this.glow && (this.enchantments == null || this.enchantments.isEmpty())) {
            try {
                Constructor caller = MinecraftReflection.getCraftItemStackClass().getDeclaredConstructor(ItemStack.class);
                caller.setAccessible(true);
                ItemStack item = (ItemStack)caller.newInstance(stack);
                NbtCompound compound = (NbtCompound)NbtFactory.fromItemTag((ItemStack)item);
                compound.put(NbtFactory.ofList((String)"ench", (Object[])new Object[0]));
                return item;
            }
            catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        this.material = Material.STONE;
        this.amount = 1;
        this.durability = 0;
        if (this.useMeta) {
            this.useMeta = false;
        }
        if (this.glow) {
            this.glow = false;
        }
        if (this.hideAttributes) {
            this.hideAttributes = false;
        }
        if (this.unbreakable) {
            this.unbreakable = false;
        }
        if (this.displayName != null) {
            this.displayName = null;
        }
        if (this.enchantments != null) {
            this.enchantments.clear();
            this.enchantments = null;
        }
        if (this.lore != null) {
            this.lore.clear();
            this.lore = null;
        }
        this.skinOwner = null;
        this.skinUrl = null;
        this.color = null;
        return stack;
    }

    public static ItemStack glow(ItemStack stack) {
        try {
            Constructor caller = MinecraftReflection.getCraftItemStackClass().getDeclaredConstructor(ItemStack.class);
            caller.setAccessible(true);
            ItemStack item = (ItemStack)caller.newInstance(stack);
            NbtCompound compound = (NbtCompound)NbtFactory.fromItemTag((ItemStack)item);
            compound.put(NbtFactory.ofList((String)"ench", (Object[])new Object[0]));
            return item;
        }
        catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
            e.printStackTrace();
            return stack;
        }
    }

    public static ItemBuilder fromStack(ItemStack stack) {
        ItemBuilder builder = new ItemBuilder().type(stack.getType()).amount(stack.getAmount()).durability(stack.getDurability());
        if (stack.hasItemMeta()) {
            Color color;
            ItemMeta meta = stack.getItemMeta();
            builder.flag(meta.getItemFlags());
            if (meta.hasDisplayName()) {
                builder.name(meta.getDisplayName());
            }
            if (meta.hasLore()) {
                builder.lore(meta.getLore());
            }
            if (meta instanceof LeatherArmorMeta && (color = ((LeatherArmorMeta)meta).getColor()) != null) {
                builder.color(color);
            }
            if (meta instanceof SkullMeta) {
                SkullMeta sm = (SkullMeta)meta;
                if (sm.hasOwner()) {
                    builder.skin(sm.getOwner());
                }
            } else if (meta instanceof PotionMeta) {
                PotionMeta potionMeta = (PotionMeta)meta;
                builder.potion(potionMeta.getCustomEffects());
            }
            for (Map.Entry entry : meta.getEnchants().entrySet()) {
                builder.enchantment((Enchantment)entry.getKey(), (Integer)entry.getValue());
            }
        }
        return builder;
    }

    public Material getMaterial() {
        return this.material;
    }

    public int getAmount() {
        return this.amount;
    }

    public short getDurability() {
        return this.durability;
    }

    public boolean isUseMeta() {
        return this.useMeta;
    }

    public boolean isGlow() {
        return this.glow;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public Map<Enchantment, Integer> getEnchantments() {
        return this.enchantments;
    }

    public List<PotionEffect> getPotions() {
        return this.potions;
    }

    public List<String> getLore() {
        return this.lore;
    }

    public Color getColor() {
        return this.color;
    }

    public String getSkinOwner() {
        return this.skinOwner;
    }

    public Skin getSkin() {
        return this.skin;
    }

    public String getSkinUrl() {
        return this.skinUrl;
    }

    public boolean isHideAttributes() {
        return this.hideAttributes;
    }

    public boolean isUnbreakable() {
        return this.unbreakable;
    }

    public List<ItemFlag> getItemFlags() {
        return this.itemFlags;
    }
}

