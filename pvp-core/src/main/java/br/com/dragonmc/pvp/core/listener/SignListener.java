/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.block.Sign
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.block.SignChangeEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 */
package br.com.dragonmc.pvp.core.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SignListener
implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getClickedBlock().getType() != Material.WALL_SIGN && event.getClickedBlock().getType() != Material.SIGN_POST) {
            return;
        }
        Player player = event.getPlayer();
        Sign sign = (Sign)event.getClickedBlock().getState();
        String[] lines = sign.getLines();
        if (lines[1].toLowerCase().contains("sopas")) {
            Inventory soup = Bukkit.createInventory(null, (int)54, (String)"\u00a77Sopas");
            for (int i = 0; i < 54; ++i) {
                soup.setItem(i, new ItemStack(Material.MUSHROOM_SOUP));
            }
            player.openInventory(soup);
        } else if (lines[1].toLowerCase().contains("recraft")) {
            Inventory recraft = Bukkit.createInventory(null, (int)9, (String)"\u00a77Sopas");
            recraft.setItem(3, new ItemStack(Material.BOWL, 64));
            recraft.setItem(4, new ItemStack(Material.RED_MUSHROOM, 64));
            recraft.setItem(5, new ItemStack(Material.BROWN_MUSHROOM, 64));
            player.openInventory(recraft);
        } else if (lines[1].toLowerCase().contains("cactus")) {
            Inventory cactu = Bukkit.createInventory(null, (int)9, (String)"\u00a77Sopas");
            cactu.setItem(3, new ItemStack(Material.BOWL, 64));
            cactu.setItem(4, new ItemStack(Material.CACTUS, 64));
            cactu.setItem(5, new ItemStack(Material.CACTUS, 64));
            player.openInventory(cactu);
        } else if (lines[1].toLowerCase().contains("cocoa")) {
            Inventory cocoa = Bukkit.createInventory(null, (int)9, (String)"\u00a77Cocoa");
            cocoa.setItem(2, new ItemStack(Material.BOWL, 64));
            cocoa.setItem(3, new ItemStack(Material.INK_SACK, 64, (short) 3));
            cocoa.setItem(5, new ItemStack(Material.BOWL, 64));
            cocoa.setItem(6, new ItemStack(Material.INK_SACK, 64, (short) 3));
            player.openInventory(cocoa);
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onSignChange(SignChangeEvent event) {
        String[] code;
        String line = event.getLine(0);
        if (line.equalsIgnoreCase("sopa") || line.equalsIgnoreCase("sopas")) {
            event.setLine(0, "\u00a7bDragon\u00a7fMC");
            event.setLine(1, "\u00a7bSopas");
            event.setLine(2, "\u00a76\u00a7m>-----<");
            event.setLine(3, " ");
        } else if (line.equalsIgnoreCase("recraft") || line.equalsIgnoreCase("recrafts")) {
            event.setLine(0, "\u00a7bDragon\u00a7fMC");
            event.setLine(1, "\u00a7eRecraft");
            event.setLine(2, "\u00a76\u00a7m>-----<");
            event.setLine(3, " ");
        } else if (line.equalsIgnoreCase("cocoa") || line.equalsIgnoreCase("cocoabean")) {
            event.setLine(0, "\u00a7bDragon\u00a7fMC");
            event.setLine(1, "\u00a7cCocoabean");
            event.setLine(2, "\u00a76\u00a7m>-----<");
            event.setLine(3, " ");
        } else if (line.equalsIgnoreCase("cactu") || line.equalsIgnoreCase("cactus")) {
            event.setLine(0, "\u00a7bDragon\u00a7fMC");
            event.setLine(1, "\u00a7aCactus");
            event.setLine(2, "\u00a76\u00a7m>-----<");
            event.setLine(3, " ");
        } else if (line.equalsIgnoreCase("dificil")) {
            event.setLine(0, "\u00a76Lava");
            event.setLine(1, "\u00a7c\u00a7lHARD");
            event.setLine(2, "\u00a76\u00a7m>-----<");
            event.setLine(3, " ");
        } else if (line.equalsIgnoreCase("facil")) {
            event.setLine(0, "\u00a76Lava");
            event.setLine(1, "\u00a7a\u00a7lEASY");
            event.setLine(2, "\u00a76\u00a7m>-----<");
            event.setLine(3, " ");
        } else if (line.equalsIgnoreCase("medio")) {
            event.setLine(0, "\u00a76Lava");
            event.setLine(1, "\u00a7e\u00a7lMEDIUM");
            event.setLine(2, "\u00a76\u00a7m>-----<");
            event.setLine(3, " ");
        } else if (line.equalsIgnoreCase("extreme")) {
            event.setLine(0, "\u00a76Lava");
            event.setLine(1, "\u00a74\u00a7lEXTREME");
            event.setLine(2, "\u00a76\u00a7m>-----<");
            event.setLine(3, " ");
        } else if (line.contains(":") && (code = line.split(":")).length > 1) {
            if (code[0].equalsIgnoreCase("money")) {
                try {
                    int quantity = Integer.valueOf(code[1]);
                    event.setLine(0, "\u00a76\u00a7lMOEDAS");
                    event.setLine(1, "\u00a7e\u00a7l" + quantity);
                    event.setLine(2, " ");
                    event.setLine(3, "\u00a7a\u00a7lClique!");
                }
                catch (NumberFormatException quantity) {}
            } else if (code[0].equalsIgnoreCase("ticket")) {
                try {
                    int quantity = Integer.valueOf(code[1]);
                    event.setLine(0, "\u00a7b\u00a7lTICKET");
                    event.setLine(1, "\u00a73\u00a7l" + quantity);
                    event.setLine(2, " ");
                    event.setLine(3, "\u00a7a\u00a7lClique!");
                }
                catch (NumberFormatException quantity) {}
            } else if (code[0].equalsIgnoreCase("doublexp")) {
                try {
                    int quantity = Integer.valueOf(code[1]);
                    event.setLine(0, "\u00a73\u00a7lDOUBLEXP");
                    event.setLine(1, "\u00a7b\u00a7l" + quantity);
                    event.setLine(2, " ");
                    event.setLine(3, "\u00a7a\u00a7lClique!");
                }
                catch (NumberFormatException numberFormatException) {
                    // empty catch block
                }
            }
        }
    }
}

