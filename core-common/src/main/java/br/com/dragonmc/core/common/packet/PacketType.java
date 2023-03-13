/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.core.common.packet;

import br.com.dragonmc.core.common.packet.types.staff.Stafflog;
import br.com.dragonmc.core.common.packet.types.ActionBar;
import br.com.dragonmc.core.common.packet.types.BroadcastPacket;
import br.com.dragonmc.core.common.packet.types.PlayerMessagePacket;
import br.com.dragonmc.core.common.packet.types.PunishPlayerPacket;
import br.com.dragonmc.core.common.packet.types.ReportCreatePacket;
import br.com.dragonmc.core.common.packet.types.ReportDeletePacket;
import br.com.dragonmc.core.common.packet.types.ReportFieldPacket;
import br.com.dragonmc.core.common.packet.types.StaffchatBungeePacket;
import br.com.dragonmc.core.common.packet.types.StaffchatDiscordPacket;
import br.com.dragonmc.core.common.packet.types.configuration.ConfigurationFieldUpdate;
import br.com.dragonmc.core.common.packet.types.configuration.ConfigurationUpdate;
import br.com.dragonmc.core.common.packet.types.party.PartyCreate;
import br.com.dragonmc.core.common.packet.types.party.PartyDelete;
import br.com.dragonmc.core.common.packet.types.party.PartyField;
import br.com.dragonmc.core.common.packet.types.skin.SkinChange;
import br.com.dragonmc.core.common.packet.types.staff.TeleportToTarget;

public enum PacketType {
    PLAYER_MESSAGE(PlayerMessagePacket.class),
    PUNISH_PLAYER(PunishPlayerPacket.class),
    BROADCAST(BroadcastPacket.class),
    STAFFLOG(Stafflog.class),
    TELEPORT_TO_TARGET(TeleportToTarget.class),
    STAFFCHAT_DISCORD(StaffchatDiscordPacket.class),
    STAFFCHAT_BUNGEE(StaffchatBungeePacket.class),
    REPORT_CREATE(ReportCreatePacket.class),
    REPORT_DELETE(ReportDeletePacket.class),
    REPORT_FIELD(ReportFieldPacket.class),
    ACTION_BAR(ActionBar.class),
    SKIN_CHANGE(SkinChange.class),
    CONFIGURATION_UPDATE(ConfigurationUpdate.class),
    CONFIGURATION_FIELD_UPDATE(ConfigurationFieldUpdate.class),
    PARTY_CREATE(PartyCreate.class),
    PARTY_FIELD(PartyField.class),
    PARTY_DELETE(PartyDelete.class);

    private Class<? extends Packet> classType;

    public Class<? extends Packet> getClassType() {
        return this.classType;
    }

    private PacketType(Class<? extends Packet> classType) {
        this.classType = classType;
    }
}

