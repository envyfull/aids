/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerKickEvent
 *  org.bukkit.event.player.PlayerLoginEvent
 *  org.bukkit.event.player.PlayerLoginEvent$Result
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.permissions.Permission
 *  org.bukkit.permissions.PermissionAttachment
 *  org.bukkit.permissions.PermissionDefault
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 */
package br.com.dragonmc.core.bukkit.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.bukkit.BukkitCommon;
import br.com.dragonmc.core.bukkit.event.member.PlayerGroupChangeEvent;
import br.com.dragonmc.core.bukkit.utils.permission.PermissionManager;
import br.com.dragonmc.core.common.member.Member;
import br.com.dragonmc.core.common.permission.Group;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class PermissionListener
implements Listener {
    private final Map<UUID, PermissionAttachment> attachments = new HashMap<UUID, PermissionAttachment>();
    private PermissionManager manager = BukkitCommon.getInstance().getPermissionManager();

    public PermissionListener() {
        new BukkitRunnable(){

            public void run() {
                for (Player player : PermissionListener.this.manager.getServer().getOnlinePlayers()) {
                    PermissionListener.this.updateAttachment(player);
                }
            }
        }.runTaskLater((Plugin)this.manager.getPlugin(), 10L);
    }

    @EventHandler(priority=EventPriority.LOW)
    public void onPlayerLogin(PlayerLoginEvent event) {
        this.updateAttachment(event.getPlayer());
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerLoginMonitor(PlayerLoginEvent event) {
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            this.removeAttachment(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerGroupChange(PlayerGroupChangeEvent event) {
        Player player = event.getPlayer();
        Group group = event.getGroup();
        if (group == null) {
            CommonPlugin.getInstance().debug("The server couldnt load group " + event.getGroupName() + " to change the permissions of " + player.getName());
            return;
        }
        this.removeAttachment(player);
        this.updateAttachment(player);
        player.recalculatePermissions();
    }

    public void updateAttachment(Player player) {
        Member member = CommonPlugin.getInstance().getMemberManager().getMember(player.getUniqueId());
        if (member == null) {
            return;
        }
        PermissionAttachment attach = this.attachments.get(player.getUniqueId());
        Permission playerPerm = this.getCreateWrapper(player, player.getUniqueId().toString());
        if (attach == null) {
            attach = player.addAttachment((Plugin)this.manager.getPlugin());
            this.attachments.put(player.getUniqueId(), attach);
            attach.setPermission(playerPerm, true);
        } else {
            attach.getPermissions().clear();
            attach.setPermission(playerPerm, true);
        }
        playerPerm.getChildren().clear();
        for (Group group2 : member.getGroups().keySet().stream().map(groupName -> CommonPlugin.getInstance().getPluginInfo().getGroupByName((String)groupName)).filter(group -> group != null).collect(Collectors.toList())) {
            for (String perm : group2.getPermissions()) {
                if (playerPerm.getChildren().containsKey(perm)) continue;
                playerPerm.getChildren().put(perm, true);
            }
        }
        for (String perm : member.getPermissions()) {
            if (playerPerm.getChildren().containsKey(perm)) continue;
            playerPerm.getChildren().put(perm, true);
        }
        player.recalculatePermissions();
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        this.removeAttachment(event.getPlayer());
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onKick(PlayerKickEvent event) {
        this.removeAttachment(event.getPlayer());
    }

    protected void removeAttachment(Player player) {
        PermissionAttachment attach = this.attachments.remove(player.getUniqueId());
        if (attach != null) {
            attach.remove();
        }
        this.manager.getServer().getPluginManager().removePermission(player.getUniqueId().toString());
    }

    public void onDisable() {
        for (PermissionAttachment attach : this.attachments.values()) {
            attach.remove();
        }
        this.attachments.clear();
    }

    private Permission getCreateWrapper(Player player, String name) {
        Permission perm = this.manager.getServer().getPluginManager().getPermission(name);
        if (perm == null) {
            perm = new Permission(name, "Interal Permission", PermissionDefault.FALSE);
            this.manager.getServer().getPluginManager().addPermission(perm);
        }
        return perm;
    }
}

