/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  net.md_5.bungee.api.ChatColor
 */
package br.com.dragonmc.core.bukkit.command.register;

import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import br.com.dragonmc.core.bukkit.menu.staff.LanguageInventory;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.bukkit.member.BukkitMember;
import br.com.dragonmc.core.common.command.CommandArgs;
import br.com.dragonmc.core.common.command.CommandClass;
import br.com.dragonmc.core.common.command.CommandFramework;
import br.com.dragonmc.core.common.command.CommandSender;
import br.com.dragonmc.core.common.language.Language;
import br.com.dragonmc.core.common.member.Member;
import net.md_5.bungee.api.ChatColor;

public class LanguageCommand
implements CommandClass {
    @CommandFramework.Command(name="language", aliases={"lang", "lingua", "linguagem", "idioma", "idiomas"}, permission="command.language")
    public void languageCommand(CommandArgs cmdArgs) {
        CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();
        if (args.length == 0) {
            if (cmdArgs.isPlayer()) {
                new br.com.dragonmc.core.bukkit.menu.LanguageInventory(((BukkitMember)sender).getPlayer());
            } else {
                sender.sendMessage(sender.getLanguage().t("command-language-usage", "%label%", cmdArgs.getLabel()));
            }
            return;
        }
        Language language = Language.getLanguageByName(args[0]);
        if (language == null) {
            sender.sendMessage(sender.getLanguage().t("language-not-found", "%language%", args[0]));
            return;
        }
        if (cmdArgs.isPlayer()) {
            ((Member)sender).setLanguage(language);
        } else {
            CommonPlugin.getInstance().getPluginInfo().setDefaultLanguage(language);
        }
        sender.sendMessage(sender.getLanguage().t("command-language-changed", "%language%", language.getLanguageName()));
    }

    @CommandFramework.Command(name="translate", permission="command.translate")
    public void translateCommand(CommandArgs cmdArgs) {
        CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();
        if (args.length == 0) {
            sender.sendMessage(sender.getLanguage().t("command-translate-usage", "%label%", cmdArgs.getLabel()));
            if (cmdArgs.isPlayer()) {
                new LanguageInventory(((BukkitMember)sender).getPlayer());
            }
            return;
        }
        Language language = Language.getLanguageByName(args[0]);
        if (language == null) {
            sender.sendMessage(sender.getLanguage().t("language-not-found", "%language%", args[0]));
            return;
        }
        if (args.length == 1) {
            sender.sendMessage("  \u00a7aIdioma " + language.getLanguageName());
            sender.sendMessage("    \u00a7fTotal de tradu\u00e7\u00f5es: \u00a77" + CommonPlugin.getInstance().getPluginInfo().getLanguageMap().get((Object)language).size());
            sender.sendMessage("    \u00a7fTotal de tradu\u00e7\u00f5es incompletas: \u00a77" + CommonPlugin.getInstance().getPluginInfo().getLanguageMap().get((Object)language).entrySet().stream().filter(entry -> ((String)entry.getValue()).startsWith("[NOT FOUND: ")).count());
            for (Map.Entry<String, String> entry2 : CommonPlugin.getInstance().getPluginInfo().getLanguageMap().get((Object)language).entrySet()) {
                if (!entry2.getValue().startsWith("[NOT FOUND: ")) continue;
                sender.sendMessage("      \u00a7f- " + entry2.getKey());
            }
            return;
        }
        String translateKey = args[1];
        if (args.length == 2) {
            sender.sendMessage("  \u00a7aTradu\u00e7\u00e3o da " + translateKey + " no idioma " + language.getLanguageName());
            sender.sendMessage("    \u00a7f- " + language.t(translateKey, new String[0]));
        } else {
            String translate = Joiner.on((char)' ').join((Object[])Arrays.copyOfRange(args, 2, args.length)).replace("|n", "\n");
            CommonPlugin.getInstance().getPluginInfo().addTranslate(language, translateKey, translate);
            sender.sendMessage("\u00a7aA tradu\u00e7\u00e3o do idioma " + language.name() + " para do key " + translateKey + " foi alterada para " + ChatColor.translateAlternateColorCodes((char)'&', (String)translate));
            CommonPlugin.getInstance().saveConfig();
        }
    }

    @CommandFramework.Completer(name="language", aliases={"lang", "lingua", "linguagem", "idioma", "idiomas"})
    public List<String> languageCompleter(CommandArgs cmdArgs) {
        ArrayList<String> languageList;
        block4: {
            languageList = new ArrayList<String>();
            if (cmdArgs.getArgs().length != 1) break block4;
            if (cmdArgs.getArgs()[0].isEmpty()) {
                for (Language language : Language.values()) {
                    languageList.add(language.name());
                }
            } else {
                for (Language language : Language.values()) {
                    if (!language.name().toLowerCase().startsWith(cmdArgs.getArgs()[0].toLowerCase())) continue;
                    languageList.add(language.name());
                }
            }
        }
        return languageList;
    }

    @CommandFramework.Completer(name="translate")
    public List<String> translateCompleter(CommandArgs cmdArgs) {
        ArrayList<String> translateList;
        block6: {
            Language language;
            block5: {
                translateList = new ArrayList<String>();
                if (cmdArgs.getArgs().length != 1) break block5;
                if (cmdArgs.getArgs()[0].isEmpty()) {
                    for (Language language2 : Language.values()) {
                        translateList.add(language2.name());
                    }
                } else {
                    for (Language language3 : Language.values()) {
                        if (!language3.name().toLowerCase().startsWith(cmdArgs.getArgs()[0].toLowerCase())) continue;
                        translateList.add(language3.name());
                    }
                }
                break block6;
            }
            if (cmdArgs.getArgs().length != 2 || (language = Language.getLanguageByName(cmdArgs.getArgs()[0])) == null) break block6;
            for (Map.Entry entry : CommonPlugin.getInstance().getPluginInfo().getLanguageMap().computeIfAbsent(language, v -> new HashMap()).entrySet().stream().sorted((o1, o2) -> ((String)o1.getKey()).compareTo((String)o2.getKey())).collect(Collectors.toList())) {
                if (!((String)entry.getKey()).toLowerCase().startsWith(cmdArgs.getArgs()[1].toLowerCase())) continue;
                translateList.add(((String)entry.getKey()).toLowerCase());
            }
        }
        return translateList;
    }
}

