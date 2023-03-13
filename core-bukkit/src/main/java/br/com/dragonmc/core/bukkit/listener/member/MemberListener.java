/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.AsyncPlayerPreLoginEvent
 *  org.bukkit.event.player.AsyncPlayerPreLoginEvent$Result
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerLoginEvent
 *  org.bukkit.event.player.PlayerLoginEvent$Result
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 */
package br.com.dragonmc.core.bukkit.listener.member;

import java.util.Map;
import java.util.UUID;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.bukkit.BukkitCommon;
import br.com.dragonmc.core.bukkit.event.member.PlayerGroupChangeEvent;
import br.com.dragonmc.core.bukkit.event.member.PlayerUpdatedFieldEvent;
import br.com.dragonmc.core.bukkit.event.player.PlayerAdminEvent;
import br.com.dragonmc.core.bukkit.member.BukkitMember;
import br.com.dragonmc.core.bukkit.member.party.BukkitParty;
import br.com.dragonmc.core.bukkit.utils.player.PlayerAPI;
import br.com.dragonmc.core.common.member.Member;
import br.com.dragonmc.core.common.member.party.Party;
import br.com.dragonmc.core.common.member.status.StatusType;
import br.com.dragonmc.core.common.permission.Group;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class MemberListener
implements Listener {
    @EventHandler(priority=EventPriority.LOW)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            return;
        }
        UUID uniqueId = event.getUniqueId();
        BukkitMember member = CommonPlugin.getInstance().getMemberData().loadMember(uniqueId, BukkitMember.class);
        if (member == null) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, CommonPlugin.getInstance().getPluginInfo().translate("account-not-exists"));
            return;
        }
        Party party = member.getParty();
        if (party == null && member.getPartyId() != null) {
            party = CommonPlugin.getInstance().getPartyData().loadParty(member.getPartyId(), BukkitParty.class);
            if (party == null) {
                CommonPlugin.getInstance().debug("The party " + member.getPartyId() + " didnt load.");
            } else {
                CommonPlugin.getInstance().getPartyManager().loadParty(party);
            }
        }
        member.connect();
        CommonPlugin.getInstance().getMemberManager().loadMember(member);
        for (StatusType types : BukkitCommon.getInstance().getPreloadedStatus()) {
            CommonPlugin.getInstance().getStatusManager().loadStatus(uniqueId, types);
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onPlayerLoginLW(PlayerLoginEvent event) {
        if (CommonPlugin.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId()) == null) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, CommonPlugin.getInstance().getPluginInfo().translate("account-not-loaded"));
            return;
        }
        BukkitMember member = CommonPlugin.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId(), BukkitMember.class);
        Player player = event.getPlayer();
        member.setPlayer(player);
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onPlayerLogin(final PlayerLoginEvent event) {
        if (CommonPlugin.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId()) == null) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, CommonPlugin.getInstance().getPluginInfo().translate("account-not-loaded"));
            return;
        }
        final BukkitMember member = CommonPlugin.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId(), BukkitMember.class);
        int admins = BukkitCommon.getInstance().getVanishManager().getPlayersInAdmin().size();
        if (event.getResult() == PlayerLoginEvent.Result.KICK_FULL && Bukkit.getOnlinePlayers().size() - admins >= Bukkit.getMaxPlayers() && !member.hasPermission("server.full")) {
            event.setKickMessage("\u00a7cO servidor est\u00e1 cheio.");
            return;
        }
        if (!(event.getResult() != PlayerLoginEvent.Result.KICK_WHITELIST && CommonPlugin.getInstance().isJoinEnabled() || member.hasPermission("command.admin"))) {
            event.setKickMessage("\u00a7cSomente membros da equipe podem entrar no momento.");
            return;
        }
        event.allow();
        if (member.isUsingFake()) {
            PlayerAPI.changePlayerName(event.getPlayer(), member.getFakeName(), false);
        }
        if (member.hasCustomSkin()) {
            new BukkitRunnable(){

                public void run() {
                    PlayerAPI.changePlayerSkin(event.getPlayer(), member.getSkin().getValue(), member.getSkin().getSignature(), false);
                }
            }.runTask((Plugin)BukkitCommon.getInstance());
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerLoginM(PlayerLoginEvent event) {
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            CommonPlugin.getInstance().getMemberManager().unloadMember(event.getPlayer().getUniqueId());
            CommonPlugin.getInstance().getStatusManager().unloadStatus(event.getPlayer().getUniqueId());
            return;
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (CommonPlugin.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId()) == null) {
            event.getPlayer().kickPlayer(CommonPlugin.getInstance().getPluginInfo().translate("account-not-loaded"));
            return;
        }
        CommonPlugin.getInstance().getPluginPlatform().runAsync(() -> CommonPlugin.getInstance().getServerData().joinPlayer(event.getPlayer().getUniqueId(), Bukkit.getMaxPlayers()));
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerAdmin(PlayerAdminEvent event) {
        if (event.getAdminMode() == PlayerAdminEvent.AdminMode.ADMIN) {
            CommonPlugin.getInstance().getPluginPlatform().runAsync(() -> CommonPlugin.getInstance().getServerData().leavePlayer(event.getPlayer().getUniqueId(), Bukkit.getMaxPlayers()));
        } else {
            CommonPlugin.getInstance().getPluginPlatform().runAsync(() -> CommonPlugin.getInstance().getServerData().joinPlayer(event.getPlayer().getUniqueId(), Bukkit.getMaxPlayers()));
        }
    }

    @EventHandler
    public void onPlayerUpdatedField(PlayerUpdatedFieldEvent event) {
        Player player = event.getPlayer();
        BukkitMember member = event.getBukkitMember();
        switch (event.getField().toLowerCase()) {
            case "groups": {
                Map oldObject = (Map)event.getOldObject();
                Map newObject = (Map)event.getObject();
                if (newObject.isEmpty()) {
                    Group highGroup = CommonPlugin.getInstance().getPluginInfo().getGroupMap().values().stream().filter(group -> oldObject.containsKey(group.getGroupName().toLowerCase())).sorted((o1, o2) -> Integer.compare(o1.getId(), o2.getId())).findFirst().orElse(null);
                    if (highGroup == null) {
                        Bukkit.getPluginManager().callEvent((Event)new PlayerGroupChangeEvent(player, (Member)member, (String)null, 0L, PlayerGroupChangeEvent.Action.UNKNOWN));
                    } else {
                        Bukkit.getPluginManager().callEvent((Event)new PlayerGroupChangeEvent(player, (Member)member, highGroup, 0L, PlayerGroupChangeEvent.Action.REMOVE));
                    }
                } else if (oldObject.isEmpty()) {
                    Group highGroup = CommonPlugin.getInstance().getPluginInfo().getGroupMap().values().stream().filter(group -> newObject.containsKey(group.getGroupName().toLowerCase())).sorted((o1, o2) -> Integer.compare(o1.getId(), o2.getId())).findFirst().orElse(null);
                    if (highGroup == null) {
                        Bukkit.getPluginManager().callEvent((Event)new PlayerGroupChangeEvent(player, (Member)member, (String)null, 0L, PlayerGroupChangeEvent.Action.SET));
                    } else {
                        Bukkit.getPluginManager().callEvent((Event)new PlayerGroupChangeEvent(player, (Member)member, highGroup, 0L, PlayerGroupChangeEvent.Action.SET));
                    }
                } else if (newObject.size() == 1) {
                    String groupName = (String) newObject.keySet().stream().findFirst().orElse(null);
                    if (groupName == null) {
                        Bukkit.getPluginManager().callEvent((Event)new PlayerGroupChangeEvent(player, (Member)member, (String)null, 0L, PlayerGroupChangeEvent.Action.SET));
                    } else {
                        Bukkit.getPluginManager().callEvent((Event)new PlayerGroupChangeEvent(player, (Member)member, groupName, 0L, PlayerGroupChangeEvent.Action.SET));
                    }
                } else if (oldObject.size() < newObject.size()) {
                    String groupName = (String) oldObject.keySet().stream().filter(group -> newObject.containsKey(group)).findFirst().orElse(null);
                    if (groupName == null) {
                        Bukkit.getPluginManager().callEvent((Event)new PlayerGroupChangeEvent(player, (Member)member, (String)null, 0L, PlayerGroupChangeEvent.Action.ADD));
                    } else {
                        Bukkit.getPluginManager().callEvent((Event)new PlayerGroupChangeEvent(player, (Member)member, groupName, 0L, PlayerGroupChangeEvent.Action.ADD));
                    }
                } else if (oldObject.size() > newObject.size()) {
                    String groupName = (String) newObject.keySet().stream().filter(group -> !oldObject.containsKey(group)).findFirst().orElse(null);
                    if (groupName == null) {
                        Bukkit.getPluginManager().callEvent((Event)new PlayerGroupChangeEvent(player, (Member)member, (String)null, 0L, PlayerGroupChangeEvent.Action.REMOVE));
                    } else {
                        Bukkit.getPluginManager().callEvent((Event)new PlayerGroupChangeEvent(player, (Member)member, groupName, 0L, PlayerGroupChangeEvent.Action.REMOVE));
                    }
                } else {
                    Bukkit.getPluginManager().callEvent((Event)new PlayerGroupChangeEvent(player, (Member)member, (Group) newObject.keySet().stream().findFirst().orElse(null), 0L, PlayerGroupChangeEvent.Action.UNKNOWN));
                }
                member.updateGroup();
                break;
            }
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Member member = CommonPlugin.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId());
        if (member != null) {
            CommonPlugin.getInstance().getPluginPlatform().runAsync(() -> {
                CommonPlugin.getInstance().getMemberManager().unloadMember(member);
                CommonPlugin.getInstance().getServerData().leavePlayer(event.getPlayer().getUniqueId(), Bukkit.getMaxPlayers());
                CommonPlugin.getInstance().getStatusManager().unloadStatus(member.getUniqueId());
                Party party = member.getParty();
                if (party != null && !party.getMembers().stream().filter(id -> CommonPlugin.getInstance().getMemberManager().getMember((UUID)id) != null).findFirst().isPresent()) {
                    CommonPlugin.getInstance().getPartyData().deleteParty(party);
                    CommonPlugin.getInstance().getPartyManager().unloadParty(party.getPartyId());
                }
            });
        }
    }
}

