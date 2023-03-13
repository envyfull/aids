/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.game.engine.command;

import br.com.dragonmc.game.engine.GameAPI;
import br.com.dragonmc.core.common.command.CommandArgs;
import br.com.dragonmc.core.common.command.CommandClass;
import br.com.dragonmc.core.common.command.CommandFramework;
import br.com.dragonmc.core.common.command.CommandSender;
import br.com.dragonmc.core.common.utils.DateUtils;
import br.com.dragonmc.core.common.utils.string.StringFormat;

public class ModeradorCommand
implements CommandClass {
    @CommandFramework.Command(name="time", aliases={"tempo"}, permission="command.time")
    public void timeCommand(CommandArgs cmdArgs) {
        long time;
        CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();
        if (args.length == 0) {
            sender.sendMessage(sender.getLanguage().t("command-time-usage", "%label%", cmdArgs.getLabel()));
            return;
        }
        if (args[0].equalsIgnoreCase("stop")) {
            GameAPI.getInstance().setTimer(!GameAPI.getInstance().isTimer());
            GameAPI.getInstance().setConsoleControl(false);
            sender.sendMessage("\u00a7%command-time-timer-" + (GameAPI.getInstance().isTimer() ? "enabled" : "disabled") + "%\u00a7");
            return;
        }
        try {
            time = DateUtils.parseDateDiff(args[0], true);
        }
        catch (Exception e) {
            sender.sendMessage(sender.getLanguage().t("number-format-invalid", "%number%", args[0]));
            return;
        }
        int seconds = (int)Math.floor((time - System.currentTimeMillis()) / 1000L);
        if (seconds >= 7200) {
            seconds = 7200;
        }
        sender.sendMessage(sender.getLanguage().t("command-time-changed", "%time%", StringFormat.formatTime(seconds, StringFormat.TimeFormat.NORMAL)));
        GameAPI.getInstance().setTime(seconds);
    }
}

