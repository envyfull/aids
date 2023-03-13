/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.Cache
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.collect.ImmutableList
 *  net.md_5.bungee.api.CommandSender
 *  net.md_5.bungee.api.ProxyServer
 *  net.md_5.bungee.api.connection.ProxiedPlayer
 *  net.md_5.bungee.api.event.LoginEvent
 *  net.md_5.bungee.api.event.PermissionCheckEvent
 *  net.md_5.bungee.api.event.PlayerDisconnectEvent
 *  net.md_5.bungee.api.event.PostLoginEvent
 *  net.md_5.bungee.api.plugin.Event
 *  net.md_5.bungee.api.plugin.Listener
 *  net.md_5.bungee.api.plugin.Plugin
 *  net.md_5.bungee.connection.InitialHandler
 *  net.md_5.bungee.connection.LoginResult
 *  net.md_5.bungee.connection.LoginResult$Property
 *  net.md_5.bungee.event.EventHandler
 */
package br.com.dragonmc.core.bungee.listener;

import br.com.dragonmc.core.bungee.BungeeConst;
import br.com.dragonmc.core.bungee.BungeeMain;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.ImmutableList;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import br.com.dragonmc.core.common.CommonConst;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.common.PluginInfo;
import br.com.dragonmc.core.bungee.event.player.PlayerFieldUpdateEvent;
import br.com.dragonmc.core.bungee.event.player.PlayerPardonedEvent;
import br.com.dragonmc.core.bungee.event.player.PlayerPunishEvent;
import br.com.dragonmc.core.bungee.member.BungeeMember;
import br.com.dragonmc.core.common.member.Member;
import br.com.dragonmc.core.common.member.configuration.LoginConfiguration;
import br.com.dragonmc.core.common.member.party.Party;
import br.com.dragonmc.core.common.permission.Group;
import br.com.dragonmc.core.common.punish.Punish;
import br.com.dragonmc.core.common.punish.PunishType;
import br.com.dragonmc.core.common.report.Report;
import br.com.dragonmc.core.common.utils.DateUtils;
import br.com.dragonmc.core.common.utils.skin.Skin;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.connection.LoginResult;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.protocol.Property;

public class MemberListener
implements Listener {
    private Cache<String, Punish> banCache = CacheBuilder.newBuilder().expireAfterWrite(30L, TimeUnit.MINUTES).expireAfterAccess(30L, TimeUnit.MINUTES).build((CacheLoader)new CacheLoader<String, Punish>(){

        public Punish load(String name) throws Exception {
            return null;
        }
    });

    @EventHandler
    public void onLogin(LoginEvent event) {
        event.registerIntent((Plugin) BungeeMain.getInstance());
        CommonPlugin.getInstance().getPluginPlatform().runAsync(() -> {
            this.handleMemberLoad(event);
            event.completeIntent((Plugin)BungeeMain.getInstance());
        });
    }

    private void handleMemberLoad(LoginEvent event) {
        String playerName = event.getConnection().getName();
        UUID uniqueId = event.getConnection().getUniqueId();
        BungeeMember member = CommonPlugin.getInstance().getMemberData().loadMember(uniqueId, BungeeMember.class);
        if (member == null) {
            LoginConfiguration.AccountType accountType = event.getConnection().isOnlineMode() ? LoginConfiguration.AccountType.PREMIUM : LoginConfiguration.AccountType.CRACKED;
            BungeeMember memberByName = CommonPlugin.getInstance().getMemberData().loadMember(playerName, true, BungeeMember.class);
            if (accountType == LoginConfiguration.AccountType.PREMIUM && memberByName != null && memberByName.getLoginConfiguration().getAccountType() == LoginConfiguration.AccountType.CRACKED) {
                try {
                    InitialHandler initialHandler = (InitialHandler)event.getConnection();
                    Field field = InitialHandler.class.getDeclaredField("uniqueId");
                    field.setAccessible(true);
                    field.set(initialHandler, memberByName.getUniqueId());
                }
                catch (Exception ex) {
                    CommonPlugin.getInstance().getLogger().log(Level.SEVERE, "Failed to set unique id", ex);
                    event.setCancelled(true);
                    event.setCancelReason("\u00a7cSua conta n\u00e3o foi carregada.\nDetectamos que voc\u00ea est\u00e1 usando uma conta premium, mas j\u00e1 est\u00e1 registrado no servidor como cracked.\nFizemos as altera\u00e7\u00f5es necess\u00e1rias mas est\u00e1 ocorrendo um erro.\nEntre em contato com um administrador.");
                    return;
                }
                event.getConnection().setOnlineMode(false);
            } else {
                if (memberByName != null) {
                    if (!memberByName.getPlayerName().equals(playerName)) {
                        event.setCancelled(true);
                        event.setCancelReason("\u00a7cSua conta j\u00e1 est\u00e1 registrada no servidor com outro nick.");
                        return;
                    }
                    if (accountType != memberByName.getLoginConfiguration().getAccountType()) {
                        event.setCancelled(true);
                        event.setCancelReason("\u00a7cSua conta j\u00e1 est\u00e1 registrada no servidor como " + memberByName.getLoginConfiguration().getAccountType().name() + ".");
                    }
                }
                member = new BungeeMember(uniqueId, playerName, accountType);
                CommonPlugin.getInstance().getMemberData().createMember(member);
                CommonPlugin.getInstance().debug("The member " + member.getPlayerName() + "(" + member.getUniqueId() + ") has been created.");
            }
        } else if (member.getLoginConfiguration().getAccountType() == LoginConfiguration.AccountType.NONE) {
            member.getLoginConfiguration().setAccountType(event.getConnection().isOnlineMode() ? LoginConfiguration.AccountType.PREMIUM : LoginConfiguration.AccountType.CRACKED);
            member.saveConfig();
        }
        if (member.getSkin() == null || !member.isCustomSkin() && member.getSkin().getCreatedAt() + 604800000L > System.currentTimeMillis()) {
            try {
                InitialHandler initialHandler = (InitialHandler)event.getConnection();
                LoginResult loginProfile = initialHandler.getLoginProfile();
                Skin skin = null;
                if (loginProfile != null && loginProfile.getProperties() != null) {
                    for (Property property : loginProfile.getProperties()) {
                        if (!property.getName().equals("textures")) continue;
                        skin = new Skin(member.getName(), property.getValue(), property.getSignature());
                        break;
                    }
                }
                if (skin == null) {
                    member.setSkin(CommonConst.DEFAULT_SKIN);
                } else {
                    member.setSkin(skin);
                }
            }
            catch (Exception ex) {
                CommonPlugin.getInstance().getLogger().log(Level.WARNING, "Failed to load skin for " + member.getName(), ex);
            }
        }
        if (this.handleLogin(member, event)) {
            return;
        }
        if (this.handlePunish(member, event)) {
            return;
        }
        if (this.handleTimeout(member, event)) {
            return;
        }
        if (this.handleWhiteList(member, event)) {
            return;
        }
        if (!event.isCancelled()) {
            Report report;
            CommonPlugin.getInstance().getMemberManager().loadMember(member);
            this.handleParty(member, event);
            if (!CommonPlugin.getInstance().getMemberData().checkCache(member.getUniqueId())) {
                CommonPlugin.getInstance().getMemberData().saveRedisMember(member);
            }
            if ((report = CommonPlugin.getInstance().getReportManager().getReportById(member.getUniqueId())) != null) {
                report.setOnline(true);
            }
            BungeeMain.getInstance().loadTexture(event.getConnection(), member.getSkin());
        }
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        BungeeMember member = CommonPlugin.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId(), BungeeMember.class);
        if (member == null) {
            event.getPlayer().disconnect(CommonPlugin.getInstance().getPluginInfo().translate("account-not-loaded"));
            return;
        }
        member.setProxiedPlayer(event.getPlayer());
        this.handlePermissions(member);
    }

    @EventHandler
    public void onPlayerPunish(PlayerPunishEvent event) {
        if (event.getPunish().getPunishType() == PunishType.BAN && !event.getPunish().getPlayerName().equals("CONSOLE") && event.getPunish().isPermanent()) {
            Member member = event.getPunished();
            this.banIp(member.getLastIpAddress(), event.getPunish());
        }
    }

    @EventHandler
    public void onPlayerPardoned(PlayerPardonedEvent event) {
        if (event.getPunish().getPunishType() == PunishType.BAN && event.getPunish().isPermanent() && this.isIpBanned(event.getPunished().getLastIpAddress())) {
            this.banCache.invalidate((Object)event.getPunished().getLastIpAddress());
        }
    }

    public void handlePermissions(BungeeMember member) {
        ProxiedPlayer player = member.getProxiedPlayer();
        for (Object permission : ImmutableList.copyOf((Collection)player.getPermissions())) {
            player.setPermission((String) permission, false);
        }
        for (String string : member.getPermissions()) {
            player.setPermission(string.toLowerCase(), true);
        }
        for (Group group : (Group[])member.getGroups().keySet().stream().map(name -> CommonPlugin.getInstance().getPluginInfo().getGroupByName((String)name)).toArray(Group[]::new)) {
            for (String string : group.getPermissions()) {
                player.setPermission(string.toLowerCase(), true);
            }
        }
    }

    @EventHandler
    public void onPlayerFieldUpdate(PlayerFieldUpdateEvent event) {
        if (event.getFieldName().toLowerCase().contains("group")) {
            this.handlePermissions(event.getPlayer());
        }
    }

    @EventHandler
    public void onPermissionCheck(PermissionCheckEvent event) {
        if (!(event.getSender() instanceof ProxiedPlayer) || event.hasPermission()) {
            return;
        }
        CommandSender sender = event.getSender();
        Member member = CommonPlugin.getInstance().getMemberManager().getMember(((ProxiedPlayer)sender).getUniqueId());
        if (member == null) {
            return;
        }
        String permission = sender.getPermissions().stream().filter(string -> string.equals("*")).findFirst().orElse(null);
        if (permission == null) {
            event.setHasPermission(member.hasSilentPermission(event.getPermission()));
        } else {
            event.setHasPermission(true);
        }
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        BungeeMember member = CommonPlugin.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId(), BungeeMember.class);
        if (member != null) {
            Report report;
            member.getLoginConfiguration().logOut();
            member.logOut();
            CommonPlugin.getInstance().getMemberManager().unloadMember(member);
            CommonPlugin.getInstance().getMemberData().cacheConnection(event.getPlayer().getPendingConnection().getName(), member.getLoginConfiguration().getAccountType() == LoginConfiguration.AccountType.PREMIUM);
            CommonPlugin.getInstance().getMemberData().cacheMember(member.getUniqueId());
            Party party = member.getParty();
            if (party != null && !party.getMembers().stream().filter(id -> CommonPlugin.getInstance().getMemberManager().getMember((UUID)id) != null).findFirst().isPresent()) {
                member.setPartyId(null);
                CommonPlugin.getInstance().getPartyData().deleteParty(party);
                CommonPlugin.getInstance().getPartyManager().unloadParty(party.getPartyId());
            }
            if ((report = CommonPlugin.getInstance().getReportManager().getReportById(member.getUniqueId())) != null) {
                report.setOnline(false);
            }
            CommonPlugin.getInstance().getPartyManager().getPartyInvitesMap().remove(member.getUniqueId());
        }
    }

    private void banIp(String ipAddress, Punish punish) {
        this.banCache.put(ipAddress, punish);
    }

    private boolean isIpBanned(String ipAddress) {
        return this.banCache.asMap().containsKey(ipAddress);
    }

    private boolean handleParty(BungeeMember member, LoginEvent event) {
        Party party = member.getParty();
        if (party != null) {
            return true;
        }
        if (member.getPartyId() != null) {
            member.setPartyId(null);
        }
        return true;
    }

    private boolean handleTimeout(BungeeMember member, LoginEvent event) {
        if (member.getLoginConfiguration().isTimeouted()) {
            event.setCancelled(true);
            event.setCancelReason(PluginInfo.t(member, "command.login.timeouted", "%time%", DateUtils.getTime(member.getLanguage(), member.getLoginConfiguration().getTimeoutTime()), "%website%", CommonPlugin.getInstance().getPluginInfo().getWebsite()));
            return true;
        }
        return false;
    }

    private boolean handlePunish(BungeeMember member, LoginEvent event) {
        Punish punish = member.getPunishConfiguration().getActualPunish(PunishType.BAN);
        if (punish != null) {
            event.setCancelled(true);
            event.setCancelReason(PluginInfo.t(member, "ban-" + (punish.isPermanent() ? "permanent" : "temporary") + "-kick-message", "%reason%", punish.getPunishReason(), "%expireAt%", DateUtils.getTime(member.getLanguage(), punish.getExpireAt()), "%punisher%", punish.getPunisherName(), "%website%", CommonPlugin.getInstance().getPluginInfo().getWebsite(), "%store%", CommonPlugin.getInstance().getPluginInfo().getStore(), "%discord%", CommonPlugin.getInstance().getPluginInfo().getDiscord()));
            return true;
        }
        if (this.isIpBanned(member.getLastIpAddress())) {
            punish = new Punish(member, BungeeConst.CONSOLE_SENDER, "Conta alternativa", -1L, PunishType.BAN);
            member.getPunishConfiguration().punish(punish);
            member.saveConfig();
            ProxyServer.getInstance().getPluginManager().callEvent((Event)new PlayerPunishEvent(member, punish, BungeeConst.CONSOLE_SENDER));
            event.setCancelled(true);
            event.setCancelReason(PluginInfo.t(member, "ban-" + (punish.isPermanent() ? "permanent" : "temporary") + "-kick-message", "%reason%", punish.getPunishReason(), "%expireAt%", DateUtils.getTime(member.getLanguage(), punish.getExpireAt()), "%punisher%", punish.getPunisherName(), "%website%", CommonPlugin.getInstance().getPluginInfo().getWebsite(), "%store%", CommonPlugin.getInstance().getPluginInfo().getStore(), "%discord%", CommonPlugin.getInstance().getPluginInfo().getDiscord()));
            return true;
        }
        return false;
    }

    private boolean handleLogin(BungeeMember member, LoginEvent event) {
        SocketAddress socket = event.getConnection().getSocketAddress();
        if (!(socket instanceof InetSocketAddress)) {
            event.setCancelled(true);
            event.setCancelReason("\u00a7cWe cannot load your ip address.");
            return true;
        }
        String playerName = event.getConnection().getName();
        InetSocketAddress inetSocketAddress = (InetSocketAddress)socket;
        String ipAddress = inetSocketAddress.getHostString();
        try {
            member.logIn(playerName, ipAddress);
            return false;
        }
        catch (NullPointerException ex) {
            event.setCancelled(true);
            event.setCancelReason("\u00a7cWe cannot load your ip address.");
            ex.printStackTrace();
            return true;
        }
    }

    private boolean handleWhiteList(BungeeMember bungeeMember, LoginEvent loginEvent) {
        if (BungeeMain.getInstance().isMaintenance() && !bungeeMember.hasPermission("command.admin") && !BungeeMain.getInstance().isMemberInWhiteList(bungeeMember.getPlayerName())) {
            long maintenanceTime = BungeeMain.getInstance().getWhitelistExpires();
            loginEvent.setCancelled(true);
            loginEvent.setCancelReason("\u00a7c\u00a7lDRAGONMC\n\u00a74\n\u00a7cO servidor entrou em manuten\u00e7\u00e3o!\n\u00a7cPara melhorar sua jogabilidade estamos atualizando o servidor" + (maintenanceTime == 0L ? ", espere para entrar novamente!" : ".\n\u00a7cO servidor volta em " + DateUtils.getTime(CommonPlugin.getInstance().getPluginInfo().getDefaultLanguage(), maintenanceTime)) + "\n\u00a7f\n\u00a7ePara mais informa\u00e7\u00f5es \u00a7b" + CommonPlugin.getInstance().getPluginInfo().getDiscord());
            return true;
        }
        return false;
    }
}

