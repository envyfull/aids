/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.api.ProxyServer
 *  net.md_5.bungee.api.chat.BaseComponent
 *  net.md_5.bungee.api.chat.ClickEvent$Action
 */
package br.com.dragonmc.core.bungee.command.register;

import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.common.command.CommandArgs;
import br.com.dragonmc.core.common.command.CommandClass;
import br.com.dragonmc.core.common.command.CommandFramework;
import br.com.dragonmc.core.common.member.Member;
import br.com.dragonmc.core.common.permission.Tag;
import br.com.dragonmc.core.common.utils.string.MessageBuilder;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;

public class MediaCommand
implements CommandClass {
    @CommandFramework.Command(name="youtube")
    public void youtubeCommand(CommandArgs cmdArgs) {
        Member sender = cmdArgs.getSenderAsMember();
        String[] args = cmdArgs.getArgs();
        if (args.length == 0) {
            sender.sendMessage(" \u00a7e\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " <youtube>\u00a7f para alterar seu link do youtube.");
            return;
        }
        String youtubeLink = args[0];
        sender.setYoutubeUrl(youtubeLink);
        sender.sendMessage(" \u00a7a\u00bb \u00a7fVoc\u00ea alterou seu link do youtube para \u00a7b" + youtubeLink + "\u00a7f.");
    }

    @CommandFramework.Command(name="twitch")
    public void twitchCommand(CommandArgs cmdArgs) {
        Member sender = cmdArgs.getSenderAsMember();
        String[] args = cmdArgs.getArgs();
        if (args.length == 0) {
            sender.sendMessage(" \u00a7e\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " <twitch>\u00a7f para alterar seu link da twitch.");
            return;
        }
        String twitchUrl = args[0];
        if (!twitchUrl.toLowerCase().contains("twitch.tv")) {
            twitchUrl = "twitch.tv/" + twitchUrl;
        }
        sender.setTwitchUrl(twitchUrl.toLowerCase());
        sender.sendMessage(" \u00a7a\u00bb \u00a7fVoc\u00ea alterou seu link da twitch para \u00a7d" + twitchUrl.toLowerCase() + "\u00a7f.");
    }

    @CommandFramework.Command(name="stream", permission="command.stream")
    public void streamCommand(CommandArgs cmdArgs) {
        Member sender = cmdArgs.getSenderAsMember();
        if (sender.hasTwitch()) {
            if (sender.hasCooldown("command-stream") && !sender.hasPermission("staff.super")) {
                sender.sendMessage("\u00a7cVoc\u00ea precisa esperar " + sender.getCooldownFormatted("command-stream") + " para usar esse comando novamente.");
                return;
            }
            Tag tag = CommonPlugin.getInstance().getPluginInfo().getTagByName(sender.getServerGroup().getGroupName());
            if (tag == null) {
                sender.sendMessage("\u00a7cO servidor n\u00e3o encontrou a sua tag.");
                return;
            }
            ProxyServer.getInstance().broadcast(" ");
            ProxyServer.getInstance().broadcast((BaseComponent)new MessageBuilder("\u00a76\u00a7LDRAGON \u00a78\u00bb \u00a7fO nosso " + tag.getRealPrefix() + sender.getName() + "\u00a7f est\u00e1 em live agora! \u00a7bClique aqui para acompanhar.").setHoverEvent("\u00a7eClique para abrir o link no navegador.").setClickEvent(ClickEvent.Action.OPEN_URL, sender.getTwitchUrl().toLowerCase().startsWith("http") ? sender.getTwitchUrl() : "https://" + sender.getTwitchUrl()).create());
            ProxyServer.getInstance().broadcast(" ");
            sender.putCooldown("command-stream", 300L);
        } else {
            sender.sendMessage("\u00a7cVoc\u00ea ainda n\u00e3o registrou a sua Twitch. Use /twitch");
        }
    }

    @CommandFramework.Command(name="record", aliases={"record"}, permission="command.record")
    public void recordCommand(CommandArgs cmdArgs) {
        Member sender = cmdArgs.getSenderAsMember();
        if (sender.hasCooldown("command-stream") && !sender.hasPermission("staff.super")) {
            sender.sendMessage("\u00a7cVoc\u00ea precisa esperar " + sender.getCooldownFormatted("command-stream") + " para usar esse comando novamente.");
            return;
        }
        Tag tag = CommonPlugin.getInstance().getPluginInfo().getTagByName(sender.getServerGroup().getGroupName());
        if (tag == null) {
            sender.sendMessage("\u00a7cO servidor n\u00e3o encontrou a sua tag.");
            return;
        }
        ProxyServer.getInstance().broadcast(" ");
        ProxyServer.getInstance().broadcast((BaseComponent)new MessageBuilder("\u00a76\u00a7lDRAGON \u00a78\u00bb \u00a7fO nosso " + tag.getRealPrefix() + sender.getName() + "\u00a7f est\u00e1 gravando no " + sender.getActualServerId() + "! \u00a7bClique aqui para se conectar.").setHoverEvent("\u00a7eClique aqui para se conectar.").setClickEvent(ClickEvent.Action.RUN_COMMAND, "/connect " + sender.getActualServerId()).create());
        ProxyServer.getInstance().broadcast(" ");
        sender.putCooldown("command-stream", 300L);
    }
}

