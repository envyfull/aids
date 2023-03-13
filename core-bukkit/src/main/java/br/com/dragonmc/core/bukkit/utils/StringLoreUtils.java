/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 */
package br.com.dragonmc.core.bukkit.utils;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;

public class StringLoreUtils {
    public static List<String> formatForLore(String text) {
        return StringLoreUtils.getLore(30, text);
    }

    public static List<String> getLore(int max, String text) {
        ArrayList<String> lore = new ArrayList<String>();
        text = ChatColor.translateAlternateColorCodes((char)'&', (String)text);
        String[] split = text.split(" ");
        String color = "";
        text = "";
        for (int i = 0; i < split.length; ++i) {
            String toAdd;
            if (ChatColor.stripColor((String)text).length() >= max || ChatColor.stripColor((String)text).endsWith(".") || ChatColor.stripColor((String)text).endsWith("!")) {
                lore.add(text);
                if (text.endsWith(".") || text.endsWith("!")) {
                    lore.add("");
                }
                text = color;
            }
            if ((toAdd = split[i]).contains("\u00a7")) {
                color = ChatColor.getLastColors((String)toAdd.toLowerCase());
            }
            if (toAdd.contains("\n")) {
                toAdd = toAdd.substring(0, toAdd.indexOf("\n"));
                split[i] = split[i].substring(toAdd.length() + 1);
                lore.add(text + (text.length() == 0 ? "" : " ") + toAdd);
                text = color;
                --i;
                continue;
            }
            text = text + (ChatColor.stripColor((String)text).length() == 0 ? "" : " ") + toAdd;
        }
        lore.add(text);
        return lore;
    }
}

