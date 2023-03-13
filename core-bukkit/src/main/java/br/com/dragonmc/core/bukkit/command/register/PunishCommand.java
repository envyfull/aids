/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.OfflinePlayer
 */
package br.com.dragonmc.core.bukkit.command.register;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import br.com.dragonmc.core.common.command.CommandArgs;
import br.com.dragonmc.core.common.command.CommandClass;
import br.com.dragonmc.core.common.command.CommandFramework;
import br.com.dragonmc.core.common.member.Member;
import br.com.dragonmc.core.common.member.configuration.MemberConfiguration;
import br.com.dragonmc.core.common.punish.PunishType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class PunishCommand
implements CommandClass {
    @CommandFramework.Command(name="ban", aliases={"kick", "mute", "tempban", "tempmute", "tempbanir", "tempmutar", "punish"}, permission="command.punish")
    public void banCommand(CommandArgs cmdArgs) {
    }

    @CommandFramework.Command(name="pardon", aliases={"unpunish", "unban", "unmute"}, permission="command.pardon")
    public void pardonCommand(CommandArgs cmdArgs) {
    }

    @CommandFramework.Command(name="anticheat", aliases={"ac"}, permission="command.anticheat", console=false)
    public void anticheatCommand(CommandArgs cmdArgs) {
        Member member = cmdArgs.getSenderAsMember(Member.class);
        member.getMemberConfiguration().setCheatState(member.getMemberConfiguration().isAnticheatEnabled() ? MemberConfiguration.CheatState.DISABLED : MemberConfiguration.CheatState.ENABLED);
        member.sendMessage(member.getLanguage().t("command.anticheat.state-" + member.getMemberConfiguration().getCheatState().name().toLowerCase(), new String[0]));
    }

    @CommandFramework.Completer(name="punish")
    public List<String> banCompleter(CommandArgs cmdArgs) {
        return cmdArgs.getArgs().length == 2 ? Arrays.asList(PunishType.values()).stream().filter(state -> state.name().toLowerCase().startsWith(cmdArgs.getArgs()[1].toLowerCase())).map(Enum::name).collect(Collectors.toList()) : Bukkit.getOnlinePlayers().stream().filter(player -> player.getName().toLowerCase().startsWith(cmdArgs.getArgs()[cmdArgs.getArgs().length - 1].toLowerCase())).map(OfflinePlayer::getName).collect(Collectors.toList());
    }
}

