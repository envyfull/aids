/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Play$Client
 *  com.comphenix.protocol.PacketType$Play$Server
 *  com.comphenix.protocol.ProtocolLibrary
 *  com.comphenix.protocol.events.ListenerPriority
 *  com.comphenix.protocol.events.PacketAdapter
 *  com.comphenix.protocol.events.PacketEvent
 *  com.comphenix.protocol.events.PacketListener
 *  com.comphenix.protocol.wrappers.WrappedChatComponent
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Difficulty
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.WorldBorder
 *  org.bukkit.WorldCreator
 *  org.bukkit.craftbukkit.libs.joptsimple.internal.Strings
 *  org.bukkit.craftbukkit.v1_8_R3.CraftWorld
 *  org.bukkit.entity.Item
 *  org.bukkit.entity.ItemFrame
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.HandlerList
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.ItemSpawnEvent
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 */
package br.com.dragonmc.game.bedwars;

import br.com.dragonmc.game.bedwars.island.Island;
import br.com.dragonmc.game.bedwars.manager.GeneratorManager;
import br.com.dragonmc.game.bedwars.manager.IslandManager;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.game.engine.GameAPI;
import br.com.dragonmc.game.bedwars.event.IslandWinEvent;
import br.com.dragonmc.game.bedwars.gamer.Gamer;
import br.com.dragonmc.game.bedwars.generator.Generator;
import br.com.dragonmc.game.bedwars.generator.GeneratorType;
import br.com.dragonmc.game.bedwars.listener.PlayerListener;
import br.com.dragonmc.game.bedwars.listener.ScoreboardListener;
import br.com.dragonmc.game.bedwars.scheduler.GameScheduler;
import br.com.dragonmc.game.bedwars.scheduler.WaitingScheduler;
import br.com.dragonmc.game.engine.scheduler.Scheduler;
import br.com.dragonmc.core.bukkit.utils.player.PlayerHelper;
import br.com.dragonmc.core.bukkit.utils.worldedit.schematic.DataException;
import br.com.dragonmc.core.bukkit.utils.worldedit.schematic.Schematic;
import br.com.dragonmc.core.common.language.Language;
import br.com.dragonmc.core.common.medal.Medal;
import br.com.dragonmc.core.common.member.Member;
import br.com.dragonmc.core.common.server.ServerType;
import br.com.dragonmc.core.common.server.loadbalancer.server.MinigameState;
import br.com.dragonmc.core.common.utils.configuration.Configuration;
import br.com.dragonmc.core.common.utils.configuration.impl.JsonConfiguration;
import br.com.dragonmc.core.common.utils.string.StringFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.WorldCreator;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class GameMain
extends GameAPI
implements Listener {
    public static final char[] CHARS = "abcdefghijklmnoprstuvwxyz".toCharArray();
    private static GameMain instance;
    private GeneratorManager generatorManager;
    private IslandManager islandManager;
    private NextUpgrade generatorUpgrade = this.createUpgrade(GeneratorType.DIAMOND, 2, 360);
    private double minimunDistanceToPlaceBlocks;
    private double minimunY;
    private int playersPerTeam = 1;
    private int teamPerGame = 8;
    private double maxHeight;
    private List<Location> playersBlock = new ArrayList<Location>();
    private Schematic towerSchematic;
    private JsonConfiguration configuration;

    @Override
    public void onLoad() {
        super.onLoad();
        instance = this;
        this.loadConfiguration();
        this.setGamerClass(Gamer.class);
        this.setCollectionName("bedwars-gamer");
        this.setUnloadGamer(true);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        try {
            this.towerSchematic = Schematic.getInstance().loadSchematic(new File(this.getDataFolder(), "tower.schematic"));
        }
        catch (IOException | DataException exception) {
            // empty catch block
        }
        WorldCreator.name((String)"spawn").createWorld();
        this.generatorManager = new GeneratorManager();
        this.islandManager = new IslandManager();
        this.setTime(60);
        this.setState(MinigameState.STARTING);
        this.setPlayersPerTeam(this.getPlugin().getServerType().getPlayersPerTeam());
        this.setTeamPerGame(this.getPlugin().getServerType().name().contains("X") ? 2 : this.getPlayersPerTeam() * 8);
        this.setMaxPlayers(8 * this.getPlayersPerTeam());
        this.startScheduler(new WaitingScheduler());
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new PlayerListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new ScoreboardListener(), (Plugin)this);
        Bukkit.getWorlds().forEach(world -> {
            world.setAutoSave(false);
            world.getEntitiesByClass(Item.class).forEach(item -> item.remove());
            ((CraftWorld)world).getHandle().savingDisabled = true;
            world.setDifficulty(Difficulty.NORMAL);
        });
        ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener)new PacketAdapter((Plugin)this, ListenerPriority.NORMAL, new PacketType[]{PacketType.Play.Server.CHAT}){

            public void onPacketSending(PacketEvent e) {
                if (e.getPacketType() == PacketType.Play.Server.CHAT || e.getPacketType() == PacketType.Play.Client.CHAT) {
                    try {
                        String json = ((WrappedChatComponent)e.getPacket().getChatComponents().read(0)).getJson();
                        if (json.equals("{\"translate\":\"tile.bed.noSleep\"}") || json.equals("{\"translate\":\"tile.bed.notValid\"}")) {
                            e.setCancelled(true);
                        }
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
            }
        });
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        if (event.getEntity().getItemStack().getType() == Material.BED || event.getEntity().getItemStack().getType() == Material.BED_BLOCK) {
            event.setCancelled(true);
        }
    }

    public Configuration getConfiguration() {
        return CommonPlugin.getInstance().getConfigurationManager().getConfigByName("bedwars");
    }

    private void loadConfiguration() {
        this.configuration = CommonPlugin.getInstance().getConfigurationManager().loadConfig("bedwars.json", Paths.get(this.getDataFolder().toURI()).getParent().getParent().toFile(), true, JsonConfiguration.class);
        try {
            this.configuration.loadConfig();
        }
        catch (Exception e) {
            e.printStackTrace();
            this.getPlugin().getPluginPlatform().shutdown("Cannot load the configuration bedwars.json.");
            return;
        }
        this.setMap(this.configuration.get("mapName", "Unknown"));
        this.maxHeight = this.configuration.get("maxHeight", 100.0);
        this.minimunDistanceToPlaceBlocks = this.configuration.get("distance-to-place-blocks", 12.0);
        this.minimunY = this.configuration.get("minimunY", 10.0);
        this.debug("The configuration bedwars.json has been loaded!");
    }

    public void setMinimunY(double minimunY) {
        this.minimunY = minimunY;
        this.configuration.set("minimunY", 10.0);
        try {
            this.configuration.saveConfig();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMinimunDistanceToPlaceBlocks(double minimunDistanceToPlaceBlocks) {
        this.minimunDistanceToPlaceBlocks = minimunDistanceToPlaceBlocks;
        this.configuration.set("distance-to-place-blocks", 12.0);
        try {
            this.configuration.saveConfig();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getId(Island island) {
        return CHARS[island.getIslandColor().getColor().ordinal()] + "";
    }

    public String getTag(Island island, Language language) {
        return "" + island.getIslandColor().getColor() + ChatColor.BOLD + language.t(island.getIslandColor().name().toLowerCase() + "-symbol", new String[0]) + island.getIslandColor().getColor() + " ";
    }

    @Override
    public void onDisable() {
        Bukkit.getWorld((String)"world").getEntities().forEach(entity -> {
            if (!(entity instanceof ItemFrame)) {
                entity.remove();
            }
        });
        super.onDisable();
    }

    public int getMaxTeams() {
        return this.getTeamPerGame();
    }

    public void startGame() {
        for (Scheduler scheduler : this.getSchedulerManager().getSchedulers()) {
            this.getSchedulerManager().unloadScheduler(scheduler);
            if (!(scheduler instanceof WaitingScheduler)) continue;
            HandlerList.unregisterAll((Listener)((WaitingScheduler)scheduler));
        }
        this.setUnloadGamer(false);
        GameAPI.getInstance().setState(MinigameState.GAMETIME);
        GameAPI.getInstance().startScheduler(new GameScheduler());
    }

    public List<Gamer> getAlivePlayers() {
        return GameAPI.getInstance().getGamerManager().filter(gamer -> gamer.isAlive(), Gamer.class);
    }

    public void checkWinner() {
        List islandList = GameMain.getInstance().getIslandManager().values().stream().filter(island -> island.getIslandStatus() != Island.IslandStatus.LOSER).collect(Collectors.toList());
        if (islandList.isEmpty()) {
            this.handleServer();
        } else if (islandList.size() == 1) {
            Island islandWinner = (Island) islandList.stream().findFirst().orElse(null);
            this.setState(MinigameState.WINNING);
            Bukkit.getPluginManager().callEvent((Event)new IslandWinEvent(islandWinner));
            Bukkit.getOnlinePlayers().forEach(player -> {
                Island island = GameMain.getInstance().getIslandManager().getIsland(player.getUniqueId());
                if (island == islandWinner) {
                    player.setAllowFlight(true);
                    player.setFlying(true);
                    PlayerHelper.title(player, "\u00a7%bedwars-title-win%\u00a7", "\u00a7%bedwars-subtitle-win%\u00a7", 10, 200, 10);
                } else {
                    PlayerHelper.title(player, "\u00a7%bedwars-title-lose%\u00a7", "\u00a7%bedwars-subtitle-lose%\u00a7", 10, 200, 10);
                }
            });
            List topGamers = GameAPI.getInstance().getGamerManager().stream(Gamer.class).filter(gamer -> !this.getVanishManager().isPlayerInAdmin(gamer.getUniqueId()) && this.getIslandManager().getIsland(gamer.getUniqueId()) != null).sorted((o1, o2) -> o2.getFinalKills() - o1.getFinalKills()).collect(Collectors.toList());
            Gamer gamer2 = GameAPI.getInstance().getGamerManager().sort((o1, o2) -> o2.getBrokenBeds() - o1.getBrokenBeds(), Gamer.class).stream().findFirst().orElse(null);
            Bukkit.getOnlinePlayers().forEach(player -> {
                Language language = Language.getLanguage(player.getUniqueId());
                player.sendMessage("\u00a7a\u00a7m" + Strings.repeat((char)'-', (int)64));
                player.sendMessage(StringFormat.centerString("\u00a7b\u00a7lBed Wars", 128));
                player.sendMessage(StringFormat.centerString("\u00a7eVencedor \u00a77- " + islandWinner.getIslandColor().getColor() + "Time \u00a7%" + islandWinner.getIslandColor().name().toLowerCase() + "-name%\u00a7", 128));
                player.sendMessage("");
                player.sendMessage(StringFormat.centerString(language.t("bedwars.win-message.top-final-kills", new String[0]), 128));
                for (int i = 1; i <= Math.min(topGamers.size(), 3); ++i) {
                    player.sendMessage(StringFormat.centerString((i == 1 ? "\u00a7a" : (i == 2 ? "\u00a7e" : "\u00a7c")) + i + "\u00b0 \u00a77" + ((Gamer)topGamers.get(i - 1)).getPlayerName() + " \u00a7b- \u00a7f" + ((Gamer)topGamers.get(i - 1)).getFinalKills(), 128));
                }
                player.sendMessage("");
                player.sendMessage(StringFormat.centerString(language.t("bedwars.win-message.top-bed-broker", new String[0]), 128));
                player.sendMessage(StringFormat.centerString("\u00a77" + gamer2.getPlayerName() + " \u00a7b- \u00a7f" + gamer2.getBrokenBeds(), 128));
                player.sendMessage(" ");
                player.sendMessage("\u00a7a\u00a7m" + Strings.repeat((char)'-', (int)64));
            });
            this.handleServer();
        }
    }

    private void handleServer() {
        new BukkitRunnable(){
            int time = 0;

            public void run() {
                if (Bukkit.getOnlinePlayers().isEmpty()) {
                    Bukkit.shutdown();
                    return;
                }
                if (++this.time == 8) {
                    Bukkit.getOnlinePlayers().forEach(player -> GameAPI.getInstance().sendPlayerToServer((Player)player, CommonPlugin.getInstance().getServerType(), CommonPlugin.getInstance().getServerType().getServerLobby(), ServerType.LOBBY));
                } else if (this.time == 12) {
                    Bukkit.shutdown();
                }
            }
        }.runTaskTimer((Plugin)GameAPI.getInstance(), 20L, 20L);
    }

    public List<Location> getNearestBlocksByMaterial(Location location, Material material, int radius, int height) {
        ArrayList<Location> locationList = new ArrayList<Location>();
        for (int x = -radius; x < radius; ++x) {
            for (int y = -height; y < height; ++y) {
                for (int z = -radius; z < radius; ++z) {
                    Location loc = location.clone().add((double)x, (double)y, (double)z);
                    if (loc.getBlock().getType() != material) continue;
                    locationList.add(loc.getBlock().getLocation());
                }
            }
        }
        return locationList;
    }

    public List<Location> getNearestBlocksByMaterial(Location location, Material material, int radius) {
        return this.getNearestBlocksByMaterial(location, material, radius, 0);
    }

    public NextUpgrade createUpgrade(GeneratorType generatorType, int level, int timer) {
        return new NextUpgrade(generatorType.name().toLowerCase() + "-" + level, timer, v -> {
            if (generatorType == GeneratorType.EMERALD && level == 3) {
                this.setGeneratorUpgrade(new NextUpgrade("Deathmatch", GameAPI.getInstance().getTime() + 1 + 360 - 60, v2 -> {
                    Bukkit.broadcastMessage((String)"\u00a7c\u00a7lDEATHMATCH \u00a7fO servidor come\u00e7ar\u00e1 a reduzir a borda do mundo.");
                    WorldBorder worldBorder = ((World)Bukkit.getWorlds().stream().findFirst().orElse(null)).getWorldBorder();
                    worldBorder.setDamageAmount(1.0);
                    worldBorder.setCenter(GameMain.getInstance().getLocationManager().getLocation("central"));
                    worldBorder.setSize(300.0);
                    worldBorder.setSize(20.0, 180L);
                    this.setGeneratorUpgrade(null);
                }));
                return;
            }
            GeneratorType newUpgrade = generatorType == GeneratorType.DIAMOND ? GeneratorType.EMERALD : GeneratorType.DIAMOND;
            this.setGeneratorUpgrade(this.createUpgrade(newUpgrade, ((Generator)this.getGeneratorManager().getGenerators(newUpgrade).stream().findFirst().orElse(null)).getLevel() + 1, GameAPI.getInstance().getTime() + 1 + 360));
            List<Generator> list = this.getGeneratorManager().getGenerators(generatorType);
            for (Generator generator : list) {
                generator.setLevel(generator.getLevel() + 1);
            }
            if (generatorType == GeneratorType.DIAMOND) {
                generatorType.setTimer(generatorType.getTimer() - (level - 1) * 5);
            } else {
                generatorType.setTimer(generatorType.getTimer() - (level - 1) * 10);
            }
        });
    }

    public String createMessage(Player player, String message, Island island, boolean global, boolean globaPrefix, int level) {
        String levelFormatted = "\u00a77[" + this.getColorByLevel(level) + "\u00a77]";
        Member member = CommonPlugin.getInstance().getMemberManager().getMember(player.getUniqueId());
        Medal medal = member.getMedal();
        return ((globaPrefix ? (global ? "\u00a76[G] " : "") : "") + island.getIslandColor().getColor() + "[\u00a7%" + island.getIslandColor().name().toLowerCase() + "-symbol%\u00a7] " + levelFormatted + " " + (medal == null ? "" : medal.getChatColor() + medal.getSymbol() + " ") + member.getTag().getRealPrefix() + player.getName() + " \u00a77\u00bb \u00a7f" + message).trim();
    }

    public boolean hasLose(UUID uniqueId) {
        Island island = this.getIslandManager().getIsland(uniqueId);
        return island == null ? true : island.getIslandStatus() == Island.IslandStatus.LOSER;
    }

    public boolean isSpectator(UUID uniqueId) {
        Gamer gamer = this.getGamerManager().getGamer(uniqueId, Gamer.class);
        return gamer == null ? false : gamer.isSpectator();
    }

    public GeneratorManager getGeneratorManager() {
        return this.generatorManager;
    }

    public IslandManager getIslandManager() {
        return this.islandManager;
    }

    public NextUpgrade getGeneratorUpgrade() {
        return this.generatorUpgrade;
    }

    public double getMinimunDistanceToPlaceBlocks() {
        return this.minimunDistanceToPlaceBlocks;
    }

    public double getMinimunY() {
        return this.minimunY;
    }

    public int getPlayersPerTeam() {
        return this.playersPerTeam;
    }

    public int getTeamPerGame() {
        return this.teamPerGame;
    }

    public double getMaxHeight() {
        return this.maxHeight;
    }

    public List<Location> getPlayersBlock() {
        return this.playersBlock;
    }

    public Schematic getTowerSchematic() {
        return this.towerSchematic;
    }

    public static GameMain getInstance() {
        return instance;
    }

    public void setGeneratorUpgrade(NextUpgrade generatorUpgrade) {
        this.generatorUpgrade = generatorUpgrade;
    }

    public void setPlayersPerTeam(int playersPerTeam) {
        this.playersPerTeam = playersPerTeam;
    }

    public void setTeamPerGame(int teamPerGame) {
        this.teamPerGame = teamPerGame;
    }

    public void setMaxHeight(double maxHeight) {
        this.maxHeight = maxHeight;
    }

    public class NextUpgrade {
        private String name;
        private int timer;
        private Consumer<Void> consumer;

        public NextUpgrade(String name, int timer, Consumer<Void> consumer) {
            this.name = name;
            this.timer = timer;
            this.consumer = consumer;
        }

        public String getName() {
            return this.name;
        }

        public int getTimer() {
            return this.timer;
        }

        public Consumer<Void> getConsumer() {
            return this.consumer;
        }
    }
}

