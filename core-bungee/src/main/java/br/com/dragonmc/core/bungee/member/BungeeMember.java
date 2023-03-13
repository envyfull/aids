/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.BungeeTitle
 *  net.md_5.bungee.api.chat.BaseComponent
 *  net.md_5.bungee.api.chat.TextComponent
 *  net.md_5.bungee.api.connection.ProxiedPlayer
 */
package br.com.dragonmc.core.bungee.member;

import java.util.UUID;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.common.member.Member;
import br.com.dragonmc.core.common.member.configuration.LoginConfiguration;
import br.com.dragonmc.core.common.packet.types.ActionBar;
import net.md_5.bungee.BungeeTitle;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeMember
extends Member {
    private transient ProxiedPlayer proxiedPlayer;

    public BungeeMember(UUID uniqueId, String playerName, LoginConfiguration.AccountType accountType) {
        super(uniqueId, playerName, accountType);
    }

    @Override
    public boolean hasPermission(String permission) {
        if (this.proxiedPlayer != null && this.proxiedPlayer.hasPermission(permission.toLowerCase())) {
            return true;
        }
        return super.hasPermission(permission);
    }

    @Override
    public boolean hasReply() {
        return super.hasReply();
    }

    @Override
    public void sendMessage(String message) {
        if (this.proxiedPlayer != null) {
            this.proxiedPlayer.sendMessage(CommonPlugin.getInstance().getPluginInfo().findAndTranslate(this.getLanguage(), message));
        }
    }

    public void sendMessage(BaseComponent str) {
        if (this.proxiedPlayer != null) {
            this.proxiedPlayer.sendMessage(str);
        }
    }


    public void sendMessage(BaseComponent ... fromLegacyText) {
        if (this.proxiedPlayer != null) {
            this.proxiedPlayer.sendMessage(fromLegacyText);
        }
    }

    @Override
    public void sendTitle(String title, String subTitle, int fadeIn, int stayIn, int fadeOut) {
        if (this.proxiedPlayer != null) {
            BungeeTitle packet = new BungeeTitle();
            packet.title(TextComponent.fromLegacyText((String)title));
            packet.subTitle(TextComponent.fromLegacyText((String)subTitle));
            packet.fadeIn(fadeIn);
            packet.fadeOut(fadeOut);
            packet.stay(stayIn);
            packet.send(this.proxiedPlayer);
        }
    }

    @Override
    public void sendActionBar(String message) {
        if (this.proxiedPlayer != null) {
            CommonPlugin.getInstance().getServerData().sendPacket(new ActionBar(this.getUniqueId(), message).server(this.getActualServerId()));
        }
    }

    @Override
    public void saveConfig() {
        super.saveConfig();
        this.save("ipAddress", "lastIpAddress", "firstLogin", "lastLogin", "joinTime", "onlineTime", "online");
    }

    public ProxiedPlayer getProxiedPlayer() {
        return this.proxiedPlayer;
    }

    public void setProxiedPlayer(ProxiedPlayer proxiedPlayer) {
        this.proxiedPlayer = proxiedPlayer;
    }
}

