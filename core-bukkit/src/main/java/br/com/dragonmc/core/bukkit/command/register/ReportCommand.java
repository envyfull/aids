/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.core.bukkit.command.register;

import java.util.Optional;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.bukkit.BukkitCommon;
import br.com.dragonmc.core.bukkit.member.BukkitMember;
import br.com.dragonmc.core.bukkit.menu.report.ReportInventory;
import br.com.dragonmc.core.bukkit.menu.report.ReportListInventory;
import br.com.dragonmc.core.common.command.CommandArgs;
import br.com.dragonmc.core.common.command.CommandClass;
import br.com.dragonmc.core.common.command.CommandFramework;
import br.com.dragonmc.core.common.report.Report;
import org.bukkit.entity.Player;

public class ReportCommand
implements CommandClass {
    @CommandFramework.Command(name="report", aliases={"reports"}, permission="command.report", console=false)
    public void reportCommand(CommandArgs cmdArgs) {
        Player sender = cmdArgs.getSenderAsMember(BukkitMember.class).getPlayer();
        String[] args = cmdArgs.getArgs();
        if (args.length == 0) {
            new ReportListInventory(sender, 1);
            return;
        }
        Optional<Player> optional = BukkitCommon.getPlayer(args[0], false);
        if (!optional.isPresent()) {
            sender.sendMessage(cmdArgs.getSender().getLanguage().t("player-is-not-online", "%player%", args[0]));
            return;
        }
        Player target = optional.get();
        Report report = CommonPlugin.getInstance().getReportManager().getReportById(target.getUniqueId());
        if (report == null) {
            new ReportListInventory(sender, 1);
        } else {
            new ReportInventory(target, report, new ReportListInventory(sender, 1));
        }
    }
}

