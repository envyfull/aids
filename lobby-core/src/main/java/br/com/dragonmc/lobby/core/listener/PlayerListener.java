/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.GameMode
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerLoginEvent
 *  org.bukkit.event.player.PlayerLoginEvent$Result
 *  org.bukkit.event.player.PlayerMoveEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 */
package br.com.dragonmc.lobby.core.listener;

import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.bukkit.BukkitCommon;
import br.com.dragonmc.lobby.core.CoreMain;
import br.com.dragonmc.lobby.core.LobbyConst;
import br.com.dragonmc.lobby.core.gamer.Gamer;
import br.com.dragonmc.core.bukkit.utils.player.PlayerHelper;
import br.com.dragonmc.core.common.member.Member;
import br.com.dragonmc.core.common.server.ServerType;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerListener
implements Listener {
    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            return;
        }
        Player player = event.getPlayer();
        Member member = CommonPlugin.getInstance().getMemberManager().getMember(player.getUniqueId());
        if (member == null) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "\u00a7cN\u00e3o foi poss\u00edvel carregar sua conta, tente novamente.");
            return;
        }
        CoreMain.getInstance().getGamerManager().loadGamer(member.getUniqueId(), new Gamer(member));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Member member = CommonPlugin.getInstance().getMemberManager().getMember(player.getUniqueId());
        player.teleport(BukkitCommon.getInstance().getLocationManager().getLocation("spawn"));
        player.setGameMode(GameMode.ADVENTURE);
        if (player.hasPermission("command.fly")) {
            player.setAllowFlight(true);
            player.setFlying(true);
            player.teleport(player.getLocation().add(0.0, 2.0, 0.0));
        }
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 4));
        player.setHealth(player.getMaxHealth());
        player.setHealthScale(1.0);
        player.setFoodLevel(20);
        player.setExp(0.0f);
        player.setLevel(0);
        player.setTotalExperience(0);
        CoreMain.getInstance().getPlayerInventory().handle(event.getPlayer());
        if (CommonPlugin.getInstance().getServerType() == ServerType.LOBBY && member.getSessionTime() <= 10000L) {
            PlayerHelper.title(player, "\u00a7b\u00a7LDRAGON", "\u00a7eSeja bem-vindo!");
        }
        PlayerHelper.setHeaderAndFooter(player, "\n\u00a7b\u00a7lDRAGON\n", LobbyConst.TAB_FOOTER);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        CoreMain.getInstance().getGamerManager().unloadGamer(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getTo().getBlockY() < 1) {
            event.getPlayer().teleport(BukkitCommon.getInstance().getLocationManager().getLocation("spawn"));
        } else if (event.getPlayer().getLocation().subtract(0.0, 1.0, 0.0).getBlock().getType() == Material.SLIME_BLOCK) {
            event.getPlayer().setVelocity(event.getPlayer().getLocation().getDirection().multiply(2).setY(0.6));
        }
    }
}

