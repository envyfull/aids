/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.md_5.bungee.api.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 */
package br.com.dragonmc.lobby.core.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.dragonmc.lobby.core.CoreMain;
import br.com.dragonmc.lobby.core.gamer.Gamer;
import br.com.dragonmc.lobby.core.wadgets.Heads;
import br.com.dragonmc.lobby.core.wadgets.Particles;
import br.com.dragonmc.lobby.core.wadgets.Wadget;
import br.com.dragonmc.lobby.core.wadgets.Wings;
import br.com.dragonmc.core.bukkit.utils.item.ItemBuilder;
import br.com.dragonmc.core.bukkit.utils.menu.MenuInventory;
import br.com.dragonmc.core.bukkit.utils.menu.MenuItem;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class CosmeticsInventory {
    private int itemsPerPage = 21;

    public CosmeticsInventory(Player player, Wadget wadget, int page) {
        Gamer gamer = CoreMain.getInstance().getGamerManager().getGamer(player.getUniqueId());
        MenuInventory menuInventory = new MenuInventory("§7Cosméticos", (wadget == null) ? 3 : 6);

        if (wadget == null) {
            for (int i = 0; i < (Wadget.values()).length; i++) {
                Wadget actual = Wadget.values()[i];


                List<Enum<?>> list = Arrays.asList((actual == Wadget.HEADS) ? (Enum<?>[]) Heads.values() : ((actual == Wadget.CAPES) ?
                        (Enum<?>[]) Wings.values() : (Enum<?>[]) Particles.values()));
                long totalOwned = list.stream().filter(o -> (player.hasPermission("lobby.wadgets") || player.hasPermission(actual.name().toLowerCase() + "." + o.name().toLowerCase()))).count();
                menuInventory.setItem(11 + i * 2, (new ItemBuilder())
                        .name("§a" + actual.getName()).type(actual.getType())
                        .lore("", "§7Disponível: §f" + totalOwned + "/" + list.size()).build(), (p, inv, type, stack, slot) -> new CosmeticsInventory(player, actual, 1));
            }
        } else {
            List<MenuItem> items = new ArrayList<>();
            switch (wadget) {
                case HEADS:
                    for (Heads heads : Heads.values()) {
                        if (player.hasPermission("lobby.cosmetics") || player
                                .hasPermission(wadget.name().toLowerCase() + "." + heads.name().toLowerCase())) {
                            items.add(new MenuItem((new ItemBuilder()).type(Material.SKULL_ITEM).durability(3)
                                    .name("§a" + heads.getHeadName()).lore("", "§eClique aqui para selecionar."
                                    ).skin(heads.getValue(), "").build(), (p, inv, type, stack, slot) -> {
                                p.getInventory().setHelmet(stack);
                                p.closeInventory();
                                p.sendMessage("§aColetável ativado: Chapéu do " + heads.getHeadName());
                            }));
                        } else {
                            items.add(new MenuItem((new ItemBuilder())
                                    .type(Material.INK_SACK).durability(8).name("§a" + heads.getHeadName())
                                    .lore("", "§7Exclusivo para §aVIP", "", "§cVocê não possui esse item."
                                    ).skin(heads.getValue(), "").build()));
                        }
                    }
                    menuInventory
                            .setItem(new MenuItem((new ItemBuilder()).type(Material.BARRIER).name("§cRemover cabeça").build(), (p, inv, type, stack, s) -> {
                                p.getInventory().setHelmet(null);
                                p.closeInventory();
                            }), 49);
                    break;
                case CAPES:
                    for (Wings wing : Wings.values()) {
                        if (player.hasPermission("lobby.cosmetics") || player
                                .hasPermission(wadget.name().toLowerCase() + "." + wing.name().toLowerCase())) {
                            items.add(new MenuItem((new ItemBuilder()).type(Material.INK_SACK).durability(10)
                                    .name("§a" + wing.getName()).build(), (p, inv, type, stack, slot) -> {
                                gamer.setUsingParticle(true);
                                gamer.setWing(wing);
                                gamer.setCape(true);
                                gamer.setParticle(null);
                                player.closeInventory();
                                player.sendMessage("§aColetável ativado: " + ChatColor.stripColor(wing.getName()) + "!");
                            }));
                        } else {
                            items.add(new MenuItem(wing.getItem().name(wing.getName()).type(Material.INK_SACK).durability(8)
                                    .lore("", "§7Exclusivo para §aVIP", "", "§cVocê não possui esse item.").build()));
                        }
                    }
                    menuInventory
                            .setItem(new MenuItem((new ItemBuilder()).type(Material.BARRIER).name("§cRemover cabeça").build(), (p, inv, type, stack, s) -> {
                                gamer.setUsingParticle(false);
                                gamer.setCape(false);
                                gamer.setWing(null);
                                gamer.setParticle(null);
                                player.closeInventory();
                            }), 49);
                    break;

                case PARTICLES:
                    for (Particles particle : Particles.values()) {
                        if (player.hasPermission("lobby.cosmetics") || player
                                .hasPermission(wadget.name().toLowerCase() + "." + particle.name().toLowerCase())) {
                            items.add(new MenuItem((new ItemBuilder()).type(Material.INK_SACK).durability(10)
                                    .name("§a" + particle.getName()).build(), (p, inv, type, stack, slot) -> {
                                gamer.setUsingParticle(true);

                                gamer.setCape(false);

                                gamer.setWing(null);

                                gamer.setParticle(particle);

                                player.closeInventory();

                                player.sendMessage("§aColetável ativado: " + ChatColor.stripColor(particle.getName()) + "!");

                            }));

                        } else {

                            items.add(new MenuItem(particle.getItem().name(particle.getName()).type(Material.INK_SACK)
                                    .durability(8).lore("", "§7Exclusivo para §aVIP", "", "§cVocê não possui esse item."
                                    ).build()));

                        }

                    }
                    menuInventory
                            .setItem(new MenuItem((new ItemBuilder()).type(Material.BARRIER).name("§cRemover cabeça").build(), (p, inv, type, stack, s) -> {
                                gamer.setUsingParticle(false);
                                gamer.setCape(false);
                                gamer.setParticle(null);
                                player.closeInventory();
                            }), 49);

                    break;

            }


            int pageStart = 0;
            int pageEnd = this.itemsPerPage;

            if (page > 1) {
                pageStart = (page - 1) * this.itemsPerPage;
                pageEnd = page * this.itemsPerPage;

            }

            if (pageEnd > items.size()) {
                pageEnd = items.size();

            }

            int w = 10;

            for (int i = pageStart; i < pageEnd; i++) {
                MenuItem item = items.get(i);
                menuInventory.setItem(item, w);

                if (w % 9 == 7) {
                    w += 3;

                } else {

                    w++;

                }

            }
            if (page == 1) {
                menuInventory.setItem(47, (new ItemBuilder()).name("§cVoltar").type(Material.ARROW).build(), (p, inv, type, stack, slot) -> new CosmeticsInventory(player, null));

            } else {

                menuInventory.setItem(new MenuItem((new ItemBuilder())
                        .type(Material.ARROW).name("§aPágina " + (page - 1)).build(), (p, inv, type, stack, s) -> new CosmeticsInventory(player, wadget, page - 1)), 47);

            }


            if (Math.ceil((items.size() / this.itemsPerPage)) + 1.0D > page) {
                menuInventory.setItem(new MenuItem((new ItemBuilder())
                        .type(Material.ARROW).name("§aPágina " + (page + 1)).build(), (p, inventory, clickType, item, slot) -> new CosmeticsInventory(player, wadget, page + 1)), 51);

            }

        }


        menuInventory.open(player);

    }


    public CosmeticsInventory(Player player, Wadget wadget) {
        this(player, wadget, 1);

    }


    public CosmeticsInventory(Player player) {
        this(player, null, 1);

    }

}
