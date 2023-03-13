/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.api.chat.BaseComponent
 */
package br.com.dragonmc.core.common.command;

import java.util.UUID;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.common.language.Language;
import br.com.dragonmc.core.common.member.Profile;
import br.com.dragonmc.core.common.permission.Group;
import net.md_5.bungee.api.chat.BaseComponent;

public interface CommandSender {
    public UUID getUniqueId();

    public String getSenderName();

    public void sendMessage(String var1);

    public void sendMessage(BaseComponent var1);

    public void sendMessage(BaseComponent ... var1);

    public boolean hasPermission(String var1);

    public void setTellEnabled(boolean var1);

    public boolean isTellEnabled();

    public void setReplyId(UUID var1);

    public UUID getReplyId();

    public boolean isPlayer();

    public boolean isStaff();

    public boolean isUserBlocked(Profile var1);

    default public Group getServerGroup() {
        if (this.isPlayer()) {
            return null;
        }
        return CommonPlugin.getInstance().getPluginInfo().getHighGroup();
    }

    default public String getName() {
        return this.getSenderName();
    }

    default public boolean hasReply() {
        return this.getReplyId() != null;
    }

    public Language getLanguage();
}

