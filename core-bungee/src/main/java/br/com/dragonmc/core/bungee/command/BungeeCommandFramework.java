/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.api.CommandSender
 *  net.md_5.bungee.api.chat.TextComponent
 *  net.md_5.bungee.api.connection.ProxiedPlayer
 *  net.md_5.bungee.api.event.TabCompleteEvent
 *  net.md_5.bungee.api.plugin.Command
 *  net.md_5.bungee.api.plugin.Listener
 *  net.md_5.bungee.api.plugin.Plugin
 *  net.md_5.bungee.event.EventHandler
 */
package br.com.dragonmc.core.bungee.command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.common.command.CommandArgs;
import br.com.dragonmc.core.common.command.CommandClass;
import br.com.dragonmc.core.common.command.CommandFramework;
import br.com.dragonmc.core.common.member.Member;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public class BungeeCommandFramework
implements CommandFramework {
    private final Map<String, Map.Entry<Method, Object>> commandMap = new HashMap<String, Map.Entry<Method, Object>>();
    private final Map<String, Map.Entry<Method, Object>> completers = new HashMap<String, Map.Entry<Method, Object>>();
    private final Plugin plugin;

    public BungeeCommandFramework(Plugin plugin) {
        this.plugin = plugin;
        this.plugin.getProxy().getPluginManager().registerListener(plugin, (Listener)new BungeeCompleter());
    }

    public boolean handleCommand(final CommandSender sender, final String label, final String[] args) {
        for (int i = args.length; i >= 0; --i) {
            StringBuilder buffer = new StringBuilder();
            buffer.append(label.toLowerCase());
            for (int x = 0; x < i; ++x) {
                buffer.append(".").append(args[x].toLowerCase());
            }
            final String cmdLabel = buffer.toString();
            if (!this.commandMap.containsKey(cmdLabel)) continue;
            final Map.Entry<Method, Object> entry = this.commandMap.get(cmdLabel);
            Command command = entry.getKey().getAnnotation(Command.class);
            if (sender instanceof ProxiedPlayer) {
                ProxiedPlayer p = (ProxiedPlayer)sender;
                Member member = CommonPlugin.getInstance().getMemberManager().getMember(p.getUniqueId());
                if (member == null) {
                    p.disconnect(TextComponent.fromLegacyText((String)"ERRO"));
                    return true;
                }
                if (!command.permission().isEmpty() && !member.hasPermission(command.permission())) {
                    member.sendMessage(member.getLanguage().t("no-permission", new String[0]));
                    return true;
                }
            } else if (!command.console()) {
                sender.sendMessage(CommonPlugin.getInstance().getPluginInfo().translate("command-only-for-player"));
                return true;
            }
            if (command.runAsync()) {
                this.plugin.getProxy().getScheduler().runAsync(this.plugin, new Runnable(){

                    @Override
                    public void run() {
                        try {
                            ((Method)entry.getKey()).invoke(entry.getValue(), new BungeeCommandArgs(sender, label.replace(".", " "), args, cmdLabel.split("\\.").length - 1));
                        }
                        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                try {
                    entry.getKey().invoke(entry.getValue(), new BungeeCommandArgs(sender, label, args, cmdLabel.split("\\.").length - 1));
                }
                catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
        this.defaultCommand(new BungeeCommandArgs(sender, label, args, 0));
        return true;
    }

    @Override
    public void registerCommands(CommandClass cls) {
        for (Method m : cls.getClass().getMethods()) {
            if (m.getAnnotation(Command.class) != null) {
                Command command = m.getAnnotation(Command.class);
                if (m.getParameterTypes().length > 1 || m.getParameterTypes().length <= 0 || !CommandArgs.class.isAssignableFrom(m.getParameterTypes()[0])) {
                    System.out.println("Unable to register command " + m.getName() + ". Unexpected method arguments");
                    continue;
                }
                this.registerCommand(command, command.name(), m, cls);
                for (String alias : command.aliases()) {
                    this.registerCommand(command, alias, m, cls);
                }
                continue;
            }
            if (m.getAnnotation(Completer.class) == null) continue;
            Completer comp = m.getAnnotation(Completer.class);
            if (m.getParameterTypes().length > 1 || m.getParameterTypes().length <= 0 || !CommandArgs.class.isAssignableFrom(m.getParameterTypes()[0])) {
                System.out.println("Unable to register tab completer " + m.getName() + ". Unexpected method arguments");
                continue;
            }
            if (m.getReturnType() != List.class) {
                System.out.println("Unable to register tab completer " + m.getName() + ". Unexpected return type");
                continue;
            }
            this.registerCompleter(comp.name(), m, cls);
            for (String alias : comp.aliases()) {
                this.registerCompleter(alias, m, cls);
            }
        }
    }

    private void registerCommand(Command command, String label, Method m, Object obj) {
        AbstractMap.SimpleEntry<Method, Object> entry = new AbstractMap.SimpleEntry<Method, Object>(m, obj);
        this.commandMap.put(label.toLowerCase(), entry);
        String cmdLabel = label.replace(".", ",").split(",")[0].toLowerCase();
        this.plugin.getProxy().getPluginManager().registerCommand(this.plugin, new BungeeCommand(cmdLabel));
    }

    private void registerCompleter(String label, Method m, Object obj) {
        this.completers.put(label, new AbstractMap.SimpleEntry<Method, Object>(m, obj));
    }

    private void defaultCommand(CommandArgs args) {
        args.getSender().sendMessage("\u00a7cComando do bungeecord inacess\u00edvel!");
    }

    @Override
    public Class<?> getJarClass() {
        return this.plugin.getClass();
    }

    public class BungeeCompleter
    implements Listener {
        @EventHandler
        public void onTabComplete(TabCompleteEvent event) {
            if (!(event.getSender() instanceof ProxiedPlayer)) {
                return;
            }
            ProxiedPlayer player = (ProxiedPlayer)event.getSender();
            String[] split = event.getCursor().replaceAll("\\s+", " ").split(" ");
            if (split.length == 0) {
                return;
            }
            String[] args = new String[split.length - 1];
            for (int i = 1; i < split.length; ++i) {
                args[i - 1] = split[i];
            }
            String label = split[0].substring(1);
            for (int i = args.length; i >= 0; --i) {
                StringBuilder buffer = new StringBuilder();
                buffer.append(label.toLowerCase());
                for (int x = 0; x < i; ++x) {
                    buffer.append(".").append(args[x].toLowerCase());
                }
                String cmdLabel = buffer.toString();
                if (!BungeeCommandFramework.this.completers.containsKey(cmdLabel)) continue;
                Map.Entry entry = (Map.Entry)BungeeCommandFramework.this.completers.get(cmdLabel);
                try {
                    event.getSuggestions().clear();
                    List list = (List)((Method)entry.getKey()).invoke(entry.getValue(), new BungeeCommandArgs((CommandSender)player, label, args, cmdLabel.split("\\.").length - 1));
                    event.getSuggestions().addAll(list);
                    continue;
                }
                catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class BungeeCommand
    extends net.md_5.bungee.api.plugin.Command {
        protected BungeeCommand(String label) {
            super(label);
        }

        protected BungeeCommand(String label, String permission) {
            super(label, permission, new String[0]);
        }

        public void execute(CommandSender sender, String[] args) {
            BungeeCommandFramework.this.handleCommand(sender, this.getName(), args);
        }
    }
}

