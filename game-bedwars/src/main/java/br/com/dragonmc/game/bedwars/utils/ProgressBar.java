/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  org.bukkit.ChatColor
 */
package br.com.dragonmc.game.bedwars.utils;

import com.google.common.base.Strings;
import org.bukkit.ChatColor;

public class ProgressBar {
    public static String getProgressBar(double current, double max, int totalBars, char symbol, ChatColor completedColor, ChatColor notCompletedColor) {
        float percent = (float)((double)((float)current) / max);
        int progressBars = (int)((float)totalBars * percent);
        return Strings.repeat((String)("" + completedColor + symbol), (int)progressBars) + Strings.repeat((String)("" + notCompletedColor + symbol), (int)(totalBars - progressBars));
    }
}

