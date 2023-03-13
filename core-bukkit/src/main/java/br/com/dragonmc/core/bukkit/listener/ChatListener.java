/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.api.chat.BaseComponent
 *  net.md_5.bungee.api.chat.ClickEvent
 *  net.md_5.bungee.api.chat.ClickEvent$Action
 *  net.md_5.bungee.api.chat.HoverEvent
 *  net.md_5.bungee.api.chat.HoverEvent$Action
 *  net.md_5.bungee.api.chat.TextComponent
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.AsyncPlayerChatEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 */
package br.com.dragonmc.core.bukkit.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import br.com.dragonmc.core.BukkitConst;
import br.com.dragonmc.core.common.CommonConst;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.bukkit.BukkitCommon;
import br.com.dragonmc.core.common.language.Language;
import br.com.dragonmc.core.common.medal.Medal;
import br.com.dragonmc.core.common.member.Member;
import br.com.dragonmc.core.common.member.Profile;
import br.com.dragonmc.core.common.punish.Punish;
import br.com.dragonmc.core.common.punish.PunishType;
import br.com.dragonmc.core.common.utils.DateUtils;
import br.com.dragonmc.core.common.utils.string.MessageBuilder;
import br.com.dragonmc.core.common.utils.string.StringFormat;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ChatListener
implements Listener {
    public static final Pattern PATTERN = Pattern.compile("(\u00a7%question-(\\d+)%\u00a7)");
    private static Pattern urlFinderPattern = Pattern.compile("((https?):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)", 2);

    @EventHandler(priority=EventPriority.LOWEST)
    public void onAsyncPlayerChatLW(AsyncPlayerChatEvent event) {
        if (BukkitCommon.getInstance().getChatManager().containsChat(event.getPlayer().getUniqueId())) {
            String nextQuestion;
            event.setCancelled(true);
            Player player = event.getPlayer();
            String message = event.getMessage();
            boolean cancel = message.contains("cancel") || Language.getLanguage(player.getUniqueId()).t("cancel", new String[0]).equalsIgnoreCase(message);
            boolean validate = BukkitCommon.getInstance().getChatManager().validate(player.getUniqueId(), message);
            if ((validate || cancel) && (nextQuestion = BukkitCommon.getInstance().getChatManager().callback(player.getUniqueId(), event.getMessage(), cancel)) != null) {
                Matcher matcher = PATTERN.matcher(nextQuestion);
                while (matcher.find()) {
                    String replace = matcher.group();
                    String id = matcher.group(2).toLowerCase();
                    nextQuestion = nextQuestion.replace(replace, BukkitCommon.getInstance().getChatManager().getAnswers(player.getUniqueId(), StringFormat.parseInt(id).getAsInt() - 1));
                }
                if (BukkitCommon.getInstance().getChatManager().isClearChat(player.getUniqueId())) {
                    for (int i = 0; i < 100; ++i) {
                        player.sendMessage(" ");
                    }
                }
                player.sendMessage(nextQuestion);
            }
        }
    }

    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
    public void onAsyncPlayerChatL(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Member member = CommonPlugin.getInstance().getMemberManager().getMember(player.getUniqueId());
        if (member == null) {
            event.setCancelled(true);
            return;
        }
        Punish punish = member.getPunishConfiguration().getActualPunish(PunishType.MUTE);
        if (punish != null) {
            member.sendMessage((BaseComponent)new MessageBuilder(punish.getMuteMessage(member.getLanguage())).setHoverEvent("\u00a7fPunido em: \u00a77" + CommonConst.DATE_FORMAT.format(punish.getCreatedAt()) + "\n\u00a7fExpire em: \u00a77" + (punish.isPermanent() ? "\u00a7cnunca" : DateUtils.formatDifference(member.getLanguage(), punish.getExpireAt() / 1000L))).create());
            event.setCancelled(true);
            return;
        }
        switch (BukkitCommon.getInstance().getChatState()) {
            case ENABLED: {
                break;
            }
            case DISABLED: {
                if (member.hasPermission("chat.disabled-say")) break;
                member.sendMessage("\u00a7cVoc\u00ea n\u00e3o pode falar no chat no momento, somente membros da equipe.");
                event.setCancelled(true);
                return;
            }
            case YOUTUBER: {
                if (member.hasPermission("chat.youtuber-say")) break;
                member.sendMessage("\u00a7cVoc\u00ea n\u00e3o pode falar no chat no momento, somente celebridades do servidor.");
                event.setCancelled(true);
                return;
            }
            case PAYMENT: {
                if (member.hasPermission("chat.payment-say")) break;
                member.sendMessage("\u00a7cVoc\u00ea n\u00e3o pode falar no chat no momento, somente jogadores pagantes.");
                event.setCancelled(true);
                return;
            }
        }
        if (member.hasCooldown("chat-cooldown") && !member.hasPermission("command.admin")) {
            member.sendActionBar("\u00a7cVoc\u00ea precisa esperar " + member.getCooldownFormatted("chat-cooldown") + " para falar no chat novamente.");
            event.setCancelled(true);
            return;
        }
        member.putCooldown("chat-cooldown", 3.5);
        if (!member.hasPermission("command.admin")) {
            for (String string : event.getMessage().split(" ")) {
                if (!BukkitConst.SWEAR_WORDS.contains(string.toLowerCase())) continue;
                StringBuilder stringBuilder = new StringBuilder();
                for (int x = 0; x < string.length(); ++x) {
                    stringBuilder.append('*');
                }
                event.setMessage(event.getMessage().replace(string, stringBuilder.toString().trim()));
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onAsyncPlayerChatN(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        Player player = event.getPlayer();
        Member member = CommonPlugin.getInstance().getMemberManager().getMember(player.getUniqueId());
        String message = event.getMessage();
        MessageBuilder messageBuilder = new MessageBuilder("");
        Medal medal = member.getMedal();
        if (medal != null) {
            messageBuilder.extra(new MessageBuilder(medal.getChatColor() + medal.getSymbol() + " ").setHoverEvent("\u00a7aMedalha: " + medal.getChatColor() + medal.getMedalName()).create());
        }
        messageBuilder.extra(new MessageBuilder(member.getTag().getRealPrefix() + player.getName()).setHoverEvent("\u00a7a" + player.getName() + "\n\n\u00a7fMedalha: " + (medal == null ? "\u00a77Nenhuma" : medal.getChatColor() + medal.getSymbol()) + "\n\u00a7fTempo de sess\u00e3o atual: \u00a77" + DateUtils.formatDifference(member.getLanguage(), member.getSessionTime() / 1000L) + "\n\n\u00a7eMensagem enviada \u00e0s " + CommonConst.TIME_FORMAT.format(System.currentTimeMillis()) + ".").create());
        messageBuilder.extra(" \u00a77\u00bb \u00a7f");
        String[] split = event.getMessage().split(" ");
        String currentColor = "\u00a7f";
        for (int x = 0; x < split.length; ++x) {
            String msg = (x > 0 ? " " : "") + currentColor + split[x];
            List<String> links = ChatListener.extractUrls(msg);
            if (links.isEmpty()) {
                messageBuilder.extra(new MessageBuilder(msg).create());
            } else {
                String url = ((String)links.stream().findFirst().orElse(null)).toLowerCase();
                if (!(url.contains("you") || url.contains("twitch") || member.hasPermission("command.admin"))) {
                    member.sendMessage("\u00a7cN\u00e3o \u00e9 permitido enviar links.");
                    event.setCancelled(true);
                    return;
                }
                messageBuilder.extra(new MessageBuilder(msg).setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url)).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText((String)url))).create());
            }
            currentColor = ChatColor.getLastColors((String)msg);
        }
        TextComponent textComponent = messageBuilder.create();
        event.getRecipients().removeIf(recipient -> {
            Member memberRecipient = CommonPlugin.getInstance().getMemberManager().getMember(recipient.getUniqueId());
            if (memberRecipient == null || memberRecipient.getUniqueId().equals(member.getUniqueId())) {
                return false;
            }
            if (!memberRecipient.getMemberConfiguration().isSeeingChat() && !member.hasPermission("staff.seechat-ignore")) {
                return true;
            }
            return memberRecipient.isUserBlocked(Profile.from(member)) && !member.hasPermission("staff.seechat-ignore");
        });
        event.getRecipients().forEach(recipient -> recipient.spigot().sendMessage((BaseComponent)textComponent));
        Bukkit.getConsoleSender().sendMessage(member.getPlayerName() + ": " + message);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        BukkitCommon.getInstance().getChatManager().remove(event.getPlayer().getUniqueId());
    }

    public static List<String> extractUrls(String text) {
        ArrayList<String> containedUrls = new ArrayList<String>();
        Matcher urlMatcher = urlFinderPattern.matcher(text);
        while (urlMatcher.find()) {
            containedUrls.add(urlMatcher.group(1));
        }
        return containedUrls;
    }
}

