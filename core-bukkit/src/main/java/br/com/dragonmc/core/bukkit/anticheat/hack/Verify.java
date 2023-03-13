/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Listener
 *  org.bukkit.metadata.MetadataValue
 *  org.bukkit.plugin.Plugin
 */
package br.com.dragonmc.core.bukkit.anticheat.hack;

import br.com.dragonmc.core.bukkit.BukkitCommon;
import br.com.dragonmc.core.bukkit.anticheat.StormCore;
import br.com.dragonmc.core.bukkit.anticheat.gamer.UserData;
import br.com.dragonmc.core.bukkit.member.BukkitMember;
import br.com.dragonmc.core.common.CommonPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public interface Verify
extends Listener {
    public HackType getHackType();

    default public boolean isPlayerBypass(Player player) {
        return CommonPlugin.getInstance().getMemberManager().getMember(player.getUniqueId(), BukkitMember.class).isAnticheatBypass();
    }

    default public boolean isIgnore(Player player) {
        if (player.hasMetadata("anticheat-ignore")) {
            MetadataValue metadataValue = player.getMetadata("anticheat-ignore").stream().findFirst().orElse(null);
            if (metadataValue.asLong() > System.currentTimeMillis()) {
                return true;
            }
            player.removeMetadata("anticheat-ignore", (Plugin) BukkitCommon.getInstance());
        }
        return false;
    }

    default public boolean ignore(Player player, double seconds) {
        StormCore.getInstance().ignore(player, seconds);
        return true;
    }

    default public UserData getUserData(Player player) {
        return CommonPlugin.getInstance().getMemberManager().getMember(player.getUniqueId(), BukkitMember.class).getUserData();
    }

    default public void alert(Player player) {
        this.getUserData(player).pulse(this.getHackType());
    }

    default public void alert(Player player, String message) {
        this.getUserData(player).pulse(this.getHackType(), message);
    }
}

