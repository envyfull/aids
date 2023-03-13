/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 */
package br.com.dragonmc.lobby.login.listener;

import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.bukkit.BukkitCommon;
import br.com.dragonmc.core.bukkit.event.UpdateEvent;
import br.com.dragonmc.core.bukkit.event.member.PlayerAuthEvent;
import br.com.dragonmc.lobby.login.captcha.Captcha;
import br.com.dragonmc.lobby.login.captcha.impl.ItemCaptcha;
import br.com.dragonmc.lobby.login.captcha.impl.MoveCaptcha;
import br.com.dragonmc.lobby.login.event.CaptchaSuccessEvent;
import br.com.dragonmc.core.common.member.Member;
import br.com.dragonmc.core.common.member.configuration.LoginConfiguration;
import br.com.dragonmc.core.common.server.ServerType;
import br.com.dragonmc.core.common.utils.Callback;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerListener
implements Listener {
    private List<Captcha> captchaList = new ArrayList<Captcha>();
    private Map<UUID, Long> timeMap = new HashMap<UUID, Long>();

    public PlayerListener() {
        this.captchaList.add(new MoveCaptcha());
        this.captchaList.add(new ItemCaptcha());
    }

    @EventHandler
    public void update(UpdateEvent event) {
        if (event.getType() == UpdateEvent.UpdateType.SECOND) {
            ImmutableSet<Map.Entry> playerList = ImmutableSet.copyOf(this.timeMap.entrySet());
            for (Map.Entry next : playerList) {
                Player player = Bukkit.getPlayer((UUID)((UUID)next.getKey()));
                Member member = CommonPlugin.getInstance().getMemberManager().getMember(player.getUniqueId());
                boolean needCaptcha = this.needCaptcha(member);
                if ((Long)next.getValue() > System.currentTimeMillis()) {
                    if (needCaptcha) continue;
                    int time = (int)((Long)next.getValue() - System.currentTimeMillis()) / 1000 + 1;
                    if (time % 10 == 0) {
                        member.sendMessage(member.getLoginConfiguration().isRegistered() ? "\u00a7aVoc\u00ea precisa se autenticar usando o /login <sua senha>\u00a7f." : "\u00a7aVoc\u00ea precisa se autenticar usando o /register <sua senha> <repita sua senha>\u00a7f.");
                    }
                    member.sendActionBar("\u00a7c" + time + " segundos restantes.");
                    continue;
                }
                if (needCaptcha) {
                    this.deletePlayer(player);
                }
                player.kickPlayer(needCaptcha ? "\u00a7cVoc\u00ea precisa fazer o captcha para se autenticar." : "\u00a7cVoc\u00ea demorou demais para se logar.");
            }
        }
    }

    @EventHandler
    public void onPlayerAuth(final PlayerAuthEvent event) {
        this.timeMap.remove(event.getPlayer().getUniqueId());
        new BukkitRunnable(){

            public void run() {
                BukkitCommon.getInstance().sendPlayerToServer(event.getPlayer(), true, ServerType.LOBBY);
            }
        }.runTaskTimer((Plugin)BukkitCommon.getInstance(), 20L, 60L);
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Member member = CommonPlugin.getInstance().getMemberManager().getMember(player.getUniqueId());
        if (member.getLoginConfiguration().getAccountType() == LoginConfiguration.AccountType.PREMIUM) {
            if (!member.hasPermission("staff.super")) {
                BukkitCommon.getInstance().sendPlayerToServer(event.getPlayer(), true, ServerType.LOBBY);
            }
            return;
        }
        boolean captcha = this.needCaptcha(member);
        this.loadTime(player);
        if (captcha) {
            player.sendMessage("\u00a7aComplete o captcha para ter o acesso liberado.");
            this.captcha(player, this.captchaList.stream().findFirst().orElse(null), 0);
        }
        new BukkitRunnable(){

            public void run() {
            }
        };
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.timeMap.remove(event.getPlayer().getUniqueId());
    }

    private void captcha(final Player player, Captcha orElse, final int i) {
        if (this.captchaList.size() == i) {
            Member member = CommonPlugin.getInstance().getMemberManager().getMember(player.getUniqueId());
            CommonPlugin.getInstance().debug("The ip " + member.getIpAddress() + " pass in the captcha.");
            member.getLoginConfiguration().setCaptcha(true);
            this.loadTime(player);
            Bukkit.getPluginManager().callEvent((Event)new CaptchaSuccessEvent(player));
            return;
        }
        orElse.verify(player, new Callback<Boolean>(){

            @Override
            public void callback(Boolean t) {
                if (t.booleanValue()) {
                    PlayerListener.this.captcha(player, i + 1 >= PlayerListener.this.captchaList.size() ? null : (Captcha)PlayerListener.this.captchaList.get(i + 1), i + 1);
                } else {
                    PlayerListener.this.deletePlayer(player);
                    player.kickPlayer("\u00a7cVoc\u00ea falhou no captcha.");
                }
            }
        });
    }

    public boolean needCaptcha(Member member) {
        return !member.getLoginConfiguration().isCaptcha() && member.getLoginConfiguration().getAccountType() != LoginConfiguration.AccountType.PREMIUM;
    }

    public void loadTime(Player player) {
        this.timeMap.put(player.getUniqueId(), System.currentTimeMillis() + 30000L - 1L);
    }

    public void deletePlayer(Player player) {
        Member member = CommonPlugin.getInstance().getMemberManager().getMember(player.getUniqueId());
        CommonPlugin.getInstance().debug("The ip " + member.getIpAddress() + " was kicked from captcha failure.");
        if (member.getOnlineTime() <= 600000L) {
            CommonPlugin.getInstance().getPluginPlatform().runAsync(() -> CommonPlugin.getInstance().getMemberData().deleteMember(player.getUniqueId()));
        }
    }
}

