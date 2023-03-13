/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.api.CommandSender
 *  net.md_5.bungee.api.chat.BaseComponent
 *  net.md_5.bungee.api.chat.TextComponent
 *  net.md_5.bungee.api.connection.ProxiedPlayer
 */
package br.com.dragonmc.core.bungee.command;

import java.util.UUID;
import br.com.dragonmc.core.common.CommonConst;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.common.language.Language;
import br.com.dragonmc.core.common.member.Member;
import br.com.dragonmc.core.common.member.Profile;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeCommandSender
implements br.com.dragonmc.core.common.command.CommandSender {
    private final CommandSender sender;
    private UUID replyId;
    private boolean tellEnabled;

    @Override
    public UUID getUniqueId() {
        if (this.sender instanceof ProxiedPlayer) {
            return ((ProxiedPlayer)this.sender).getUniqueId();
        }
        return CommonConst.CONSOLE_ID;
    }

    @Override
    public String getName() {
        if (this.sender instanceof ProxiedPlayer) {
            return ((ProxiedPlayer)this.sender).getName();
        }
        return "CONSOLE";
    }

    @Override
    public void sendMessage(String str) {
        this.sender.sendMessage(TextComponent.fromLegacyText((String)this.translate(this.getLanguage(), str)));
    }

    @Override
    public void sendMessage(BaseComponent baseComponent) {
        this.sender.sendMessage(baseComponent);
    }

    @Override
    public void sendMessage(BaseComponent ... fromLegacyText) {
        this.sender.sendMessage(fromLegacyText);
    }

    @Override
    public boolean isPlayer() {
        return this.sender instanceof ProxiedPlayer;
    }

    @Override
    public String getSenderName() {
        return this.sender.getName();
    }

    @Override
    public boolean hasPermission(String permission) {
        if (this.sender instanceof ProxiedPlayer) {
            return CommonPlugin.getInstance().getMemberManager().getMember(this.getUniqueId()).hasPermission(permission);
        }
        return true;
    }

    @Override
    public Language getLanguage() {
        return this.isPlayer() ? ((Member)this.getSender()).getLanguage() : CommonPlugin.getInstance().getPluginInfo().getDefaultLanguage();
    }

    public String translate(Language language, String string) {
        return CommonPlugin.getInstance().getPluginInfo().findAndTranslate(language, string);
    }

    @Override
    public boolean isStaff() {
        if (this.sender instanceof ProxiedPlayer) {
            Member member = CommonPlugin.getInstance().getMemberManager().getMember(((ProxiedPlayer)this.sender).getUniqueId());
            return member.getServerGroup().isStaff();
        }
        return true;
    }

    @Override
    public boolean isUserBlocked(Profile profile) {
        return this.isPlayer() ? ((Member)this.getSender()).isUserBlocked(profile) : false;
    }

    public BungeeCommandSender(CommandSender sender) {
        this.sender = sender;
    }

    public CommandSender getSender() {
        return this.sender;
    }

    @Override
    public UUID getReplyId() {
        return this.replyId;
    }

    @Override
    public boolean isTellEnabled() {
        return this.tellEnabled;
    }

    @Override
    public void setReplyId(UUID replyId) {
        this.replyId = replyId;
    }

    @Override
    public void setTellEnabled(boolean tellEnabled) {
        this.tellEnabled = tellEnabled;
    }
}

