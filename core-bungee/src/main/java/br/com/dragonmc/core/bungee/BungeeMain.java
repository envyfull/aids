/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.io.ByteArrayDataOutput
 *  com.google.common.io.ByteStreams
 *  net.md_5.bungee.api.ProxyServer
 *  net.md_5.bungee.api.connection.PendingConnection
 *  net.md_5.bungee.api.connection.ProxiedPlayer
 *  net.md_5.bungee.api.plugin.Listener
 *  net.md_5.bungee.api.plugin.Plugin
 *  net.md_5.bungee.config.Configuration
 *  net.md_5.bungee.config.ConfigurationProvider
 *  net.md_5.bungee.config.YamlConfiguration
 *  net.md_5.bungee.connection.InitialHandler
 *  net.md_5.bungee.connection.LoginResult
 *  net.md_5.bungee.connection.LoginResult$Property
 */
package br.com.dragonmc.core.bungee;

import br.com.dragonmc.core.bungee.listener.*;
import br.com.dragonmc.core.bungee.networking.BungeePubSubHandler;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import br.com.dragonmc.core.common.CommonConst;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.common.backend.redis.RedisConnection;
import br.com.dragonmc.core.bungee.command.BungeeCommandFramework;
import br.com.dragonmc.core.bungee.manager.BungeeServerManager;
import br.com.dragonmc.core.bungee.manager.LoginManager;
import br.com.dragonmc.core.bungee.member.BungeeParty;
import br.com.dragonmc.core.common.server.ServerManager;
import br.com.dragonmc.core.common.utils.skin.Skin;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.connection.LoginResult;
import net.md_5.bungee.protocol.Property;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BungeeMain
extends Plugin {
    private static BungeeMain instance;
    private CommonPlugin plugin;
    private ServerManager serverManager;
    private LoginManager loginManager;
    private RedisConnection.PubSubListener pubSubListener;
    private Configuration config;
    private int playersRecord;
    private boolean whitelistEnabled;
    private long whitelistExpires;
    private List<String> whiteList = new ArrayList<String>();
    private List<String> messages;

    public void onLoad() {
        instance = this;
        this.plugin = new CommonPlugin(new BungeePlatform());
        super.onLoad();
    }

    public void onEnable() {
        this.loadConfiguration();
        this.serverManager = new BungeeServerManager();
        this.loginManager = new LoginManager();
        this.getProxy().getPluginManager().registerListener((Plugin)this, (Listener)new DataListener());
        this.getProxy().getPluginManager().registerListener((Plugin)this, (Listener)new LoginListener());
        this.getProxy().getPluginManager().registerListener((Plugin)this, (Listener)new LogListener());
        this.getProxy().getPluginManager().registerListener((Plugin)this, (Listener)new MemberListener());
        this.getProxy().getPluginManager().registerListener((Plugin)this, (Listener)new MessageListener());
        this.getProxy().getPluginManager().registerListener((Plugin)this, (Listener)new ServerListener());
        ProxyServer.getInstance().getServers().remove("lobby");
        this.plugin.loadServers(this.serverManager);
        this.plugin.setServerId("bungeecord.dragonmc.com.br");
        this.plugin.getServerData().startServer(3000);
        this.plugin.getServerData().setJoinEnabled(!this.whitelistEnabled);
        this.plugin.getMemberData().reloadPlugins();
        this.plugin.setPartyClass(BungeeParty.class);
        this.plugin.getReportManager().getReports().stream().forEach(report -> report.setOnline(false));
        new BungeeCommandFramework(this).loadCommands("br.com.dragonmc.core");
        this.pubSubListener = new RedisConnection.PubSubListener(this.plugin.getRedisConnection(), new BungeePubSubHandler(), "member_field", "server_info", "server_packet");
        this.getProxy().getScheduler().runAsync((Plugin)this, (Runnable)this.pubSubListener);
        this.getProxy().getScheduler().schedule((Plugin)this, () -> {
            if (!this.getMessages().isEmpty()) {
                ProxyServer.getInstance().broadcast(this.getMessages().get(CommonConst.RANDOM.nextInt(this.getMessages().size())));
            }
        }, 2L, 2L, TimeUnit.MINUTES);
        super.onEnable();
    }

    public boolean isMaintenance() {
        if (this.whitelistEnabled) {
            if (this.whitelistExpires == -1L || this.whitelistExpires > System.currentTimeMillis()) {
                return true;
            }
            this.setWhitelistEnabled(false, -1L);
        }
        return false;
    }

    public void addMemberToWhiteList(String playerName) {
        if (!this.whiteList.contains(playerName.toLowerCase())) {
            this.whiteList.add(playerName.toLowerCase());
            this.getConfig().set("whiteList", this.whiteList);
            this.saveConfig();
        }
    }

    public void removeMemberFromWhiteList(String playerName) {
        if (this.whiteList.contains(playerName.toLowerCase())) {
            this.whiteList.remove(playerName.toLowerCase());
            this.getConfig().set("whiteList", this.whiteList);
            this.saveConfig();
        }
    }

    public boolean isMemberInWhiteList(String playerName) {
        return this.whiteList.contains(playerName.toLowerCase());
    }

    public void setWhitelistEnabled(boolean whitelistEnabled, long time) {
        this.whitelistEnabled = whitelistEnabled;
        this.whitelistExpires = time;
        this.getConfig().set("whitelistEnabled", (Object)whitelistEnabled);
        this.getConfig().set("whitelistExpires", (Object)this.whitelistExpires);
        this.saveConfig();
        this.plugin.getServerData().setJoinEnabled(!whitelistEnabled);
    }

    public void onDisable() {
        this.plugin.getServerData().stopServer();
        super.onDisable();
    }

    private void loadConfiguration() {
        try {
            File configFile;
            if (!this.getDataFolder().exists()) {
                this.getDataFolder().mkdir();
            }
            if (!(configFile = new File(this.getDataFolder(), "config.yml")).exists()) {
                try {
                    configFile.createNewFile();
                    try (InputStream is = this.getResourceAsStream("config.yml");
                         FileOutputStream os = new FileOutputStream(configFile);){
                        ByteStreams.copy((InputStream)is, (OutputStream)os);
                    }
                }
                catch (IOException e) {
                    throw new RuntimeException("Unable to create configuration file", e);
                }
            }
            this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
            this.loadDefaultConfig(this.config);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void loadDefaultConfig(Configuration config2) {
        this.whitelistEnabled = this.config.getBoolean("whitelistEnabled", false);
        this.whitelistExpires = this.config.getLong("whitelistExpires", -1L);
        this.whiteList = new ArrayList<String>(this.config.getStringList("whiteList"));
        this.messages = this.config.getStringList("messages.broadcast");
    }

    public void addMessage(String message) {
        this.messages.add(message);
        this.config.set("messages.broadcast", this.messages);
        this.saveConfig();
    }

    public void removeMessage(int index) {
        this.messages.remove(index);
        this.config.set("messages.broadcast", this.messages);
        this.saveConfig();
    }

    public void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(this.config, new File(this.getDataFolder(), "config.yml"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getAveragePing(Collection<ProxiedPlayer> players) {
        int averagePing = 0;
        for (ProxiedPlayer player : players) {
            averagePing += player.getPing();
        }
        return averagePing / Math.max(ProxyServer.getInstance().getPlayers().size(), 1);
    }

    public void teleport(ProxiedPlayer player, ProxiedPlayer target) {
        player.connect(target.getServer().getInfo());
        ProxyServer.getInstance().getScheduler().schedule((Plugin)BungeeMain.getInstance(), () -> {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("BungeeTeleport");
            out.writeUTF(target.getUniqueId().toString());
            player.getServer().sendData("BungeeCord", out.toByteArray());
        }, 300L, TimeUnit.MILLISECONDS);
    }

    public void loadTexture(PendingConnection connection, Skin skin) {
        if (skin == null) {
            return;
        }
        InitialHandler initialHandler = (InitialHandler)connection;
        LoginResult loginProfile = initialHandler.getLoginProfile();
        Property property = new Property("textures", skin.getValue(), skin.getSignature());
        if (loginProfile == null || loginProfile == null && property == null) {
            Property[] propertyArray;
            String string = connection.getUniqueId().toString().replace("-", "");
            String string2 = connection.getName();
            if (property == null) {
                propertyArray = new Property[]{};
            } else {
                Property[] propertyArray2 = new Property[1];
                propertyArray = propertyArray2;
                propertyArray2[0] = property;
            }
            LoginResult loginResult = new LoginResult(string, string2, propertyArray);
            try {
                Class<?> initialHandlerClass = connection.getClass();
                Field profileField = initialHandlerClass.getDeclaredField("loginProfile");
                profileField.setAccessible(true);
                profileField.set(connection, loginResult);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (property != null) {
            loginProfile.setProperties(new Property[]{property});
        }
    }

    public CommonPlugin getPlugin() {
        return this.plugin;
    }

    public ServerManager getServerManager() {
        return this.serverManager;
    }

    public LoginManager getLoginManager() {
        return this.loginManager;
    }

    public RedisConnection.PubSubListener getPubSubListener() {
        return this.pubSubListener;
    }

    public Configuration getConfig() {
        return this.config;
    }

    public int getPlayersRecord() {
        return this.playersRecord;
    }

    public boolean isWhitelistEnabled() {
        return this.whitelistEnabled;
    }

    public long getWhitelistExpires() {
        return this.whitelistExpires;
    }

    public List<String> getWhiteList() {
        return this.whiteList;
    }

    public List<String> getMessages() {
        return this.messages;
    }

    public static BungeeMain getInstance() {
        return instance;
    }

    public void setPlayersRecord(int playersRecord) {
        this.playersRecord = playersRecord;
    }
}

