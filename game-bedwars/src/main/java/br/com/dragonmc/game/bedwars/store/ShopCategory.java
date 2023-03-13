/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 */
package br.com.dragonmc.game.bedwars.store;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import br.com.dragonmc.core.bukkit.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public enum ShopCategory {
    FAVORITES("Compra r\u00e1pida", Material.NETHER_STAR, new ArrayList<ShopItem>()),
    BLOCKS("Blocos", Material.WOOD, Arrays.asList(new ShopItem(new ItemBuilder().type(Material.WOOL).name("L\u00e3").amount(16).build(), new ShopPrice(Material.IRON_INGOT, 4)), new ShopItem(new ItemBuilder().type(Material.STAINED_CLAY).name("Argila").amount(16).build(), new ShopPrice(Material.IRON_INGOT, 12)), new ShopItem(new ItemBuilder().type(Material.STAINED_GLASS).name("Vidro \u00e0 prova de explos\u00f5es").amount(4).build(), new ShopPrice(Material.IRON_INGOT, 12)), new ShopItem(new ItemBuilder().type(Material.ENDER_STONE).name("Pedra do fim").amount(12).build(), new ShopPrice(Material.IRON_INGOT, 24)), new ShopItem(new ItemBuilder().type(Material.LADDER).name("Escada").amount(16).build(), new ShopPrice(Material.IRON_INGOT, 4)), new ShopItem(new ItemBuilder().type(Material.WOOD).name("Madeira").amount(16).build(), new ShopPrice(Material.GOLD_INGOT, 4)), new ShopItem(new ItemBuilder().type(Material.OBSIDIAN).name("Obsidian").amount(4).build(), new ShopPrice(Material.EMERALD, 4)))),
    SWORDS("Espadas", Material.GOLD_SWORD, Arrays.asList(new ShopItem(new ItemBuilder().type(Material.STONE_SWORD).name("Espada de Pedra").build(), new ShopPrice(Material.IRON_INGOT, 10)), new ShopItem(new ItemBuilder().type(Material.IRON_SWORD).name("Espada de Ferro").build(), new ShopPrice(Material.GOLD_INGOT, 7)), new ShopItem(new ItemBuilder().type(Material.DIAMOND_SWORD).name("Espada de Diamante").build(), new ShopPrice(Material.EMERALD, 4)), new ShopItem(new ItemBuilder().type(Material.STICK).enchantment(Enchantment.KNOCKBACK).name("Graveto com Repuls\u00e3o").build(), new ShopPrice(Material.GOLD_INGOT, 5)))),
    ARMOR("Armaduras", Material.CHAINMAIL_BOOTS, Arrays.asList(new ShopItem(new ItemBuilder().type(Material.CHAINMAIL_BOOTS).name("Armadura de Malha").build(), new ShopPrice(Material.IRON_INGOT, 30)), new ShopItem(new ItemBuilder().type(Material.IRON_BOOTS).name("Armadura de Ferro").build(), new ShopPrice(Material.GOLD_INGOT, 12)), new ShopItem(new ItemBuilder().type(Material.DIAMOND_BOOTS).name("Armadura de Diamante").build(), new ShopPrice(Material.EMERALD, 6)))),
    EQUIP("Equipamentos", Material.STONE_PICKAXE, Arrays.asList(new ShopItem(new ItemBuilder().type(Material.SHEARS).name("Tesoura").build(), new ShopPrice(Material.IRON_INGOT, 20)), new ShopItem(new ItemBuilder().type(Material.GOLD_PICKAXE).name("Picareta").build(), new ShopPrice(Material.IRON_INGOT, 1)), new ShopItem(new ItemBuilder().type(Material.GOLD_AXE).name("Machado").build(), new ShopPrice(Material.IRON_INGOT, 1)))),
    ARCHERS("Arcos", Material.BOW, Arrays.asList(new ShopItem(new ItemBuilder().type(Material.ARROW).name("Flecha").amount(8).build(), new ShopPrice(Material.GOLD_INGOT, 2)), new ShopItem(new ItemBuilder().type(Material.BOW).name("Arco").build(), new ShopPrice(Material.GOLD_INGOT, 12)), new ShopItem(new ItemBuilder().type(Material.BOW).name("Arco (For\u00e7a I)").enchantment(Enchantment.ARROW_DAMAGE).build(), new ShopPrice(Material.GOLD_INGOT, 24)), new ShopItem(new ItemBuilder().type(Material.BOW).name("Arco (For\u00e7a I e Impacto I)").enchantment(Enchantment.ARROW_DAMAGE).enchantment(Enchantment.ARROW_KNOCKBACK).build(), new ShopPrice(Material.EMERALD, 6)))),
    POTIONS("Po\u00e7\u00f5es", Material.BREWING_STAND_ITEM, Arrays.asList(new ShopItem(new ItemBuilder().name("Po\u00e7\u00e3o de Agilidade II (45 Segundos)").type(Material.POTION).potion(new PotionEffect(PotionEffectType.SPEED, 900, 1)).durability(8226).build(), new ShopPrice(Material.EMERALD, 1)), new ShopItem(new ItemBuilder().name("Po\u00e7\u00e3o de Invisibilidade (30 Segundos)").type(Material.POTION).potion(new PotionEffect(PotionEffectType.INVISIBILITY, 600, 0)).durability(8238).build(), new ShopPrice(Material.EMERALD, 2)), new ShopItem(new ItemBuilder().name("Po\u00e7\u00e3o de Super Pulo V (45 Segundos)").type(Material.POTION).potion(new PotionEffect(PotionEffectType.JUMP, 900, 4)).durability(8203).build(), new ShopPrice(Material.EMERALD, 1)))),
    UTILITIES("Utilidades", Material.TNT, Arrays.asList(new ShopItem(new ItemBuilder().type(Material.GOLDEN_APPLE).name("Ma\u00e7a dourada").build(), new ShopPrice(Material.GOLD_INGOT, 3)), new ShopItem(new ItemBuilder().type(Material.SNOW_BALL).name("Tra\u00e7a").build(), new ShopPrice(Material.IRON_INGOT, 40)), new ShopItem(new ItemBuilder().type(Material.FIREBALL).name("Bola de Fogo").build(), new ShopPrice(Material.IRON_INGOT, 40)), new ShopItem(new ItemBuilder().type(Material.MONSTER_EGG).name("Defesa dos Sonhos").build(), new ShopPrice(Material.IRON_INGOT, 120)), new ShopItem(new ItemBuilder().type(Material.TNT).name("TNT").build(), new ShopPrice(Material.GOLD_INGOT, 4)), new ShopItem(new ItemBuilder().type(Material.ENDER_PEARL).name("P\u00e9rola do Fim").build(), new ShopPrice(Material.EMERALD, 4)), new ShopItem(new ItemBuilder().type(Material.WATER_BUCKET).name("Balde de \u00c1gua").build(), new ShopPrice(Material.GOLD_INGOT, 3)), new ShopItem(new ItemBuilder().type(Material.MILK_BUCKET).name("Leite M\u00e1gico").build(), new ShopPrice(Material.GOLD_INGOT, 4)), new ShopItem(new ItemBuilder().type(Material.SPONGE).name("Esponja").amount(4).build(), new ShopPrice(Material.GOLD_INGOT, 3)), new ShopItem(new ItemBuilder().type(Material.EGG).name("Ovo").build(), new ShopPrice(Material.EMERALD, 2)), new ShopItem(new ItemBuilder().type(Material.COMPASS).name("Rastreador").build(), new ShopPrice(Material.EMERALD, 2))));

    private String name;
    private Material material;
    private List<ShopItem> shopItem;
    public static final Map<String, ShopCategory> MAP;

    public static ShopCategory getCategoryByName(String name) {
        return MAP.get(name.toLowerCase());
    }

    public static ShopCategory getCategoryByIcon(Material material) {
        return MAP.get(material.name().toLowerCase());
    }

    private ShopCategory(String name, Material material, List<ShopItem> shopItem) {
        this.name = name;
        this.material = material;
        this.shopItem = shopItem;
    }

    public String getName() {
        return this.name;
    }

    public Material getMaterial() {
        return this.material;
    }

    public List<ShopItem> getShopItem() {
        return this.shopItem;
    }

    static {
        MAP = new HashMap<String, ShopCategory>();
        for (ShopCategory category : ShopCategory.values()) {
            MAP.put(category.name().toLowerCase(), category);
            MAP.put(category.getMaterial().name().toLowerCase(), category);
        }
    }

    public static class ShopPrice {
        private Material material;
        private int amount;

        public ShopPrice(Material material, int amount) {
            this.material = material;
            this.amount = amount;
        }

        public Material getMaterial() {
            return this.material;
        }

        public int getAmount() {
            return this.amount;
        }
    }

    public static class ShopItem {
        private ItemStack stack;
        private ShopPrice price;

        public ShopItem(ItemStack stack, ShopPrice price) {
            this.stack = stack;
            this.price = price;
        }

        public ItemStack getStack() {
            return this.stack;
        }

        public ShopPrice getPrice() {
            return this.price;
        }
    }
}

