/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.apache.commons.lang.Validate
 *  org.bukkit.Bukkit
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandException
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandMap
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.PluginCommand
 *  org.bukkit.command.TabCompleter
 *  org.bukkit.entity.Player
 *  org.bukkit.help.GenericCommandHelpTopic
 *  org.bukkit.help.HelpTopic
 *  org.bukkit.help.HelpTopicComparator
 *  org.bukkit.help.IndexHelpTopic
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.SimplePluginManager
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.spigotmc.CustomTimingsHandler
 */
package br.com.dragonmc.core.bukkit.command;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.bukkit.BukkitCommon;
import br.com.dragonmc.core.common.command.CommandArgs;
import br.com.dragonmc.core.common.command.CommandClass;
import br.com.dragonmc.core.common.command.CommandFramework;
import br.com.dragonmc.core.common.server.ServerType;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.help.GenericCommandHelpTopic;
import org.bukkit.help.HelpTopic;
import org.bukkit.help.HelpTopicComparator;
import org.bukkit.help.IndexHelpTopic;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.scheduler.BukkitRunnable;

public class BukkitCommandFramework
        implements CommandFramework {
    public static final BukkitCommandFramework INSTANCE = new BukkitCommandFramework((Plugin)BukkitCommon.getInstance());
    private Plugin plugin;
    private final Map<String, Map.Entry<Method, Object>> commandMap = new HashMap<String, Map.Entry<Method, Object>>();
    private CommandMap map;
    private Map<String, org.bukkit.command.Command> knownCommands;

    public BukkitCommandFramework(Plugin plugin) {
        this.plugin = plugin;
        if (plugin.getServer().getPluginManager() instanceof SimplePluginManager) {
            Field field;
            SimplePluginManager manager = (SimplePluginManager)plugin.getServer().getPluginManager();
            try {
                field = SimplePluginManager.class.getDeclaredField("commandMap");
                field.setAccessible(true);
                this.map = (CommandMap)field.get(manager);
            }
            catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
                e.printStackTrace();
            }
            try {
                field = this.map.getClass().getDeclaredField("knownCommands");
                field.setAccessible(true);
                this.knownCommands = (HashMap)field.get(this.map);
            }
            catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean handleCommand(final CommandSender sender, final String label, Command cmd, final String[] args) {
        for (int i = args.length; i >= 0; --i) {
            StringBuilder buffer = new StringBuilder();
            buffer.append(label.toLowerCase());
            for (int x = 0; x < i; ++x) {
                buffer.append(".").append(args[x].toLowerCase());
            }
            final String cmdLabel = buffer.toString();
            if (!this.commandMap.containsKey(cmdLabel)) continue;
            final Map.Entry<Method, Object> entry = this.commandMap.get(cmdLabel);
            CommandFramework.Command command = entry.getKey().getAnnotation(CommandFramework.Command.class);
            if (!command.console() && !(sender instanceof Player)) {
                sender.sendMessage("\u00a7%command-only-for-players%\u00a7");
                return true;
            }
            if (command.runAsync() && Bukkit.isPrimaryThread()) {
                new BukkitRunnable(){

                    public void run() {
                        try {
                            ((Method)entry.getKey()).invoke(entry.getValue(), new BukkitCommandArgs(sender, label, args, cmdLabel.split("\\.").length - 1));
                        }
                        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }.runTaskAsynchronously(this.plugin);
            } else {
                try {
                    entry.getKey().invoke(entry.getValue(), new BukkitCommandArgs(sender, label, args, cmdLabel.split("\\.").length - 1));
                }
                catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
        sender.sendMessage("\u00a7cO comando est\u00e1 inacess\u00edvel no momento!");
        return true;
    }

    @Override
    public void registerCommands(CommandClass commandClass) {
        for (Method m : commandClass.getClass().getMethods()) {
            if (m.getAnnotation(CommandFramework.Command.class) == null) continue;
            CommandFramework.Command command = m.getAnnotation(CommandFramework.Command.class);
            if (m.getParameterTypes().length > 1 || m.getParameterTypes().length <= 0 || !CommandArgs.class.isAssignableFrom(m.getParameterTypes()[0])) {
                System.out.println("Unable to register command " + m.getName() + ". Unexpected method arguments");
                continue;
            }
            this.registerCommand(command, command.name(), m, commandClass);
            for (String alias : command.aliases()) {
                this.registerCommand(command, alias, m, commandClass);
            }
        }
        for (Method m : commandClass.getClass().getMethods()) {
            if (m.getAnnotation(CommandFramework.Completer.class) == null) continue;
            CommandFramework.Completer comp = m.getAnnotation(CommandFramework.Completer.class);
            if (m.getParameterTypes().length > 1 || m.getParameterTypes().length == 0 || m.getParameterTypes()[0] != CommandArgs.class) {
                System.out.println("Unable to register tab completer " + m.getName() + ". Unexpected method arguments");
                continue;
            }
            if (m.getReturnType() != List.class) {
                System.out.println("Unable to register tab completer " + m.getName() + ". Unexpected return type");
                continue;
            }
            this.registerCompleter(comp.name(), m, commandClass);
            for (String alias : comp.aliases()) {
                this.registerCompleter(alias, m, commandClass);
            }
        }
    }

    public void registerHelp() {
        TreeSet<HelpTopic> help = new TreeSet<HelpTopic>(HelpTopicComparator.helpTopicComparatorInstance());
        for (String s : this.commandMap.keySet()) {
            if (s.contains(".")) continue;
            Command cmd = (Command) this.map.getCommand(s);
            GenericCommandHelpTopic topic = new GenericCommandHelpTopic((org.bukkit.command.Command) cmd);
            help.add(topic);
        }
        IndexHelpTopic topic = new IndexHelpTopic(this.plugin.getName(), "All commands for " + this.plugin.getName(), null, help, "Below is a list of all " + this.plugin.getName() + " commands:");
        Bukkit.getServer().getHelpMap().addTopic((HelpTopic)topic);
    }

    private void registerCommand(CommandFramework.Command command, String label, Method m, Object obj) {
        AbstractMap.SimpleEntry<Method, Object> entry = new AbstractMap.SimpleEntry<Method, Object>(m, obj);
        this.commandMap.put(label.toLowerCase(), entry);
        String cmdLabel = label.replace(".", ",").split(",")[0].toLowerCase();
        if (this.map.getCommand(cmdLabel) == null) {
            BukkitCommand cmd = new BukkitCommand(command.name(), cmdLabel, this.plugin, command.permission());
            this.knownCommands.put(cmdLabel, cmd);
        } else if (this.map.getCommand(cmdLabel) instanceof BukkitCommand) {
            BukkitCommand bukkitCommand = (BukkitCommand)this.map.getCommand(cmdLabel);
            bukkitCommand.setPermission(command.permission());
        }
        if (!command.description().equalsIgnoreCase("") && cmdLabel == label) {
            this.map.getCommand(cmdLabel).setDescription(command.description());
        }
        if (!command.usage().equalsIgnoreCase("") && cmdLabel == label) {
            this.map.getCommand(cmdLabel).setUsage(command.usage());
        }
    }

    private void registerCompleter(String label, Method m, Object obj) {
        BukkitCommand command;
        String cmdLabel = label.replace(".", ",").split(",")[0].toLowerCase();
        if (this.map.getCommand(cmdLabel) == null) {
            command = new BukkitCommand(cmdLabel, cmdLabel, this.plugin, "");
            this.knownCommands.put(cmdLabel, command);
        }
        if (this.map.getCommand(cmdLabel) instanceof BukkitCommand) {
            command = (BukkitCommand)this.map.getCommand(cmdLabel);
            if (command.getCompleter() == null) {
                command.setCompleter(new BukkitCompleter());
            }
            command.getCompleter().addCompleter(label, m, obj);
        } else if (this.map.getCommand(cmdLabel) instanceof PluginCommand) {
            try {
                command = (BukkitCommand) this.map.getCommand(cmdLabel);
                Field field = ((command)).getClass().getDeclaredField("completer");
                field.setAccessible(true);
                if (field.get(command) == null) {
                    BukkitCompleter completer = new BukkitCompleter();
                    completer.addCompleter(label, m, obj);
                    field.set(command, completer);
                } else if (field.get(command) instanceof BukkitCompleter) {
                    BukkitCompleter completer = (BukkitCompleter)field.get(command);
                    completer.addCompleter(label, m, obj);
                } else {
                    System.out.println("Unable to register tab completer " + m.getName() + ". A tab completer is already registered for that command!");
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void unregisterCommands(String ... commands) {
        try {
            Field f1 = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            f1.setAccessible(true);
            CommandMap commandMap = (CommandMap)f1.get(Bukkit.getServer());
            Field f2 = commandMap.getClass().getDeclaredField("knownCommands");
            f2.setAccessible(true);
            for (String command : commands) {
                if (!this.knownCommands.containsKey(command)) continue;
                this.knownCommands.remove(command);
                ArrayList<String> aliases = new ArrayList<String>();
                for (String key : this.knownCommands.keySet()) {
                    String substr;
                    if (!key.contains(":") || !(substr = key.substring(key.indexOf(":") + 1)).equalsIgnoreCase(command)) continue;
                    aliases.add(key);
                }
                for (String alias : aliases) {
                    this.knownCommands.remove(alias);
                }
            }
            Iterator<Map.Entry<String, org.bukkit.command.Command>> iterator = this.knownCommands.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, org.bukkit.command.Command> entry = iterator.next();
                if (!entry.getKey().contains(":")) continue;
                entry.getValue().unregister(commandMap);
                iterator.remove();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Class<?> getJarClass() {
        return this.plugin.getClass();
    }

    public class BukkitCompleter
            implements TabCompleter {
        private final Map<String, Map.Entry<Method, Object>> completers = new HashMap<String, Map.Entry<Method, Object>>();

        public void addCompleter(String label, Method m, Object obj) {
            this.completers.put(label, new AbstractMap.SimpleEntry<Method, Object>(m, obj));
        }

        public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
            for (int i = args.length; i >= 0; --i) {
                StringBuilder buffer = new StringBuilder();
                buffer.append(label.toLowerCase());
                for (int x = 0; x < i; ++x) {
                    if (args[x].equals("") || args[x].equals(" ")) continue;
                    buffer.append(".").append(args[x].toLowerCase());
                }
                String cmdLabel = buffer.toString();
                if (!this.completers.containsKey(cmdLabel)) continue;
                Map.Entry<Method, Object> entry = this.completers.get(cmdLabel);
                try {
                    return (List)entry.getKey().invoke(entry.getValue(), new BukkitCommandArgs(sender, label, args, cmdLabel.split("\\.").length - 1));
                }
                catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        public List<String> onTabComplete(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
            return null;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args, Location location) {
            return TabCompleter.super.onTabComplete(sender, command, alias, args, location);
        }
    }

    public class BukkitCommand
            extends org.bukkit.command.Command {
        private Plugin owningPlugin;
        private CommandExecutor executor;
        private BukkitCompleter completer;
        private String permission;

        public BukkitCommand(String fallbackPrefix, String label, Plugin owner, String permission) {
            super(label);
            this.executor = owner;
            this.owningPlugin = owner;
            this.usageMessage = "";
            this.permission = permission;
            if (CommonPlugin.getInstance().getServerType() == ServerType.RANKUP) {
                try {
                    Class<?> timingsClass = Class.forName("co.aikar.timings.Timings");
                    Method method = timingsClass.getDeclaredMethod("ofSafe", String.class);
                    method.setAccessible(true);
                    Field field = Command.class.getDeclaredField("timings");
                    field.setAccessible(true);
                    field.set(this, method.invoke(null, "** Command: " + this.getName()));
                }
                catch (ClassNotFoundException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
                catch (NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
                catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
                catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            } else {
              //  this.timings = new CustomTimingsHandler("** Command: " + this.getName());
            }
        }

        public boolean execute(CommandSender sender, String commandLabel, String[] args) {
            boolean success = false;
            if (!this.owningPlugin.isEnabled()) {
                return false;
            }
            if (!this.testPermission(sender)) {
                return true;
            }
            try {
                success = BukkitCommandFramework.this.handleCommand(sender, commandLabel, (Command) this, args);
            }
            catch (Throwable ex) {
                throw new CommandException("Unhandled exception executing command '" + commandLabel + "' in plugin " + this.owningPlugin.getDescription().getFullName(), ex);
            }
            if (!success && this.usageMessage.length() > 0) {
                for (String line : this.usageMessage.replace("<command>", commandLabel).split("\n")) {
                    sender.sendMessage(line);
                }
            }
            return success;
        }

        public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws CommandException, IllegalArgumentException {
            Validate.notNull(sender, (String)"Sender cannot be null");
            Validate.notNull(args, (String)"Arguments cannot be null");
            Validate.notNull(alias, (String)"Alias cannot be null");
            List completions = null;
            try {
                if (this.completer != null) {
                    completions = this.completer.onTabComplete(sender, this, alias, args);
                }
                if (completions == null && this.executor instanceof TabCompleter) {
                    completions = ((TabCompleter)this.executor).onTabComplete(sender, this, alias, args);
                }
            }
            catch (Throwable ex) {
                StringBuilder message = new StringBuilder();
                message.append("Unhandled exception during tab completion for command '/").append(alias).append(' ');
                for (String arg : args) {
                    message.append(arg).append(' ');
                }
                message.deleteCharAt(message.length() - 1).append("' in plugin ").append(this.owningPlugin.getDescription().getFullName());
                throw new CommandException(message.toString(), ex);
            }
            if (completions == null) {
                return super.tabComplete(sender, alias, args);
            }
            return completions;
        }

        public boolean testPermission(CommandSender target) {
            if (this.testPermissionSilent(target)) {
                return true;
            }
            target.sendMessage("\u00a7%no-permission%\u00a7");
            return false;
        }

        public boolean testPermissionSilent(CommandSender target) {
            if (this.getPermission().isEmpty()) {
                return true;
            }
            if (target instanceof Player) {
                return target.hasPermission(this.getPermission());
            }
            return true;
        }

        public void setCompleter(BukkitCompleter completer) {
            this.completer = completer;
        }

        public BukkitCompleter getCompleter() {
            return this.completer;
        }

        public String getPermission() {
            return this.permission;
        }
    }
}
