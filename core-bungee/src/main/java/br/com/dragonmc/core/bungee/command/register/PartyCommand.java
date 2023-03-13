/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  net.md_5.bungee.api.ProxyServer
 *  net.md_5.bungee.api.chat.BaseComponent
 *  net.md_5.bungee.api.config.ServerInfo
 */
package br.com.dragonmc.core.bungee.command.register;

import com.google.common.base.Joiner;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;
import java.util.UUID;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.bungee.member.BungeeMember;
import br.com.dragonmc.core.bungee.member.BungeeParty;
import br.com.dragonmc.core.common.command.CommandArgs;
import br.com.dragonmc.core.common.command.CommandClass;
import br.com.dragonmc.core.common.command.CommandFramework;
import br.com.dragonmc.core.common.manager.PartyManager;
import br.com.dragonmc.core.common.member.Member;
import br.com.dragonmc.core.common.member.Profile;
import br.com.dragonmc.core.common.member.party.Party;
import br.com.dragonmc.core.common.member.party.PartyRole;
import br.com.dragonmc.core.common.utils.string.MessageBuilder;
import br.com.dragonmc.core.common.utils.string.StringFormat;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ServerInfo;

public class PartyCommand
implements CommandClass {
    @CommandFramework.Command(name="partychat", aliases={"pc", "partychat", "party.chat"}, console=false)
    public void partychatCommand(CommandArgs cmdArgs) {
        Member sender = (Member)cmdArgs.getSender();
        Object[] args = cmdArgs.getArgs();
        if (args.length == 0) {
            sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " <mensagem>\u00a7f para mandar uma mensagem na party.");
            return;
        }
        if (sender.getParty() == null) {
            sender.sendMessage("\u00a7cVoc\u00ea n\u00e3o est\u00e1 em uma party.");
            return;
        }
        sender.getParty().chat(sender, Joiner.on((char)' ').join(args));
    }

    @CommandFramework.Command(name="party", console=false)
    public void partyCommand(CommandArgs cmdArgs) {
        Member sender = (Member)cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();
        if (args.length == 0) {
            sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " <disband/acabar>\u00a7f para acabar com sua party.");
            sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " convidar <player>\u00a7f para convidar algum jogador para sua party.");
            sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " expulsar <player>\u00a7f para expulsar algu\u00e9m da sua party.");
            sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " abrir <m\u00e1ximo de players>\u00a7f para abrir sua party para uma quantidade de pessoas.");
            sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " fechar\u00a7f para fechar sua party.");
            sender.sendMessage("");
            sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " aceitar <player>\u00a7f para aceitar um convite de party.");
            sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " entrar <player>\u00a7f para entrar numa party p\u00fablica.");
            sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " sair\u00a7f para sair de uma party.");
            return;
        }
        switch (args[0].toLowerCase()) {
            case "close": 
            case "fechar": {
                Party party = sender.getParty();
                if (party == null) {
                    sender.sendMessage("\u00a7cVoc\u00ea n\u00e3o tem uma party para fechar.");
                    return;
                }
                if (!party.closeParty()) {
                    sender.sendMessage("\u00a7cSua party n\u00e3o est\u00e1 aberta.");
                }
                return;
            }
            case "abrir": 
            case "open": {
                Party party;
                int maxPlayers = 6;
                if (args.length >= 2) {
                    OptionalInt parseInt = StringFormat.parseInt(args[1]);
                    if (!parseInt.isPresent()) {
                        sender.sendMessage(sender.getLanguage().t("invalid-format-integer", "%value%"));
                        return;
                    }
                    maxPlayers = parseInt.getAsInt();
                }
                if (maxPlayers > 10) {
                    sender.sendMessage("\u00a7cVoc\u00ea n\u00e3o pode abrir sua party para mais de 10 jogadores.");
                    return;
                }
                Party party2 = party = sender.getParty() == null ? this.createParty(sender) : sender.getParty();
                if (party.openParty(maxPlayers)) break;
                sender.sendMessage("\u00a7cO valor inserido \u00e9 menor que o n\u00famero de membros da sua party.");
                break;
            }
            case "entrar": {
                if (args.length < 2) {
                    sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " entrar <player>\u00a7f para entrar numa party p\u00fablica.");
                    return;
                }
                if (sender.getParty() != null) {
                    sender.sendMessage("\u00a7cVoc\u00ea n\u00e3o pode entrar nessa party enquanto estiver em outra.");
                    return;
                }
                Member member = CommonPlugin.getInstance().getMemberManager().getMemberByName(args[1]);
                if (member == null) {
                    sender.sendMessage(sender.getLanguage().t("player-is-not-online", "%player%", args[1]));
                    return;
                }
                if (member.getParty() == null) {
                    sender.sendMessage("\u00a7cO jogador " + member.getPlayerName() + " n\u00e3o tem uma party.");
                    return;
                }
                if (member.getParty().getPartyPrivacy() == Party.PartyPrivacy.PRIVATE) {
                    sender.sendMessage("\u00a7cEsta party \u00e9 privada, somente jogadores convidados podem entrar.");
                    return;
                }
                member.getParty().addMember(Profile.from(sender));
                sender.setParty(member.getParty());
                break;
            }
            case "teleportar": 
            case "warp": {
                if (sender.getParty() == null) {
                    sender.sendMessage("\u00a7cVoc\u00ea n\u00e3o tem party.");
                    return;
                }
                if (sender.getParty().hasRole(sender.getUniqueId(), PartyRole.OWNER)) {
                    BungeeMember bungeeMember = (BungeeMember)sender;
                    ServerInfo info = bungeeMember.getProxiedPlayer().getServer().getInfo();
                    sender.getParty().getMembers().stream().map(id -> ProxyServer.getInstance().getPlayer(id)).forEach(player -> {
                        if (player != null) {
                            player.connect(bungeeMember.getProxiedPlayer().getServer().getInfo());
                        }
                    });
                    sender.getParty().sendMessage("\u00a7aTodos os jogadores foram levados para a sala " + info.getName() + ".");
                    break;
                }
                sender.sendMessage("\u00a7cSomente o l\u00edder da party pode fazer isso.");
                break;
            }
            case "expulsar": {
                Party party = sender.getParty();
                if (party == null) {
                    sender.sendMessage("\u00a7cVoc\u00ea n\u00e3o tem uma party.");
                    return;
                }
                if (!party.hasRole(sender.getUniqueId(), PartyRole.ADMIN)) {
                    sender.sendMessage("\u00a7cVoc\u00ea n\u00e3o tem permiss\u00e3o para expulsar algu\u00e9m da sua party.");
                    return;
                }
                if (args.length == 2) {
                    sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " expulsar <player>\u00a7f para expulsar algu\u00e9m da sua party.");
                    return;
                }
                Member member = CommonPlugin.getInstance().getMemberManager().getMemberByName(args[1]);
                if (member == null) {
                    sender.sendMessage(sender.getLanguage().t("player-is-not-online", "%player%", args[1]));
                    return;
                }
                if (sender.getUniqueId().equals(member.getUniqueId())) {
                    sender.sendMessage("\u00a7cVoc\u00ea n\u00e3o pode se expulsar da party.");
                    return;
                }
                if (party.hasRole(sender.getUniqueId(), PartyRole.OWNER)) {
                    sender.sendMessage("\u00a7cVoc\u00ea n\u00e3o pode expulsar esse jogador.");
                    return;
                }
                party.kickMember(sender, member);
                break;
            }
            case "acabar": 
            case "disband": {
                Party party = sender.getParty();
                if (party == null) {
                    sender.sendMessage("\u00a7cVoc\u00ea n\u00e3o est\u00e1 em uma party.");
                    return;
                }
                if (!party.hasRole(sender.getUniqueId(), PartyRole.OWNER)) {
                    sender.sendMessage("\u00a7cVoc\u00ea n\u00e3o pode acabar com essa party.");
                    return;
                }
                party.disband();
                break;
            }
            case "sair": {
                Party party = sender.getParty();
                if (party == null) {
                    sender.sendMessage("\u00a7cVoc\u00ea n\u00e3o est\u00e1 em uma party.");
                    return;
                }
                if (party.hasRole(sender.getUniqueId(), PartyRole.OWNER)) {
                    sender.sendMessage("\u00a7cVoc\u00ea n\u00e3o pode sair da party que voc\u00ea criou.");
                    sender.sendMessage("\u00a7cUse /party disband para sair da party.");
                    return;
                }
                party.removeMember(Profile.from(sender));
                sender.setParty(null);
                break;
            }
            case "accept": 
            case "aceitar": {
                PartyManager.InviteInfo inviteInfo;
                if (sender.getParty() != null) {
                    sender.sendMessage("\u00a7cSaia da sua party para entrar em outra.");
                    return;
                }
                if (!CommonPlugin.getInstance().getPartyManager().getPartyInvitesMap().containsKey(sender.getUniqueId()) || CommonPlugin.getInstance().getPartyManager().getPartyInvitesMap().get(sender.getUniqueId()).isEmpty()) {
                    sender.sendMessage("\u00a7cVoc\u00ea n\u00e3o tem nenhum convite de party para ser aceito.");
                    return;
                }
                if (args.length >= 2) {
                    if (CommonPlugin.getInstance().getPartyManager().getPartyInvitesMap().get(sender.getUniqueId()).size() > 1) {
                        sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " aceitar <player>\u00a7f para aceitar um convite de party.");
                        return;
                    }
                    inviteInfo = CommonPlugin.getInstance().getPartyManager().getPartyInvitesMap().get(sender.getUniqueId()).values().stream().findFirst().orElse(null);
                } else {
                    Member member = CommonPlugin.getInstance().getMemberManager().getMemberByName(args[1]);
                    if (member == null) {
                        sender.sendMessage(sender.getLanguage().t("player-is-not-online", "%player%", args[1]));
                        return;
                    }
                    inviteInfo = CommonPlugin.getInstance().getPartyManager().getPartyInvitesMap().get(sender.getUniqueId()).get(member.getUniqueId());
                    if (inviteInfo == null) {
                        sender.sendMessage("\u00a7cO jogador " + member.getPlayerName() + " n\u00e3o te convidou para party.");
                        return;
                    }
                }
                CommonPlugin.getInstance().getPartyManager().getPartyInvitesMap().remove(sender.getUniqueId());
                if (inviteInfo.getCreatedAt() + 180000L > System.currentTimeMillis()) {
                    Party party = CommonPlugin.getInstance().getPartyManager().getPartyById(inviteInfo.getPartyId());
                    if (party == null) {
                        sender.sendMessage("\u00a7cA party que convidou voc\u00ea n\u00e3o existe mais.");
                        return;
                    }
                    if (party.addMember(Profile.from(sender))) {
                        sender.setParty(party);
                    } else {
                        sender.sendMessage("\u00a7cA party est\u00e1 cheia.");
                    }
                    return;
                }
                sender.sendMessage("\u00a7cO convite para entrar na party expirou!");
                break;
            }
            default: {
                PartyManager.InviteInfo inviteInfo;
                Party party;
                boolean inviteArg;
                boolean bl = inviteArg = args[0].equalsIgnoreCase("convidar") || args[0].equalsIgnoreCase("invite");
                if (inviteArg && args.length <= 1) {
                    sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " convidar <player>\u00a7f para convidar algum jogador para sua party.");
                    return;
                }
                Member member = CommonPlugin.getInstance().getMemberManager().getMemberByName(args[inviteArg ? 1 : 0]);
                if (member == null) {
                    sender.sendMessage(sender.getLanguage().t("player-is-not-online", "%player%", args[inviteArg ? 1 : 0]));
                    return;
                }
                if (sender.getUniqueId().equals(member.getUniqueId())) {
                    sender.sendMessage("\u00a7cVoc\u00ea n\u00e3o pode convidar este jogador.");
                    return;
                }
                if (!member.getMemberConfiguration().isPartyInvites()) {
                    sender.sendMessage("\u00a7cO jogador " + member.getPlayerName() + " n\u00e3o est\u00e1 aceitando convites para party.");
                    return;
                }
                if (member.getParty() != null) {
                    sender.sendMessage("\u00a7cO jogador " + member.getPlayerName() + " j\u00e1 est\u00e1 em uma party.");
                    return;
                }
                Party party3 = party = sender.getParty() == null ? this.createParty(sender) : sender.getParty();
                if (!party.hasRole(sender.getUniqueId(), PartyRole.ADMIN)) {
                    sender.sendMessage("\u00a7cVoc\u00ea n\u00e3o tem permiss\u00e3o para convidar algu\u00e9m para sua party.");
                    return;
                }
                Map partyInvites = CommonPlugin.getInstance().getPartyManager().getPartyInvitesMap().computeIfAbsent(member.getUniqueId(), v -> new HashMap());
                if (partyInvites.containsKey(sender.getUniqueId()) && (inviteInfo = (PartyManager.InviteInfo)partyInvites.get(sender.getUniqueId())).getCreatedAt() + 180000L > System.currentTimeMillis()) {
                    sender.sendMessage("\u00a7cVoc\u00ea precisa esperar para enviar um novo invite para esse jogador.");
                    return;
                }
                CommonPlugin.getInstance().getPartyManager().invite(sender.getUniqueId(), member.getUniqueId(), party);
                sender.sendMessage("\u00a7aO convite para party foi enviado para " + member.getPlayerName() + ".");
                member.sendMessage("\u00a7aVoc\u00ea foi convidado para party de " + sender.getPlayerName() + ".");
                member.sendMessage(new BaseComponent[]{new MessageBuilder("\u00a7aClique ").create(), new MessageBuilder("\u00a7a\u00a7lAQUI").setHoverEvent("\u00a7aClique aqui para aceitar o convite.").setClickEvent("/party aceitar " + sender.getPlayerName()).create(), new MessageBuilder("\u00a7a para aceitar o convite da party.").create()});
            }
        }
    }

    public Party createParty(Member member) {
        UUID partyId = CommonPlugin.getInstance().getPartyData().getPartyId();
        BungeeParty party = new BungeeParty(partyId, member);
        member.setPartyId(partyId);
        member.sendMessage("\u00a7aSua party foi criada.");
        CommonPlugin.getInstance().getPartyData().createParty(party);
        CommonPlugin.getInstance().getPartyManager().loadParty(party);
        return party;
    }
}

