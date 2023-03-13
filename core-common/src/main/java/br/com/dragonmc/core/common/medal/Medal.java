/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.api.ChatColor
 */
package br.com.dragonmc.core.common.medal;

import java.util.List;
import net.md_5.bungee.api.ChatColor;

public class Medal {
    private String medalName;
    private String symbol;
    private String chatColor;
    private List<String> aliases;

    public ChatColor getChatColor() {
        return ChatColor.valueOf((String)this.chatColor);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Medal) {
            Medal medal = (Medal)obj;
            return medal.getMedalName().equals(this.getMedalName());
        }
        return super.equals(obj);
    }

    public String getMedalName() {
        return this.medalName;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public List<String> getAliases() {
        return this.aliases;
    }

    public Medal(String medalName, String symbol, String chatColor, List<String> aliases) {
        this.medalName = medalName;
        this.symbol = symbol;
        this.chatColor = chatColor;
        this.aliases = aliases;
    }
}

