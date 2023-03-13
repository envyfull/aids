/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.event.Event
 */
package br.com.dragonmc.core.bukkit.command.register;

import br.com.dragonmc.core.bukkit.event.member.PlayerAuthEvent;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.bukkit.BukkitCommon;
import br.com.dragonmc.core.bukkit.member.BukkitMember;
import br.com.dragonmc.core.common.command.CommandArgs;
import br.com.dragonmc.core.common.command.CommandClass;
import br.com.dragonmc.core.common.command.CommandFramework;
import br.com.dragonmc.core.common.member.configuration.LoginConfiguration;
import br.com.dragonmc.core.common.server.ServerType;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;

public class LoginCommand
implements CommandClass {
    @CommandFramework.Command(name="register", aliases={"registrar"}, console=false)
    public void registerCommand(CommandArgs cmdArgs) {
        BukkitMember sender = cmdArgs.getSenderAsMember(BukkitMember.class);
        String[] args = cmdArgs.getArgs();
        if (sender.getLoginConfiguration().isRegistered()) {
            sender.sendMessage("\u00a7cVoc\u00ea j\u00e1 est\u00e1 registrado.");
            return;
        }
        if (sender.getLoginConfiguration().getAccountType() != LoginConfiguration.AccountType.PREMIUM && !sender.getLoginConfiguration().isCaptcha()) {
            sender.sendMessage("\u00a7cComplete o captcha para se logar.");
            return;
        }
        if (args.length <= 1) {
            sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " <sua senha> <repita sua senha>\u00a7f para se registrar.");
            sender.sendMessage("\u00a7cN\u00e3o utilize coloque os s\u00edmbolos < e > na sua senha.");
            return;
        }
        if (args[0].equals(args[1])) {
            sender.sendMessage("\u00a7%command.register.success%\u00a7");
            sender.getLoginConfiguration().register(args[0]);
            sender.getLoginConfiguration().startSession();
            Bukkit.getPluginManager().callEvent((Event)new PlayerAuthEvent(sender.getPlayer(), sender));
        } else {
            sender.sendMessage("\u00a7cAs senhas inseridas n\u00e3o s\u00e3o iguais.");
        }
    }

    @CommandFramework.Command(name="logout", aliases={"deslogar"}, console=false)
    public void logoutCommand(CommandArgs cmdArgs) {
        BukkitMember sender = cmdArgs.getSenderAsMember(BukkitMember.class);
        if (sender.getLoginConfiguration().getAccountType() == LoginConfiguration.AccountType.PREMIUM) {
            return;
        }
        sender.getLoginConfiguration().logOut();
        sender.getLoginConfiguration().stopSession();
        BukkitCommon.getInstance().sendPlayerToServer(sender.getPlayer(), ServerType.LOGIN);
    }

    @CommandFramework.Command(name="login", aliases={"logar"}, console=false)
    public void loginCommand(CommandArgs cmdArgs) {
        BukkitMember sender = cmdArgs.getSenderAsMember(BukkitMember.class);
        String[] args = cmdArgs.getArgs();
        if (!sender.getLoginConfiguration().isRegistered()) {
            sender.sendMessage("\u00a7cVoc\u00ea ainda n\u00e3o se registrou, utilize /register para se autenticar.");
            return;
        }
        if (sender.getLoginConfiguration().isLogged()) {
            sender.sendMessage("\u00a7cVoc\u00ea j\u00e1 est\u00e1 logado.");
            return;
        }
        if (args.length == 0) {
            sender.sendMessage(" \u00a7a\u00bb \u00a7fUse \u00a7a/" + cmdArgs.getLabel() + " <sua senha>\u00a7f para se logar.");
            return;
        }
        if (sender.getLoginConfiguration().isPassword(args[0])) {
            sender.sendMessage("\u00a7%command.login.success%\u00a7");
            sender.getLoginConfiguration().logIn();
            sender.getLoginConfiguration().startSession();
            Bukkit.getPluginManager().callEvent((Event)new PlayerAuthEvent(sender.getPlayer(), sender));
        } else {
            int attemp = sender.getLoginConfiguration().attemp();
            if (attemp >= 5) {
                sender.getPlayer().kickPlayer("\u00a7cVoc\u00ea errou sua senha diversas vezes.\n\u00a7f\n\u00a7ePara mais informa\u00e7\u00f5es, acesse \u00a7b" + CommonPlugin.getInstance().getPluginInfo().getWebsite());
            } else {
                sender.sendMessage("\u00a7cSenha inserida inv\u00e1lida, voc\u00ea possui mais " + (5 - attemp) + "tentativas.");
            }
        }
    }
}

