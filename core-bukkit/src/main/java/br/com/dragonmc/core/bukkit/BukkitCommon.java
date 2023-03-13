/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.google.common.io.ByteArrayDataInput
 *  com.google.common.io.ByteArrayDataOutput
 *  com.google.common.io.ByteStreams
 *  net.minecraft.server.v1_8_R3.DedicatedPlayerList
 *  net.minecraft.server.v1_8_R3.PlayerList
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.craftbukkit.v1_8_R3.CraftServer
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.HandlerList
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.AsyncPlayerPreLoginEvent
 *  org.bukkit.event.player.AsyncPlayerPreLoginEvent$Result
 *  org.bukkit.metadata.FixedMetadataValue
 *  org.bukkit.metadata.MetadataValue
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.scheduler.BukkitRunnable
 */
package br.com.dragonmc.core.bukkit;

import br.com.dragonmc.core.bukkit.command.BukkitCommandFramework;
import br.com.dragonmc.core.bukkit.event.UpdateEvent;
import br.com.dragonmc.core.bukkit.listener.member.MemberListener;
import br.com.dragonmc.core.bukkit.listener.member.TagListener;
import br.com.dragonmc.core.bukkit.member.party.BukkitParty;
import br.com.dragonmc.core.bukkit.utils.character.Character;
import br.com.dragonmc.core.bukkit.utils.character.handler.ActionHandler;
import br.com.dragonmc.core.bukkit.utils.hologram.Hologram;
import br.com.dragonmc.core.bukkit.utils.hologram.impl.SimpleHologram;
import br.com.dragonmc.core.bukkit.utils.permission.PermissionManager;
import com.google.common.base.Joiner;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.common.backend.redis.RedisConnection;
import br.com.dragonmc.core.bukkit.anticheat.StormCore;
import br.com.dragonmc.core.bukkit.listener.CharacterListener;
import br.com.dragonmc.core.bukkit.listener.ChatListener;
import br.com.dragonmc.core.bukkit.listener.CombatListener;
import br.com.dragonmc.core.bukkit.listener.CommandListener;
import br.com.dragonmc.core.bukkit.listener.HologramListener;
import br.com.dragonmc.core.bukkit.listener.MenuListener;
import br.com.dragonmc.core.bukkit.listener.MoveListener;
import br.com.dragonmc.core.bukkit.listener.PermissionListener;
import br.com.dragonmc.core.bukkit.listener.PlayerListener;
import br.com.dragonmc.core.bukkit.listener.VanishListener;
import br.com.dragonmc.core.bukkit.listener.WorldListener;
import br.com.dragonmc.core.bukkit.manager.BlockManager;
import br.com.dragonmc.core.bukkit.manager.ChatManager;
import br.com.dragonmc.core.bukkit.manager.CombatlogManager;
import br.com.dragonmc.core.bukkit.manager.CooldownManager;
import br.com.dragonmc.core.bukkit.manager.HologramManager;
import br.com.dragonmc.core.bukkit.manager.LocationManager;
import br.com.dragonmc.core.bukkit.manager.VanishManager;
import br.com.dragonmc.core.bukkit.networking.BukkitPubSubHandler;
import br.com.dragonmc.core.bukkit.protocol.impl.LimiterInjector;
import br.com.dragonmc.core.bukkit.protocol.impl.TranslationInjector;
import br.com.dragonmc.core.bukkit.utils.player.PlayerHelper;
import br.com.dragonmc.core.common.member.status.StatusType;
import br.com.dragonmc.core.common.server.ServerManager;
import br.com.dragonmc.core.common.server.ServerType;
import net.minecraft.server.v1_8_R3.DedicatedPlayerList;
import net.minecraft.server.v1_8_R3.PlayerList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class BukkitCommon
extends JavaPlugin {
    private static BukkitCommon instance;
    private CommonPlugin plugin;
    private BlockManager blockManager;
    private ChatManager chatManager;
    private CombatlogManager combatlogManager;
    private CooldownManager cooldownManager;
    private HologramManager hologramManager;
    private LocationManager locationManager;
    private VanishManager vanishManager;
    private PermissionManager permissionManager;
    private ServerManager serverManager;
    private StormCore stormCore;
    private boolean serverLog;
    private boolean tagControl = true;
    private boolean removePlayerDat = true;
    private ChatState chatState = ChatState.ENABLED;
    private boolean blockCommands = true;
    private boolean permissionControl = true;
    private boolean registerCommands = true;
    private Set<StatusType> preloadedStatus = new HashSet<StatusType>();

    public void onLoad() {
        instance = this;
        this.plugin = new CommonPlugin(new BukkitPlatform());
        this.stormCore = new StormCore((Plugin)this);
        this.stormCore.onLoad();
        this.saveDefaultConfig();
        super.onLoad();
    }

    public void onEnable() {
        try {
            Listener listener = new Listener(){

                @EventHandler(priority=EventPriority.LOWEST)
                public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "\u00a7cO servidor est\u00e1 carregando.");
                }
            };
            Bukkit.getPluginManager().registerEvents(listener, (Plugin)this);
            this.loadManagers();
            this.loadListeners();
            this.loadPacketInjectors();
            this.stormCore.onEnable();
            this.getServer().getScheduler().runTaskTimer((Plugin)this, (Runnable)new UpdateScheduler(), 1L, 1L);
            this.getServer().getScheduler().runTaskAsynchronously((Plugin)BukkitCommon.getInstance(), (Runnable)new RedisConnection.PubSubListener(this.plugin.getRedisConnection(), new BukkitPubSubHandler(), "member_field", "clan_field", "server_info", "server_packet", "server_members"));
            new BukkitRunnable(){

                public void run() {
                    if (BukkitCommon.this.isBlockCommands()) {
                        BukkitCommandFramework.INSTANCE.unregisterCommands("icanhasbukkit", "plugins", "pl", "ver", "version", "?", "about", "help", "ban", "ban-ip", "banlist", "kick", "deop", "teleport", "gamemode", "op", "list", "me", "say", "scoreboard", "seed", "spawnpoint", "spreadplayers", "summon", "tell", "tellraw", "testfor", "testforblocks", "tp", "weather", "reload", "rl", "worldborder", "achievement", "blockdata", "clone", "debug", "defaultgamemode", "entitydata", "execute", "fill", "pardon", "pardon-ip", "replaceitem", "setidletimeout", "stats", "testforblock", "title", "trigger", "viaver", "ps", "holograms", "hd", "holo", "hologram", "restart", "filter", "packetlog", "?", "tps", "viaversion", "vvbukkit", "stop");
                    }
                    if (BukkitCommon.this.isRegisterCommands()) {
                        BukkitCommandFramework.INSTANCE.loadCommands("br.com.dragonmc");
                    }
                }
            }.runTaskLater((Plugin)this, 7L);
            this.loadServerInfo();
            this.loadBungeeConfig();
            HandlerList.unregisterAll((Listener)listener);
            super.onEnable();
        }
        catch (Exception ex) {
            Bukkit.shutdown();
            ex.printStackTrace();
        }
    }

    public void onDisable() {
        this.plugin.getServerData().stopServer();
        super.onDisable();
    }

    public void addStatus(StatusType statusType) {
        this.preloadedStatus.add(statusType);
    }

    public Hologram createCharacter(Location location, String playerName, ActionHandler interact) {
        Character character = new Character(playerName, location, interact);
        SimpleHologram hologram = new SimpleHologram("", location);
        this.hologramManager.registerHologram(hologram);
        hologram.spawn();
        Bukkit.getOnlinePlayers().forEach(player -> character.show((Player)player));
        return hologram;
    }

    public void loadServerInfo() {
        ServerType serverType = ServerType.valueOf(this.getConfig().getString("serverType", ServerType.BUNGEECORD.name()).toUpperCase());
        String serverId = this.getConfig().getString("serverId").toLowerCase();
        boolean joinEnabled = this.getConfig().getBoolean("joinEnabled", true);
        if (serverType.name().contains("LOBBY")) {
            this.setServerLog(true);
        }
        this.plugin.setServerAddress(Bukkit.getIp() + ":" + Bukkit.getPort());
        this.plugin.setServerType(serverType);
        this.plugin.setServerId(serverId);
        this.plugin.setJoinEnabled(joinEnabled);
        this.plugin.debug("The server id is " + serverId);
        this.plugin.debug("The server type is " + (Object)((Object)serverType));
        this.plugin.getServerData().startServer(Bukkit.getMaxPlayers());
        if (this.isServerLog()) {
            this.plugin.loadServers(this.serverManager);
        }
        this.plugin.debug("The server has been started!");
        this.plugin.setPartyClass(BukkitParty.class);
        this.plugin.getReportManager().loadReports();
    }

    public void loadManagers() {
        this.blockManager = new BlockManager();
        this.chatManager = new ChatManager();
        this.combatlogManager = new CombatlogManager();
        this.cooldownManager = new CooldownManager();
        this.hologramManager = new HologramManager();
        this.locationManager = new LocationManager();
        if (this.isPermissionControl()) {
            this.permissionManager = new PermissionManager(this);
        }
        this.serverManager = new ServerManager();
        this.vanishManager = new VanishManager();
    }

    public void loadBungeeConfig() {
        this.getServer().getMessenger().registerOutgoingPluginChannel((Plugin)this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel((Plugin)this, "BungeeCord", (channel, player, message) -> {
            ByteArrayDataInput in = ByteStreams.newDataInput((byte[])message);
            String subchannel = in.readUTF();
            if (subchannel.equalsIgnoreCase("BungeeTeleport")) {
                String uniqueId = in.readUTF();
                Player p = getInstance().getServer().getPlayer(UUID.fromString(uniqueId));
                if (p != null) {
                    player.chat("/tp " + p.getName());
                    this.getVanishManager().setPlayerInAdmin(player);
                }
            }
        });
    }

    public void loadPacketInjectors() {
        new LimiterInjector().inject((Plugin)this);
        new TranslationInjector().inject((Plugin)this);
    }

    public void loadListeners() {
        Bukkit.getPluginManager().registerEvents((Listener)new MemberListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new TagListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new CharacterListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new ChatListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new CombatListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new CommandListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new HologramListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new MenuListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new MoveListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new PlayerListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new VanishListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new WorldListener(), (Plugin)this);
        if (this.isPermissionControl()) {
            Bukkit.getPluginManager().registerEvents((Listener)new PermissionListener(), (Plugin)this);
        }
    }

    public void setMaxPlayers(int maxPlayers) {
        try {
            DedicatedPlayerList playerList = ((CraftServer)this.getServer()).getHandle();
            Field fieldMaxPlayers = PlayerList.class.getDeclaredField("maxPlayers");
            fieldMaxPlayers.setAccessible(true);
            fieldMaxPlayers.set(playerList, maxPlayers);
            if (!Bukkit.getOnlinePlayers().isEmpty()) {
                Player player = Bukkit.getOnlinePlayers().stream().findFirst().orElse(null);
                this.plugin.getServerData().leavePlayer(player.getUniqueId(), maxPlayers);
                this.plugin.getServerData().joinPlayer(player.getUniqueId(), maxPlayers);
            }
        }
        catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
    }

    public void sendPlayerToServer(Player player, String server) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
        }
        catch (Exception e) {
            e.printStackTrace(System.out);
        }
        player.sendPluginMessage((Plugin)this, "BungeeCord", b.toByteArray());
    }

    public void sendPlayerToServer(Player player, boolean silent, String server) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("PlayerConnect");
            out.writeUTF(server);
            out.writeBoolean(silent);
        }
        catch (Exception e) {
            e.printStackTrace(System.out);
        }
        player.sendPluginMessage((Plugin)this, "BungeeCord", b.toByteArray());
    }

    public void sendPlayerToServer(Player player, ServerType ... serverType) {
        this.sendPlayerToServer(player, false, serverType);
    }

    public void sendPlayerToServer(Player player, boolean silent, ServerType ... serverType) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("SearchServer");
        out.writeUTF(Joiner.on((char)'-').join((Object[])serverType));
        out.writeBoolean(silent);
        player.sendPluginMessage((Plugin)this, "BungeeCord", out.toByteArray());
    }

    public void performCommand(Player player, String command) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("BungeeCommand");
        out.writeUTF(command);
        player.sendPluginMessage((Plugin)this, "BungeeCord", out.toByteArray());
    }

    public void debug(String string) {
        this.plugin.debug(string);
        Player player = Bukkit.getPlayer((String)"yandv");
        if (player != null) {
            PlayerHelper.actionbar(player, "\u00a7c" + string);
        }
    }

    public String getColorByLevel(int level) {
        if (level >= 0 && level < 10) {
            return "\u00a77" + level + "\u272b";
        }
        if (level >= 10 && level < 20) {
            return "\u00a7a" + level + "\u2736";
        }
        if (level >= 20 && level < 30) {
            return "\u00a7b" + level + "\u273b";
        }
        if (level >= 30 && level < 40) {
            return "\u00a7d" + level + "\u2743";
        }
        if (level >= 40 && level < 50) {
            return "\u00a7e" + level + "\u2737";
        }
        if (level >= 50 && level < 60) {
            return "\u00a76" + level + "\u272b";
        }
        if (level >= 60 && level < 70) {
            return "\u00a75" + level + "\u2739";
        }
        if (level >= 70 && level < 80) {
            return "\u00a72" + level + "\u2726";
        }
        if (level >= 80 && level < 90) {
            return "\u00a71" + level + "\u2735";
        }
        if (level >= 90 && level < 100) {
            return "\u00a7c" + level + "\u2731";
        }
        return "\u00a74" + level + "\u2665";
    }

    public int getMaxPoints(int level) {
        return 500 * (level / 9) + (level % 9 == 9 ? 0 : 500);
    }

    public String createProgressBar(char character, char has, char need, int amount, double current, double max) {
        int a;
        StringBuilder bar = new StringBuilder();
        double percentage = current / max;
        double count = (double)amount * percentage;
        if (count > 0.0) {
            bar.append("\u00a7" + has);
            a = 0;
            while ((double)a < count) {
                bar.append(character);
                ++a;
            }
        }
        if ((double)amount - count > 0.0) {
            bar.append("\u00a7" + need);
            a = 0;
            while ((double)a < (double)amount - count) {
                bar.append(character);
                ++a;
            }
        }
        return bar.toString();
    }

    public String createProgressBar(char character, char need, int amount, double current, double max) {
        return this.createProgressBar(character, 'a', need, amount, current, max);
    }

    public String createProgressBar(char character, int amount, double current, double max) {
        return this.createProgressBar(character, 'a', 'c', amount, current, max);
    }

    public <T> MetadataValue createMeta(T object) {
        return new FixedMetadataValue((Plugin)this, object);
    }

    public static Optional<Player> getPlayer(UUID uniqueId) {
        Player player = Bukkit.getPlayer((UUID)uniqueId);
        return player == null ? Optional.empty() : Optional.of(player);
    }

    public static Optional<Player> getPlayer(String playerName, boolean exact) {
        Player player = exact ? Bukkit.getPlayer((String)playerName) : Bukkit.getPlayerExact((String)playerName);
        return player == null ? Optional.empty() : Optional.of(player);
    }

    public CommonPlugin getPlugin() {
        return this.plugin;
    }

    public BlockManager getBlockManager() {
        return this.blockManager;
    }

    public ChatManager getChatManager() {
        return this.chatManager;
    }

    public CombatlogManager getCombatlogManager() {
        return this.combatlogManager;
    }

    public CooldownManager getCooldownManager() {
        return this.cooldownManager;
    }

    public HologramManager getHologramManager() {
        return this.hologramManager;
    }

    public LocationManager getLocationManager() {
        return this.locationManager;
    }

    public VanishManager getVanishManager() {
        return this.vanishManager;
    }

    public PermissionManager getPermissionManager() {
        return this.permissionManager;
    }

    public ServerManager getServerManager() {
        return this.serverManager;
    }

    public StormCore getStormCore() {
        return this.stormCore;
    }

    public boolean isServerLog() {
        return this.serverLog;
    }

    public boolean isTagControl() {
        return this.tagControl;
    }

    public boolean isRemovePlayerDat() {
        return this.removePlayerDat;
    }

    public ChatState getChatState() {
        return this.chatState;
    }

    public boolean isBlockCommands() {
        return this.blockCommands;
    }

    public boolean isPermissionControl() {
        return this.permissionControl;
    }

    public boolean isRegisterCommands() {
        return this.registerCommands;
    }

    public Set<StatusType> getPreloadedStatus() {
        return this.preloadedStatus;
    }

    public static BukkitCommon getInstance() {
        return instance;
    }

    public void setServerLog(boolean serverLog) {
        this.serverLog = serverLog;
    }

    public void setTagControl(boolean tagControl) {
        this.tagControl = tagControl;
    }

    public void setRemovePlayerDat(boolean removePlayerDat) {
        this.removePlayerDat = removePlayerDat;
    }

    public void setChatState(ChatState chatState) {
        this.chatState = chatState;
    }

    public void setBlockCommands(boolean blockCommands) {
        this.blockCommands = blockCommands;
    }

    public void setPermissionControl(boolean permissionControl) {
        this.permissionControl = permissionControl;
    }

    public void setRegisterCommands(boolean registerCommands) {
        this.registerCommands = registerCommands;
    }

    public static enum ChatState {
        DISABLED,
        PAYMENT,
        YOUTUBER,
        ENABLED;

    }

    public class UpdateScheduler
    implements Runnable {
        private long currentTick;

        @Override
        public void run() {
            ++this.currentTick;
            Bukkit.getPluginManager().callEvent((Event)new UpdateEvent(UpdateEvent.UpdateType.TICK, this.currentTick));
            if ((double)this.currentTick % 20.0 == 0.0) {
                Bukkit.getPluginManager().callEvent((Event)new UpdateEvent(UpdateEvent.UpdateType.SECOND, this.currentTick));
            }
            if ((double)this.currentTick % 1200.0 == 0.0) {
                Bukkit.getPluginManager().callEvent((Event)new UpdateEvent(UpdateEvent.UpdateType.MINUTE, this.currentTick));
            }
        }
    }
}

