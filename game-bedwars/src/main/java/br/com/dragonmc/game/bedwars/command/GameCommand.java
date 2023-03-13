/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.game.bedwars.command;

import com.google.common.base.Joiner;
import br.com.dragonmc.game.engine.GameAPI;
import br.com.dragonmc.game.bedwars.GameMain;
import br.com.dragonmc.game.bedwars.island.Island;
import br.com.dragonmc.game.bedwars.store.ShopCategory;
import br.com.dragonmc.game.bedwars.utils.GamerHelper;
import br.com.dragonmc.core.bukkit.member.BukkitMember;
import br.com.dragonmc.core.bukkit.utils.item.ItemBuilder;
import br.com.dragonmc.core.bukkit.utils.menu.MenuInventory;
import br.com.dragonmc.core.common.command.CommandArgs;
import br.com.dragonmc.core.common.command.CommandClass;
import br.com.dragonmc.core.common.command.CommandFramework;
import br.com.dragonmc.core.common.member.status.Status;
import br.com.dragonmc.core.common.member.status.StatusType;
import br.com.dragonmc.core.common.member.status.types.BedwarsCategory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class GameCommand
implements CommandClass {
    @CommandFramework.Command(name="global", aliases={"shout", "g"}, console=false)
    public void globalCommand(CommandArgs cmdArgs) {
        Player sender = cmdArgs.getSenderAsMember(BukkitMember.class).getPlayer();
        if (GameMain.getInstance().getPlayersPerTeam() == 1) {
            sender.sendMessage("\u00a7cO comando est\u00e1 desativado nessa sala.");
            return;
        }
        Object[] args = cmdArgs.getArgs();
        if (!GameAPI.getInstance().getState().isGametime()) {
            sender.sendMessage("\u00a7cO jogo ainda n\u00e3o come\u00e7ou.");
            return;
        }
        if (args.length == 0) {
            sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " <message>\u00a7f para mandar uma mensagem no servidor.");
            return;
        }
        Island island = GameMain.getInstance().getIslandManager().getIsland(sender.getUniqueId());
        if (island == null) {
            sender.sendMessage("\u00a7cSomente jogadores com uma ilha podem utilizar esse comando.");
            return;
        }
        if (island.getIslandStatus() == Island.IslandStatus.LOSER) {
            sender.sendMessage("\u00a7cVoc\u00ea n\u00e3o pode mais falar no chat.");
        } else {
            Status status = GameAPI.getInstance().getPlugin().getStatusManager().loadStatus(sender.getUniqueId(), StatusType.BEDWARS);
            int level = status.getInteger(BedwarsCategory.BEDWARS_LEVEL);
            String message = GameMain.getInstance().createMessage(sender, Joiner.on((char)' ').join(args), island, true, true, level);
            Bukkit.getOnlinePlayers().forEach(ps -> ps.sendMessage(message));
        }
    }

    @CommandFramework.Command(name="rastreador", aliases={"bussola", "compass"}, console=false)
    public void rastreadorCommand(CommandArgs cmdArgs) {
        MenuInventory menuInventory = new MenuInventory("\u00a77Rastreador", 3);
        menuInventory.setItem(13, new ItemBuilder().name("\u00a7aRastreador").lore("\u00a722 esmeraldas").type(Material.COMPASS).build(), (player, inv, type, stack, slot) -> GamerHelper.buyItem(player, new ShopCategory.ShopItem(stack, new ShopCategory.ShopPrice(Material.EMERALD, 2))));
        menuInventory.open(cmdArgs.getSenderAsMember(BukkitMember.class).getPlayer());
    }
}

