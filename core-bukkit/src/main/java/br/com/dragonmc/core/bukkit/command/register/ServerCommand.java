/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  net.md_5.bungee.api.ChatColor
 *  net.minecraft.server.v1_8_R3.MinecraftServer
 *  org.bukkit.Bukkit
 *  org.bukkit.GameMode
 *  org.bukkit.Location
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.World
 *  org.bukkit.WorldCreator
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 */
package br.com.dragonmc.core.bukkit.command.register;

import com.google.common.base.Joiner;
import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import br.com.dragonmc.core.common.CommonConst;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.bukkit.BukkitCommon;
import br.com.dragonmc.core.bukkit.member.BukkitMember;
import br.com.dragonmc.core.bukkit.menu.profile.StatisticsInventory;
import br.com.dragonmc.core.bukkit.utils.ProtocolVersion;
import br.com.dragonmc.core.common.command.CommandArgs;
import br.com.dragonmc.core.common.command.CommandClass;
import br.com.dragonmc.core.common.command.CommandFramework;
import br.com.dragonmc.core.common.command.CommandSender;
import br.com.dragonmc.core.common.language.Language;
import br.com.dragonmc.core.common.server.ServerType;
import br.com.dragonmc.core.common.server.loadbalancer.server.ProxiedServer;
import br.com.dragonmc.core.common.utils.DateUtils;
import br.com.dragonmc.core.common.utils.configuration.Configuration;
import br.com.dragonmc.core.common.utils.string.StringFormat;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ServerCommand
implements CommandClass {
    @CommandFramework.Command(name="servermanager", aliases={"ss", "smanager"}, permission="command.server")
    public void servermanagerCommand(CommandArgs cmdArgs) {
        CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();
        if (args.length == 0) {
            sender.sendMessage("\u00a7%command-server-usage%\u00a7");
            return;
        }
        switch (args[0].toLowerCase()) {
            case "reload-config": {
                try {
                    CommonPlugin.getInstance().loadConfig();
                    sender.sendMessage("\u00a7%command-server-reload-config-successfully%\u00a7");
                }
                catch (Exception ex) {
                    sender.sendMessage("\u00a7%command-server-reload-config-error%\u00a7");
                    sender.sendMessage("\u00a7c" + ex.getLocalizedMessage());
                    ex.printStackTrace();
                }
                break;
            }
            case "type": {
                if (args.length == 1) {
                    sender.sendMessage("\u00a7aInsira o tipo do servidor.");
                    return;
                }
                ServerType serverType = null;
                try {
                    serverType = ServerType.valueOf(args[1].toUpperCase());
                }
                catch (Exception ex) {
                    sender.sendMessage("\u00a7cO tipo do servidor n\u00e3o foi encontrado.");
                    return;
                }
                CommonPlugin.getInstance().setServerType(serverType);
                sender.sendMessage("\u00a7aO tipo do servidor foi alterado para \u00a7f" + serverType.name() + "\u00a7a.");
                break;
            }
            case "serverid": {
                if (args.length == 1) {
                    sender.sendMessage("\u00a7aInsira o nome do servidor.");
                    return;
                }
                String serverId = args[1];
                CommonPlugin.getInstance().setServerId(serverId);
                sender.sendMessage("\u00a7aO ID do servidor foi alterado para \u00a7f" + serverId + "\u00a7a.");
                break;
            }
            case "start": {
                CommonPlugin.getInstance().getServerData().startServer(Bukkit.getMaxPlayers());
                CommonPlugin.getInstance().getServerData().updateStatus();
                Bukkit.getOnlinePlayers().forEach(player -> CommonPlugin.getInstance().getServerData().joinPlayer(player.getUniqueId(), Bukkit.getMaxPlayers()));
                sender.sendMessage("\u00a7aO servidor foi iniciado com sucesso.");
                break;
            }
            case "stop": {
                Bukkit.getOnlinePlayers().forEach(player -> CommonPlugin.getInstance().getServerData().leavePlayer(player.getUniqueId(), Bukkit.getMaxPlayers()));
                CommonPlugin.getInstance().getServerData().stopServer();
                sender.sendMessage("\u00a7aO servidor foi parado com sucesso.");
                break;
            }
            case "save-config": {
                try {
                    CommonPlugin.getInstance().saveConfig();
                    sender.sendMessage("\u00a7%command-server-save-config-successfully%\u00a7");
                }
                catch (Exception ex) {
                    sender.sendMessage("\u00a7%command-server-save-config-error%\u00a7");
                    sender.sendMessage("\u00a7c" + ex.getLocalizedMessage());
                    ex.printStackTrace();
                }
                break;
            }
            case "debug": {
                for (Map.Entry<String, ProxiedServer> entry : BukkitCommon.getInstance().getServerManager().getActiveServers().entrySet()) {
                    sender.sendMessage("  \u00a7f" + entry.getKey() + "\u00a77: " + CommonConst.GSON.toJson((Object)entry.getValue()));
                }
                break;
            }
            default: {
                sender.sendMessage("\u00a7%command-server-usage%\u00a7");
            }
        }
    }

    @CommandFramework.Command(name="stats", aliases={"status", "estatisticas"}, console=false)
    public void statsCommand(CommandArgs cmdArgs) {
        new StatisticsInventory(cmdArgs.getSenderAsMember(BukkitMember.class).getPlayer(), null);
    }

    @CommandFramework.Command(name="config", permission="command.config")
    public void configCommand(CommandArgs cmdArgs) {
        CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();
        if (args.length == 0) {
            sender.sendMessage("\u00a7%command-config-usage%\u00a7");
            return;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            sender.sendMessage("  \u00a7aConfig list:");
            for (String config : CommonPlugin.getInstance().getConfigurationManager().getConfigs()) {
                sender.sendMessage("    \u00a7f- \u00a77" + config);
            }
            return;
        }
        String configName = args[0];
        Configuration configuration = CommonPlugin.getInstance().getConfigurationManager().getConfigByName(configName);
        if (configuration == null) {
            sender.sendMessage(sender.getLanguage().t("command-config-configuration-not-found", "%name%", configName));
            return;
        }
        switch (args[1].toLowerCase()) {
            case "save": {
                try {
                    configuration.saveConfig();
                    sender.sendMessage(sender.getLanguage().t("command-config-configuration-saved", "%name%", configName));
                }
                catch (Exception ex) {
                    sender.sendMessage("\u00a7%command-config-could-not-save%\u00a7");
                    ex.printStackTrace();
                }
                return;
            }
            case "reload": 
            case "load": {
                try {
                    configuration.loadConfig();
                    sender.sendMessage(sender.getLanguage().t("command-config-configuration-loaded", "%name%", configName));
                }
                catch (Exception ex) {
                    sender.sendMessage("\u00a7%command-config-could-not-load%\u00a7");
                    ex.printStackTrace();
                }
                return;
            }
        }
        sender.sendMessage("\u00a7%command-config-usage%\u00a7");
    }

    @CommandFramework.Command(name="gamemode", aliases={"gm"}, permission="command.gamemode")
    public void gamemodeCommand(CommandArgs cmdArgs) {
        Player target;
        CommandSender sender = cmdArgs.getSender();
        Language language = sender.getLanguage();
        String[] args = cmdArgs.getArgs();
        if (args.length == 0) {
            sender.sendMessage(language.t("command-gamemode-usage", "%label%", cmdArgs.getLabel()));
            return;
        }
        GameMode gameMode = null;
        OptionalInt optionalInt = StringFormat.parseInt(args[0]);
        if (optionalInt.isPresent()) {
            gameMode = GameMode.getByValue((int)optionalInt.getAsInt());
        } else {
            try {
                gameMode = GameMode.valueOf((String)args[0].toUpperCase());
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if (gameMode == null) {
            sender.sendMessage(language.t("command-gamemode-not-found", "%gamemode%", args[0]));
            return;
        }
        Player player = target = args.length == 1 && sender.isPlayer() ? cmdArgs.getSenderAsMember(BukkitMember.class).getPlayer() : Bukkit.getPlayer((String)args[1]);
        if (target == null) {
            sender.sendMessage(language.t("player-not-found", "%player%", args[1]));
            return;
        }
        target.setGameMode(gameMode);
        if (target.getUniqueId().equals(sender.getUniqueId())) {
            sender.sendMessage(language.t("command-gamemode-your-gamemode-changed", "%gamemode%", StringFormat.formatString(gameMode.name())));
        } else {
            sender.sendMessage(language.t("command-gamemode-target-gamemode-changed", "%gamemode%", StringFormat.formatString(gameMode.name()), "%target%", target.getName()));
        }
    }

    @CommandFramework.Command(name="clear", permission="command.clear")
    public void clearCommand(CommandArgs cmdArgs) {
        CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();
        if (args.length == 0) {
            if (cmdArgs.isPlayer()) {
                Player player = cmdArgs.getSenderAsMember(BukkitMember.class).getPlayer();
                player.getInventory().clear();
                player.getInventory().setArmorContents(new ItemStack[4]);
                player.getActivePotionEffects().clear();
                sender.sendMessage("\u00a7%command-clear-cleared-inventory%\u00a7");
            } else {
                sender.sendMessage("\u00a7%command-only-for-player%\u00a7");
            }
        } else {
            Player player = Bukkit.getPlayer((String)args[0]);
            if (player == null) {
                sender.sendMessage(sender.getLanguage().t("player-not-found", "%player%", args[0]));
                return;
            }
            player.getInventory().clear();
            player.getInventory().setArmorContents(new ItemStack[4]);
            player.getActivePotionEffects().clear();
            sender.sendMessage(sender.getLanguage().t("command-clear-cleared-player-inventory", "%player%", player.getName()));
        }
    }

    @CommandFramework.Command(name="tpworld", aliases={"tpw"}, permission="command.teleport")
    public void teleportworldCommand(CommandArgs cmdArgs) {
        Player player = cmdArgs.getSenderAsMember(BukkitMember.class).getPlayer();
        String[] args = cmdArgs.getArgs();
        World world = Bukkit.getWorld((String)args[0]);
        if (world == null) {
            world = WorldCreator.name((String)args[0]).createWorld();
        }
        player.teleport(new Location(Bukkit.getWorld((String)args[0]), 0.0, 0.0, 0.0));
    }

    @CommandFramework.Command(name="teleport", aliases={"tp"}, permission="command.teleport")
    public void teleportCommand(CommandArgs cmdArgs) {
        CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();
        switch (args.length) {
            case 1: {
                Player target = Bukkit.getPlayer((String)args[0]);
                if (target == null) {
                    sender.sendMessage(sender.getLanguage().t("player-not-found", "%player%", args[0]));
                    return;
                }
                if (sender.isPlayer()) {
                    cmdArgs.getSenderAsMember(BukkitMember.class).getPlayer().teleport((Entity)target);
                    sender.sendMessage(sender.getLanguage().t("command-teleport-teleported-to-target", "%target%", target.getName()));
                    break;
                }
                sender.sendMessage("\u00a7%command-only-for-console%\u00a7");
                break;
            }
            case 2: {
                if (args[0].equalsIgnoreCase("location")) {
                    if (cmdArgs.isPlayer()) {
                        Player player = cmdArgs.getSenderAsMember(BukkitMember.class).getPlayer();
                        String locationName = args[1];
                        if (BukkitCommon.getInstance().getLocationManager().hasLocation(locationName)) {
                            Location location = BukkitCommon.getInstance().getLocationManager().getLocation(locationName);
                            player.teleport(location);
                            player.sendMessage(sender.getLanguage().t("command-teleport-teleported-to-locationname", "%location%", locationName));
                        } else {
                            sender.sendMessage(sender.getLanguage().t("command-teleport-location-not-found", "%location%", locationName));
                        }
                    } else {
                        sender.sendMessage("\u00a7%command-only-for-console%\u00a7");
                    }
                    return;
                }
                Player player = Bukkit.getPlayer((String)args[0]);
                if (player == null) {
                    sender.sendMessage(sender.getLanguage().t("player-not-found", "%player%", args[0]));
                    return;
                }
                Player target = Bukkit.getPlayer((String)args[1]);
                if (target == null) {
                    sender.sendMessage(sender.getLanguage().t("player-not-found", "%player%", args[1]));
                    return;
                }
                player.teleport((Entity)target);
                sender.sendMessage(sender.getLanguage().t("command-teleport-teleported-player-to-target", "%player%", player.getName(), "%target%", target.getName()));
                break;
            }
            case 3: {
                if (sender.isPlayer()) {
                    double z;
                    double y;
                    double x;
                    Player player = cmdArgs.getSenderAsMember(BukkitMember.class).getPlayer();
                    OptionalDouble optionalX = StringFormat.parseDouble(args[0]);
                    OptionalDouble optionalY = StringFormat.parseDouble(args[1]);
                    OptionalDouble optionalZ = StringFormat.parseDouble(args[2]);
                    if (args[0].equals("~")) {
                        x = player.getLocation().getX();
                    } else if (optionalX.isPresent()) {
                        x = optionalX.getAsDouble();
                    } else {
                        sender.sendMessage(sender.getLanguage().t("invalid-format-double", "%value%", args[0]));
                        return;
                    }
                    if (args[1].equals("~")) {
                        y = player.getLocation().getY();
                    } else if (optionalY.isPresent()) {
                        y = optionalY.getAsDouble();
                    } else {
                        sender.sendMessage(sender.getLanguage().t("invalid-format-double", "%value%", args[1]));
                        return;
                    }
                    if (args[2].equals("~")) {
                        z = player.getLocation().getZ();
                    } else if (optionalZ.isPresent()) {
                        z = optionalZ.getAsDouble();
                    } else {
                        sender.sendMessage(sender.getLanguage().t("invalid-format-double", "%value%", args[2]));
                        return;
                    }
                    Location location = new Location(player.getWorld(), x, y, z);
                    if (!location.getChunk().isLoaded()) {
                        location.getChunk().load();
                    }
                    DecimalFormat numberFormat = new DecimalFormat("#.##");
                    player.setFallDistance(-1.0f);
                    player.teleport(location);
                    sender.sendMessage(sender.getLanguage().t("command-teleport-teleported-to-location", "%x%", numberFormat.format(x), "%y%", numberFormat.format(y), "%z%", numberFormat.format(z)));
                    break;
                }
                sender.sendMessage("\u00a7%command-only-for-console%\u00a7");
                break;
            }
            default: {
                sender.sendMessage(sender.getLanguage().t("command-teleport-usage", "%label%", cmdArgs.getLabel()));
            }
        }
    }

    @CommandFramework.Command(name="setlocation", permission="command.location", console=false)
    public void setlocationCommand(CommandArgs cmdArgs) {
        String[] args = cmdArgs.getArgs();
        BukkitMember member = (BukkitMember)cmdArgs.getSender();
        if (args.length == 0) {
            member.sendMessage("\u00a7aUse /" + cmdArgs.getLabel() + " <locationName> para salvar a localiza\u00e7\u00e3o.");
            return;
        }
        String string = Joiner.on((char)' ').join((Object[])Arrays.copyOfRange(args, 0, args.length)).toLowerCase();
        member.sendMessage("\u00a7aLocaliza\u00e7\u00e3o " + args[0] + " com sucesso!");
        BukkitCommon.getInstance().getLocationManager().saveAndLoadLocation(string.replace(' ', '_'), member.getPlayer().getLocation());
    }

    @CommandFramework.Command(name="stop", aliases={"fechar"}, permission="command.stop")
    public void stopCommand(CommandArgs cmdArgs) {
        CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();
        int time = 0;
        if (args.length >= 1) {
            OptionalInt optional = StringFormat.parseInt(args[0]);
            if (optional.isPresent()) {
                time = optional.getAsInt();
            } else {
                sender.sendMessage(sender.getLanguage().t("invalid-format-integer", "%value%", args[0]));
                return;
            }
        }
        final String reason = args.length >= 2 ? Joiner.on((char)' ').join((Object[])Arrays.copyOfRange(args, 2, args.length)) : "Sem motivo";
        final int t = time;
        new BukkitRunnable(){
            int totalTime;
            {
                this.totalTime = t;
            }

            public void run() {
                if (this.totalTime <= -2) {
                    Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer("\u00a7cServer closed."));
                    Bukkit.shutdown();
                    return;
                }
                if (this.totalTime <= 0) {
                    if (this.totalTime == 0) {
                        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(reason.equals("Sem motivo") ? Language.getLanguage(player.getUniqueId()).t("server-was-closed", new String[0]) : Language.getLanguage(player.getUniqueId()).t("server-was-closed-reason", "%reason%", reason)));
                    }
                    if (Bukkit.getOnlinePlayers().isEmpty()) {
                        Bukkit.shutdown();
                    } else {
                        Bukkit.getOnlinePlayers().forEach(player -> BukkitCommon.getInstance().sendPlayerToServer((Player)player, true, CommonPlugin.getInstance().getServerType().getServerLobby(), ServerType.LOBBY));
                    }
                }
                --this.totalTime;
            }
        }.runTaskTimer((Plugin)BukkitCommon.getInstance(), 20L, 20L);
    }

    @CommandFramework.Command(name="tps", aliases={"ticks"}, permission="command.tps")
    public void tpsCommand(CommandArgs cmdArgs) {
        CommandSender sender = cmdArgs.getSender();
        if (cmdArgs.getArgs().length == 0) {
            long usedMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 2L / 0x100000L;
            long allocatedMemory = Runtime.getRuntime().totalMemory() / 0x100000L;
            sender.sendMessage(" \u00a7aServidor " + CommonPlugin.getInstance().getServerId() + ":");
            sender.sendMessage("    \u00a7fPlayers: \u00a77" + Bukkit.getOnlinePlayers().size() + " jogadores");
            sender.sendMessage("    \u00a7fM\u00e1ximo de players: \u00a77" + Bukkit.getMaxPlayers() + " jogadores");
            sender.sendMessage("    \u00a7fMem\u00f3ria: \u00a77" + usedMemory + "/" + allocatedMemory + " MB");
            sender.sendMessage("    \u00a7fLigado h\u00e1: \u00a77" + DateUtils.formatDifference(sender.getLanguage(), (System.currentTimeMillis() - ManagementFactory.getRuntimeMXBean().getStartTime()) / 1000L));
            sender.sendMessage("    \u00a7fTPS: ");
            sender.sendMessage("      \u00a7f1m: \u00a77" + this.format(MinecraftServer.getServer().recentTps[0]));
            sender.sendMessage("      \u00a7f5m: \u00a77" + this.format(MinecraftServer.getServer().recentTps[1]));
            sender.sendMessage("      \u00a7f15m: \u00a77" + this.format(MinecraftServer.getServer().recentTps[2]));
            int ping = 0;
            HashMap<ProtocolVersion, Integer> map = new HashMap<ProtocolVersion, Integer>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                ping += ProtocolVersion.getPing(player);
                ProtocolVersion version = ProtocolVersion.getProtocolVersion(player);
                map.putIfAbsent(version, 0);
                map.put(version, (Integer)map.get((Object)version) + 1);
            }
            sender.sendMessage("    \u00a7fPing m\u00e9dio: \u00a77" + (ping /= Math.max(Bukkit.getOnlinePlayers().size(), 1)) + "ms");
            if (!Bukkit.getOnlinePlayers().isEmpty()) {
                sender.sendMessage("    \u00a7fVers\u00e3o: \u00a77");
                for (Map.Entry entry : map.entrySet()) {
                    sender.sendMessage("      \u00a7f- " + ((ProtocolVersion)((Object)entry.getKey())).name().replace("MINECRAFT_", "").replace("_", ".") + ": \u00a77" + entry.getValue() + " jogadores");
                }
            }
            return;
        }
        if (cmdArgs.getArgs()[0].equalsIgnoreCase("gc")) {
            Runtime.getRuntime().gc();
            sender.sendMessage(" \u00a7a\u00bb \u00a7fVoc\u00ea passou o GarbargeCollector no servidor.");
        } else {
            World world = Bukkit.getWorld((String)cmdArgs.getArgs()[0]);
            if (world == null) {
                sender.sendMessage(" \u00a7c\u00bb \u00a7fO mundo " + cmdArgs.getArgs()[0] + " n\u00e3o existe.");
            } else {
                sender.sendMessage(" \u00a7aMundo " + world.getName());
                sender.sendMessage("    \u00a7fEntidades: \u00a77" + world.getEntities().size());
                sender.sendMessage("    \u00a7fLoaded chunks: \u00a77" + world.getLoadedChunks().length);
            }
        }
    }

    @CommandFramework.Command(name="memoryinfo", permission="command.tps")
    public void memoryinfoCommand(CommandArgs cmdArgs) {
        long usedMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 2L / 0x100000L;
        long allocatedMemory = Runtime.getRuntime().totalMemory() / 0x100000L;
        cmdArgs.getSender().sendMessage("  \u00a7aMemory Info:");
        cmdArgs.getSender().sendMessage("    \u00a7fMem\u00f3ria usada: \u00a77" + usedMemory + "MB (" + usedMemory * 100L / allocatedMemory + "%)");
        cmdArgs.getSender().sendMessage("    \u00a7fMem\u00f3ria livre: \u00a77" + (allocatedMemory - usedMemory) + "MB (" + (allocatedMemory - usedMemory) * 100L / allocatedMemory + "%)");
        cmdArgs.getSender().sendMessage("    \u00a7fMem\u00f3ria m\u00e1xima: \u00a77" + allocatedMemory + "MB");
        cmdArgs.getSender().sendMessage("    \u00a7fCPU: \u00a77" + CommonConst.DECIMAL_FORMAT.format(CommonConst.getCpuUse()) + "%");
    }

    private String format(double tps) {
        return (tps > 18.0 ? ChatColor.GREEN : (tps > 16.0 ? ChatColor.YELLOW : ChatColor.RED)) + (tps > 20.0 ? "*" : "") + Math.min((double)Math.round(tps * 100.0) / 100.0, 20.0);
    }

    @CommandFramework.Completer(name="party")
    public List<String> partyCompleter(CommandArgs cmdArgs) {
        ArrayList<String> returnList;
        block5: {
            returnList = new ArrayList<String>();
            if (cmdArgs.getArgs().length != 1) break block5;
            List<String> arguments = Arrays.asList("criar", "convidar", "aceitar", "expulsar", "sair", "chat");
            if (cmdArgs.getArgs()[0].isEmpty()) {
                for (String argument : arguments) {
                    returnList.add(argument);
                }
            } else {
                for (String argument : arguments) {
                    if (!argument.toLowerCase().startsWith(cmdArgs.getArgs()[0].toLowerCase())) continue;
                    returnList.add(argument);
                }
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!player.getName().toLowerCase().startsWith(cmdArgs.getArgs()[0].toLowerCase())) continue;
                    returnList.add(player.getName());
                }
            }
        }
        return returnList;
    }

    @CommandFramework.Completer(name="servermanager", aliases={"ss", "smanager"})
    public List<String> servermanagerCompleter(CommandArgs cmdArgs) {
        ArrayList<String> returnList;
        block4: {
            returnList = new ArrayList<String>();
            if (cmdArgs.getArgs().length != 1) break block4;
            List<String> arguments = Arrays.asList("reload-config", "save-config");
            if (cmdArgs.getArgs()[0].isEmpty()) {
                for (String argument : arguments) {
                    returnList.add(argument);
                }
            } else {
                for (String argument : arguments) {
                    if (!argument.toLowerCase().startsWith(cmdArgs.getArgs()[0].toLowerCase())) continue;
                    returnList.add(argument);
                }
            }
        }
        return returnList;
    }

    @CommandFramework.Completer(name="setlocation")
    public List<String> serverCompleter(CommandArgs cmdArgs) {
        if (cmdArgs.isPlayer() && cmdArgs.getArgs().length == 1) {
            ArrayList<String> arg = new ArrayList<String>();
            if (cmdArgs.getArgs()[0].isEmpty()) {
                for (String tag : BukkitCommon.getInstance().getLocationManager().getLocations()) {
                    arg.add(tag);
                }
            } else {
                for (String tag : BukkitCommon.getInstance().getLocationManager().getLocations()) {
                    if (!tag.startsWith(cmdArgs.getArgs()[0].toLowerCase())) continue;
                    arg.add(tag);
                }
            }
            return arg;
        }
        return new ArrayList<String>();
    }

    @CommandFramework.Completer(name="gamemode", aliases={"gm"})
    public List<String> gamemodeCompleter(CommandArgs cmdArgs) {
        ArrayList<String> returnList = new ArrayList<String>();
        if (cmdArgs.getArgs().length == 1) {
            returnList.addAll(Arrays.asList(GameMode.values()).stream().filter(gameMode -> gameMode.name().toLowerCase().startsWith(cmdArgs.getArgs()[0].toLowerCase())).map(Enum::name).collect(Collectors.toList()));
        } else if (cmdArgs.getArgs().length == 2) {
            returnList.addAll(Bukkit.getOnlinePlayers().stream().filter(player -> player.getName().toLowerCase().startsWith(cmdArgs.getArgs()[1].toLowerCase())).map(OfflinePlayer::getName).collect(Collectors.toList()));
        }
        return returnList;
    }
}

