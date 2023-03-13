/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.api.chat.BaseComponent
 *  net.md_5.bungee.api.chat.TextComponent
 */
package br.com.dragonmc.core.common.packet.types.staff;

import java.util.Arrays;

import br.com.dragonmc.core.common.utils.string.MessageBuilder;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.common.packet.Packet;
import br.com.dragonmc.core.common.packet.PacketType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class Stafflog
extends Packet {
    private String message;
    private String hoverMessage;
    private String clickMessage;
    private boolean anticheat;

    public Stafflog(String message) {
        super(PacketType.STAFFLOG);
        this.bungeecord();
        this.message = message;
        this.hoverMessage = "";
        this.clickMessage = "";
    }

    public Stafflog anticheat() {
        this.anticheat = !this.anticheat;
        return this;
    }

    public Stafflog(TextComponent textComponent) {
        super(PacketType.STAFFLOG);
        this.bungeecord();
        this.message = textComponent.toLegacyText();
        this.hoverMessage = textComponent.getHoverEvent() != null && textComponent.getHoverEvent().getValue() != null ? Arrays.stream(textComponent.getHoverEvent().getValue()).map(xva$0 -> TextComponent.toLegacyText((BaseComponent[])new BaseComponent[]{xva$0})).reduce("", (a, b) -> a + b) : "";
        this.clickMessage = textComponent.getClickEvent() != null && textComponent.getClickEvent().getValue() != null ? textComponent.getClickEvent().getValue() : "";
    }

    @Override
    public void receive() {
        if (this.anticheat) {
            CommonPlugin.getInstance().getMemberManager().getMembers().stream().filter(member -> member.isStaff() && member.getMemberConfiguration().isAnticheatImportant()).forEach(member -> member.sendMessage((BaseComponent)new MessageBuilder(this.message).setHoverEvent(this.hoverMessage).setClickEvent(this.clickMessage).create()));
        } else {
            CommonPlugin.getInstance().getMemberManager().staffLog(new MessageBuilder(this.message).setHoverEvent(this.hoverMessage).setClickEvent(this.clickMessage).create());
        }
    }
}

