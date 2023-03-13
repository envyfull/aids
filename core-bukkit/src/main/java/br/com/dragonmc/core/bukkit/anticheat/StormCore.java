/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 */
package br.com.dragonmc.core.bukkit.anticheat;

import br.com.dragonmc.core.bukkit.BukkitCommon;
import br.com.dragonmc.core.bukkit.anticheat.listener.PlayerListener;
import br.com.dragonmc.core.bukkit.event.UpdateEvent;
import br.com.dragonmc.core.bukkit.member.BukkitMember;
import com.google.common.collect.ImmutableList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import br.com.dragonmc.core.common.CommonConst;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.bukkit.anticheat.hack.HackData;
import br.com.dragonmc.core.bukkit.anticheat.hack.HackType;
import br.com.dragonmc.core.bukkit.anticheat.hack.verify.AutoclickVerify;
import br.com.dragonmc.core.bukkit.anticheat.hack.verify.AutosoupVerify;
import br.com.dragonmc.core.bukkit.anticheat.hack.verify.KillauraCheck;
import br.com.dragonmc.core.bukkit.anticheat.hack.verify.MacroVerify;
import br.com.dragonmc.core.bukkit.anticheat.hack.verify.ReachVerify;
import br.com.dragonmc.core.common.packet.types.PunishPlayerPacket;
import br.com.dragonmc.core.common.punish.Punish;
import br.com.dragonmc.core.common.punish.PunishType;
import br.com.dragonmc.core.common.utils.string.StringFormat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class StormCore {
    private static StormCore instance;
    private final Plugin plugin;
    private Map<UUID, Long> banPlayerMap = new HashMap<UUID, Long>();

    public StormCore(Plugin plugin) {
        this.plugin = plugin;
        instance = this;
    }

    public void onLoad() {
    }

    public void onEnable() {
        Bukkit.getPluginManager().registerEvents((Listener)new AutoclickVerify(), this.plugin);
        Bukkit.getPluginManager().registerEvents((Listener)new AutosoupVerify(), this.plugin);
        Bukkit.getPluginManager().registerEvents((Listener)new KillauraCheck(), this.plugin);
        Bukkit.getPluginManager().registerEvents((Listener)new MacroVerify(), this.plugin);
        Bukkit.getPluginManager().registerEvents((Listener)new ReachVerify(), this.plugin);
        Bukkit.getPluginManager().registerEvents((Listener)new PlayerListener(), this.plugin);
        Bukkit.getPluginManager().registerEvents(new Listener(){

            @EventHandler
            public void onUpdate(UpdateEvent event) {
                if (event.getType() == UpdateEvent.UpdateType.SECOND) {
                    ImmutableList.copyOf(StormCore.this.banPlayerMap.entrySet()).forEach(entry -> {
                        Player player = Bukkit.getPlayer((UUID)((UUID)entry.getKey()));
                        BukkitMember member = CommonPlugin.getInstance().getMemberManager().getMember(player.getUniqueId(), BukkitMember.class);
                        if (player == null || member == null) {
                            StormCore.this.banPlayerMap.remove(entry.getKey());
                            return;
                        }
                        int seconds = (int)(((Long)entry.getValue() - System.currentTimeMillis()) / 1000L);
                        HackType hackType = (HackType)((Object)((Object)((Map.Entry)member.getUserData().getHackMap().entrySet().stream().sorted((o1, o2) -> Integer.compare(((HackData)o1.getValue()).getTimes(), ((HackData)o2.getValue()).getTimes())).findFirst().orElse(null)).getKey()));
                        if (seconds <= 0) {
                            CommonPlugin.getInstance().getServerData().sendPacket(new PunishPlayerPacket(player.getUniqueId(), new Punish(member, CommonConst.CONSOLE_ID, "STORM", "Uso de " + StringFormat.formatString(hackType.name()), -1L, PunishType.BAN)));
                            StormCore.this.banPlayerMap.remove(entry.getKey());
                            return;
                        }
                        if (seconds <= 30 && seconds % 10 == 0 || seconds % 15 == 0) {
                            CommonPlugin.getInstance().getMemberManager().getMembers().stream().filter(m -> m.isStaff() && m.getMemberConfiguration().isSeeingLogs()).forEach(m -> m.sendMessage("\u00a7cO jogador " + player.getName() + " ser\u00e1 banido por uso de " + StringFormat.formatString(hackType.name()) + " em " + seconds + " segundos!"));
                        }
                    });
                }
            }
        }, this.plugin);
    }

    public void onDisable() {
    }

    public void ignore(Player player, double seconds) {
        player.setMetadata("anticheat-ignore", BukkitCommon.getInstance().createMeta(System.currentTimeMillis() + (long)(seconds * 1000.0)));
    }

    public void autoban(UUID playerId) {
        this.banPlayerMap.put(playerId, System.currentTimeMillis() + 61000L);
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    public Map<UUID, Long> getBanPlayerMap() {
        return this.banPlayerMap;
    }

    public static StormCore getInstance() {
        return instance;
    }
}

