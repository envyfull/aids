/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.api.ChatColor
 */
package br.com.dragonmc.core.common.packet.types;

import java.util.UUID;

import br.com.dragonmc.core.common.member.Member;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.common.packet.Packet;
import br.com.dragonmc.core.common.packet.PacketType;
import net.md_5.bungee.api.ChatColor;

public class StaffchatBungeePacket
extends Packet {
    private UUID playerId;
    private String nickname;
    private String message;

    public StaffchatBungeePacket(UUID playerId, String nickname, String message) {
        super(PacketType.STAFFCHAT_BUNGEE);
        this.playerId = playerId;
        this.nickname = nickname;
        this.message = message;
        this.bungeecord();
    }

    @Override
    public void receive() {
        Member member = CommonPlugin.getInstance().getMemberManager().getMember(this.playerId);
        String staffMessage = member == null ? "\u00a77[StaffChat] " + this.nickname + "\u00a77: \u00a7f" + ChatColor.translateAlternateColorCodes((char)'&', (String)this.message) : "\u00a73*\u00a77[StaffChat] " + CommonPlugin.getInstance().getPluginInfo().getTagByGroup(member.getServerGroup()).getStrippedColor() + " " + member.getPlayerName() + "\u00a77: \u00a7f" + ChatColor.translateAlternateColorCodes((char)'&', (String)this.message);
        CommonPlugin.getInstance().getMemberManager().getMembers().stream().filter(Member::isStaff).forEach(m -> m.sendMessage(staffMessage));
    }

    public UUID getPlayerId() {
        return this.playerId;
    }

    public String getNickname() {
        return this.nickname;
    }

    public String getMessage() {
        return this.message;
    }
}

