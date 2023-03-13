/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  net.md_5.bungee.api.chat.BaseComponent
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.core.bukkit.command;

import com.google.common.base.Joiner;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;
import br.com.dragonmc.core.common.CommonConst;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.bukkit.utils.player.PlayerHelper;
import br.com.dragonmc.core.common.command.CommandSender;
import br.com.dragonmc.core.common.language.Language;
import br.com.dragonmc.core.common.member.Member;
import br.com.dragonmc.core.common.member.Profile;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;

public class BukkitCommandSender
implements CommandSender {
    private final org.bukkit.command.CommandSender sender;
    private UUID replyId;
    private boolean tellEnabled;

    @Override
    public UUID getUniqueId() {
        if (this.sender instanceof Player) {
            return ((Player)this.sender).getUniqueId();
        }
        return CommonConst.EMPTY_UNIQUE_ID;
    }

    @Override
    public String getSenderName() {
        if (this.sender instanceof Player) {
            return this.sender.getName();
        }
        return "CONSOLE";
    }

    @Override
    public void sendMessage(String message) {
        this.sender.sendMessage(PlayerHelper.translate(CommonPlugin.getInstance().getPluginInfo().getDefaultLanguage(), message));
    }

    @Override
    public void sendMessage(BaseComponent baseComponent) {
        if (this.sender instanceof Player) {
            ((Player)this.sender).spigot().sendMessage(baseComponent);
        } else {
            this.sender.sendMessage(PlayerHelper.translate(CommonPlugin.getInstance().getPluginInfo().getDefaultLanguage(), baseComponent.toLegacyText()));
        }
    }

    @Override
    public void sendMessage(BaseComponent ... baseComponent) {
        if (this.sender instanceof Player) {
            ((Player)this.sender).spigot().sendMessage(baseComponent);
        } else {
            this.sender.sendMessage(Joiner.on((String)"").join((Iterable)Arrays.asList(baseComponent).stream().map(str -> PlayerHelper.translate(CommonPlugin.getInstance().getPluginInfo().getDefaultLanguage(), str.toLegacyText())).collect(Collectors.toList())));
        }
    }

    public Player getPlayer() {
        return (Player)this.sender;
    }

    @Override
    public boolean isPlayer() {
        return this.sender instanceof Player;
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.sender.hasPermission(permission);
    }

    @Override
    public Language getLanguage() {
        return this.isPlayer() ? ((Member)this.getSender()).getLanguage() : CommonPlugin.getInstance().getPluginInfo().getDefaultLanguage();
    }

    @Override
    public boolean isStaff() {
        if (this.sender instanceof Player) {
            Member member = CommonPlugin.getInstance().getMemberManager().getMember(((Player)this.sender).getUniqueId());
            return member.getServerGroup().isStaff();
        }
        return true;
    }

    @Override
    public boolean isUserBlocked(Profile profile) {
        return this.isPlayer() ? ((Member)this.getSender()).isUserBlocked(profile) : false;
    }

    public BukkitCommandSender(org.bukkit.command.CommandSender sender) {
        this.sender = sender;
    }

    public org.bukkit.command.CommandSender getSender() {
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

