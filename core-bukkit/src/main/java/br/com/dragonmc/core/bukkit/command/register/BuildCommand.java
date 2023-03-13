/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.block.BlockState
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.core.bukkit.command.register;

import java.util.HashMap;
import java.util.Map;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.bukkit.BukkitCommon;
import br.com.dragonmc.core.bukkit.member.BukkitMember;
import br.com.dragonmc.core.common.command.CommandArgs;
import br.com.dragonmc.core.common.command.CommandClass;
import br.com.dragonmc.core.common.command.CommandFramework;
import br.com.dragonmc.core.common.command.CommandSender;
import br.com.dragonmc.core.common.server.ServerType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

public class BuildCommand
implements CommandClass {
    @CommandFramework.Command(name="build", aliases={"b"}, permission="command.build", console=false)
    public void buildCommand(CommandArgs cmdArgs) {
        CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();
        if (args.length == 0) {
            if (CommonPlugin.getInstance().getServerType() == ServerType.BUILD || sender.hasPermission("command.build-bypass")) {
                BukkitMember player = (BukkitMember)CommonPlugin.getInstance().getMemberManager().getMemberByName(args[0], BukkitMember.class);
                player.setBuildEnabled(!(player = cmdArgs.getSenderAsMember(BukkitMember.class)).isBuildEnabled());
                player.sendMessage("\u00a7%command-build-" + (player.isBuildEnabled() ? "enabled" : "disabled") + "%\u00a7");
            } else {
                sender.sendMessage("\u00a7cVoc\u00ea n\u00e3o tem acesso a esse comando neste servidor no momento.");
            }
            return;
        }
        if (sender.hasPermission("command.build-bypass")) {
            BukkitMember player = CommonPlugin.getInstance().getMemberManager().getMemberByName(args[0], BukkitMember.class);
            if (player == null) {
                sender.sendMessage(sender.getLanguage().t("player-is-not-online", "%player%", args[0]));
                return;
            }
            player.setBuildEnabled(!player.isBuildEnabled());
            sender.sendMessage(sender.getLanguage().t("command-build-target-" + (player.isBuildEnabled() ? "enabled" : "disabled"), "%target%", player.getName()));
            player.sendMessage("\u00a7%command-build-" + (player.isBuildEnabled() ? "enabled" : "disabled") + "%\u00a7");
        } else {
            sender.sendMessage("\u00a7cVoc\u00ea n\u00e3o tem acesso a esse comando neste servidor no momento.");
        }
    }

    @CommandFramework.Command(name="wand", permission="command.build")
    public void wandCommand(CommandArgs cmdArgs) {
        if (!cmdArgs.isPlayer()) {
            return;
        }
        BukkitCommon.getInstance().getBlockManager().giveWand(cmdArgs.getSenderAsMember(BukkitMember.class).getPlayer());
        cmdArgs.getSender().sendMessage(" \u00a7a* \u00a7fVoc\u00ea recebeu a varinha do \u00a7aWorldedit\u00a7f!");
    }

    @CommandFramework.Command(name="set", permission="command.build")
    public void setCommand(CommandArgs cmdArgs) {
        if (!cmdArgs.isPlayer()) {
            return;
        }
        Player player = ((BukkitMember)cmdArgs.getSender()).getPlayer();
        String[] args = cmdArgs.getArgs();
        if (args.length == 0) {
            player.sendMessage(" \u00a7e\u00bb \u00a7fUse \u00a7a/set <material:id>\u00a7f para setar um grupo.");
            return;
        }
        Material blockMaterial = null;
        byte blockId = 0;
        if (args[0].contains(":")) {
            blockMaterial = Material.getMaterial((String)args[0].split(":")[0].toUpperCase());
            if (blockMaterial == null) {
                try {
                    blockMaterial = Material.getMaterial((int)Integer.valueOf(args[0].split(":")[0]));
                }
                catch (NumberFormatException e) {
                    player.sendMessage(" \u00a7c\u00bb \u00a7fN\u00e3o foi poss\u00edvel encontrar esse bloco!");
                    return;
                }
            }
            try {
                blockId = Byte.valueOf(args[0].split(":")[1]);
            }
            catch (Exception e) {
                player.sendMessage(" \u00a7c\u00bb \u00a7fO bloco " + args[0] + " n\u00e3o existe!");
                return;
            }
        }
        blockMaterial = Material.getMaterial((String)args[0]);
        if (blockMaterial == null) {
            try {
                blockMaterial = Material.getMaterial((int)Integer.valueOf(args[0]));
            }
            catch (NumberFormatException e) {
                player.sendMessage(" \u00a7c\u00bb \u00a7fN\u00e3o foi poss\u00edvel encontrar esse bloco!");
                return;
            }
        }
        if (blockMaterial == null) {
            player.sendMessage(" \u00a7c\u00bb \u00a7fN\u00e3o foi poss\u00edvel encontrar o bloco " + args[0] + "!");
            return;
        }
        if (!BukkitCommon.getInstance().getBlockManager().hasFirstPosition(player)) {
            player.sendMessage("\u00a7cA primeira posi\u00e7\u00e3o n\u00e3o foi setada!");
            return;
        }
        if (!BukkitCommon.getInstance().getBlockManager().hasSecondPosition(player)) {
            player.sendMessage("\u00a7cA segunda posi\u00e7\u00e3o n\u00e3o foi setada!");
            return;
        }
        Location first = BukkitCommon.getInstance().getBlockManager().getFirstPosition(player);
        Location second = BukkitCommon.getInstance().getBlockManager().getSecondPosition(player);
        HashMap<Location, BlockState> map = new HashMap<Location, BlockState>();
        int amount = 0;
        for (Location location : BukkitCommon.getInstance().getBlockManager().getLocationsFromTwoPoints(first, second)) {
            map.put(location.clone(), location.getBlock().getState());
            if (location.getBlock().getType() == blockMaterial && location.getBlock().getData() == blockId) continue;
            BukkitCommon.getInstance().getBlockManager().setBlockFast(location.getWorld(), location, blockMaterial.getId(), blockId);
            ++amount;
        }
        BukkitCommon.getInstance().getBlockManager().addUndo(player, map);
        player.sendMessage("\u00a7dVoc\u00ea colocou " + amount + " blocos!");
    }

    @CommandFramework.Command(name="undo", permission="command.build")
    public void undoCommand(CommandArgs cmdArgs) {
        if (!cmdArgs.isPlayer()) {
            return;
        }
        Player player = ((BukkitMember)cmdArgs.getSender()).getPlayer();
        if (!BukkitCommon.getInstance().getBlockManager().hasUndoList(player)) {
            player.sendMessage("\u00a7cVoc\u00ea n\u00e3o tem nada para desfazer");
            return;
        }
        Map<Location, BlockState> map = BukkitCommon.getInstance().getBlockManager().getUndoList(player).get(BukkitCommon.getInstance().getBlockManager().getUndoList(player).size() - 1);
        int amount = 0;
        for (Map.Entry<Location, BlockState> entry : map.entrySet()) {
            BukkitCommon.getInstance().getBlockManager().setBlockFast(entry.getKey().getWorld(), entry.getKey(), entry.getValue().getType().getId(), entry.getValue().getData().getData());
            ++amount;
        }
        BukkitCommon.getInstance().getBlockManager().removeUndo(player, map);
        player.sendMessage("\u00a7dVoc\u00ea colocou " + amount + " blocos!");
    }
}

