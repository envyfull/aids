/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.Sound
 *  org.bukkit.World
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 */
package br.com.dragonmc.game.bedwars.island;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;
import br.com.dragonmc.core.common.CommonConst;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.game.engine.GameAPI;
import br.com.dragonmc.game.bedwars.GameMain;
import br.com.dragonmc.game.bedwars.event.island.IslandBedBreakEvent;
import br.com.dragonmc.game.bedwars.event.island.IslandLoseEvent;
import br.com.dragonmc.game.bedwars.event.island.IslandUpgradeEvent;
import br.com.dragonmc.game.bedwars.gamer.Gamer;
import br.com.dragonmc.game.bedwars.generator.Generator;
import br.com.dragonmc.game.bedwars.generator.impl.NormalGenerator;
import br.com.dragonmc.game.bedwars.menu.StoreInventory;
import br.com.dragonmc.game.bedwars.menu.UpgradeInventory;
import br.com.dragonmc.game.bedwars.utils.GamerHelper;
import br.com.dragonmc.game.engine.gamer.Team;
import br.com.dragonmc.core.bukkit.utils.Location;
import br.com.dragonmc.core.bukkit.utils.player.PlayerHelper;
import br.com.dragonmc.core.bukkit.utils.scoreboard.ScoreboardAPI;
import br.com.dragonmc.core.common.language.Language;
import br.com.dragonmc.core.common.member.status.Status;
import br.com.dragonmc.core.common.member.status.StatusType;
import br.com.dragonmc.core.common.member.status.types.BedwarsCategory;
import br.com.dragonmc.core.common.utils.string.StringFormat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class Island
implements Cloneable {
    private IslandColor islandColor;
    private Location spawnLocation;
    private Location bedLocation;
    private Location shopLocation;
    private Location upgradeLocation;
    private Map<Material, List<Location>> generatorMap;
    private transient IslandStatus islandStatus;
    private transient List<Generator> islandGenerators;
    private transient Map<IslandUpgrade, Integer> upgradeMap;
    private transient Team team;

    public Island loadIsland(Team team) {
        String worldName = ((World)Bukkit.getWorlds().stream().findFirst().orElse(null)).getName();
        if (this.spawnLocation == null) {
            this.spawnLocation = new Location(worldName);
        }
        if (this.bedLocation == null) {
            this.bedLocation = new Location(worldName);
        }
        if (this.shopLocation == null) {
            this.shopLocation = new Location(worldName);
        }
        if (this.upgradeLocation == null) {
            this.upgradeLocation = new Location(worldName);
        }
        this.islandStatus = team.getPlayerSet().isEmpty() ? IslandStatus.LOSER : IslandStatus.ALIVE;
        this.islandGenerators = new ArrayList<Generator>();
        this.upgradeMap = new HashMap<IslandUpgrade, Integer>();
        this.team = team;
        for (org.bukkit.Location location : GameMain.getInstance().getNearestBlocksByMaterial(this.bedLocation.getAsLocation(), Material.BED_BLOCK, 4, 2)) {
            if (this.islandStatus == IslandStatus.ALIVE) {
                location.getBlock().setMetadata("bed-island", GameAPI.getInstance().createMeta(this.islandColor));
                continue;
            }
            location.getBlock().setType(Material.AIR);
        }
        for (org.bukkit.Location location : GameMain.getInstance().getNearestBlocksByMaterial(this.spawnLocation.getAsLocation(), Material.CHEST, 10, 5)) {
            if (location.getBlock().getType() != Material.CHEST) continue;
            location.getBlock().setMetadata("chest-island", GameAPI.getInstance().createMeta(this.islandColor));
        }
        return this;
    }

    public void upgrade(Player player, IslandUpgrade upgrade) {
        Integer integer = this.upgradeMap.computeIfAbsent(upgrade, v -> 0) + 1;
        if (integer <= upgrade.getMaxLevel()) {
            Bukkit.getPluginManager().callEvent((Event)new IslandUpgradeEvent(this, upgrade, integer));
            this.stream(false).forEach(p -> p.sendMessage("\u00a7a" + player.getName() + " adquiriu a melhoria: \u00a7e" + Language.getLanguage(p.getUniqueId()).t("inventory-upgrade-" + upgrade.name().toLowerCase().replace("_", "-"), "%level%", "" + integer)));
        }
        this.upgradeMap.put(upgrade, integer);
    }

    public void removeUpgrade(IslandUpgrade islandUpgrade) {
        this.upgradeMap.remove((Object)islandUpgrade);
    }

    public Integer getUpgradeLevel(IslandUpgrade upgrade) {
        return this.upgradeMap.computeIfAbsent(upgrade, v -> 0);
    }

    public boolean hasUpgrade(IslandUpgrade upgrade) {
        return this.upgradeMap.containsKey((Object)upgrade);
    }

    public void handleBreakBed(Player player) {
        if (this.islandStatus == IslandStatus.ALIVE) {
            if (player != null) {
                Island island = GameMain.getInstance().getIslandManager().getIsland(player.getUniqueId());
                if (island == null) {
                    return;
                }
                Bukkit.getOnlinePlayers().forEach(p -> {
                    Language language = Language.getLanguage(p.getUniqueId());
                    p.sendMessage(language.t("bedwars.island-bed-broke", "%island%", language.t(this.islandColor.name().toLowerCase() + "-name", new String[0]), "%islandColor%", "\u00a7" + this.islandColor.getColor().getChar(), "%enimyIsland%", StringFormat.formatString(island.getIslandColor().name()), "%enimyIslandColor%", "\u00a7" + island.getIslandColor().getColor().getChar(), "%player%", player.getName()));
                    p.playSound(p.getLocation(), Sound.ENDERDRAGON_HIT, 1.0f, 1.0f);
                });
            }
            this.stream(false).forEach(p -> {
                PlayerHelper.title(p, "\u00a7c\u00a7lCAMA DESTRUIDA", "\u00a77Voc\u00ea n\u00e3o renascer\u00e1 mais.");
                p.getWorld().playSound(p.getLocation(), Sound.ENDERDRAGON_GROWL, 1.0f, 1.0f);
            });
            this.islandStatus = IslandStatus.BED_BROKEN;
            Bukkit.getPluginManager().callEvent((Event)new IslandBedBreakEvent(player, this));
            if (this.stream(false).count() == 0L) {
                this.handleLose();
            }
            for (org.bukkit.Location location : GameMain.getInstance().getNearestBlocksByMaterial(this.bedLocation.getAsLocation(), Material.BED_BLOCK, 4, 2)) {
                location.getBlock().setType(Material.AIR);
            }
        }
    }

    public void handleLose() {
        if (this.islandStatus != IslandStatus.LOSER) {
            Bukkit.getOnlinePlayers().forEach(p -> {
                Language language = Language.getLanguage(p.getUniqueId());
                p.sendMessage(language.t("bedwars.island-lost", "%island%", language.t(this.islandColor.name().toLowerCase() + "-name", new String[0]), "%islandColor%", "\u00a7" + this.islandColor.getColor().getChar()));
            });
            this.islandStatus = IslandStatus.LOSER;
            Bukkit.getPluginManager().callEvent((Event)new IslandLoseEvent(this));
            if (this.bedLocation.getAsLocation().getBlock().getType() == Material.BED_BLOCK) {
                this.bedLocation.getAsLocation().getBlock().setType(Material.AIR);
            }
            for (UUID id : this.getTeam().getPlayerSet()) {
                Status status = CommonPlugin.getInstance().getStatusManager().loadStatus(id, StatusType.BEDWARS);
                status.setInteger(BedwarsCategory.BEDWARS_WINSTREAK, 0);
                status.setInteger(BedwarsCategory.BEDWARS_WINSTREAK.getSpecialServer(), 0);
            }
        }
    }

    public void startIsland() {
        this.loadPlayers();
        this.loadGenerators();
        this.loadNpc();
        GameAPI.getInstance().debug("The island " + (Object)((Object)this.getIslandColor()) + " has been loaded.");
    }

    public void loadNpc() {
        GameMain.getInstance().createCharacter(this.getShopLocation().getAsLocation(), "ZAKl1k", (player, right) -> {
            new StoreInventory(player);
            return false;
        }).setDisplayName("\u00a7b\u00a7lLOJA").line("\u00a7eClique para ver mais.");
        GameMain.getInstance().createCharacter(this.getUpgradeLocation().getAsLocation(), "Kotcka", (player, right) -> {
            new UpgradeInventory(player);
            return false;
        }).setDisplayName("\u00a7b\u00a7lMELHORIAS").line("\u00a7eClique para ver mais.");
    }

    public void loadPlayers() {
        for (UUID uuid : this.getTeam().getPlayerSet()) {
            Player player = Bukkit.getPlayer((UUID)uuid);
            Gamer gamer = GameAPI.getInstance().getGamerManager().getGamer(player.getUniqueId(), Gamer.class);
            gamer.setAlive(true);
            player.teleport(this.getSpawnLocation().getAsLocation());
            player.playSound(player.getLocation(), Sound.FALL_BIG, 1.0f, 1.0f);
            GamerHelper.handlePlayerToGame(player);
            for (Player o : Bukkit.getOnlinePlayers()) {
                ScoreboardAPI.joinTeam(ScoreboardAPI.createTeamIfNotExistsToPlayer(o, GameMain.getInstance().getId(this), GameMain.getInstance().getTag(this, Language.getLanguage(o.getUniqueId())), ""), player);
            }
        }
    }

    public void loadGenerators() {
        for (Map.Entry<Material, List<Location>> entry : this.getGeneratorMap().entrySet()) {
            for (Location generatorLocation : entry.getValue()) {
                NormalGenerator createGenerator = new NormalGenerator(generatorLocation.getAsLocation(), entry.getKey());
                if (entry.getKey() == Material.IRON_INGOT) {
                    createGenerator.setGenerateTime(GameMain.getInstance().getPlayersPerTeam() == 1 ? 2000L : 1500L);
                } else if (entry.getKey() == Material.GOLD_INGOT) {
                    createGenerator.setGenerateTime(GameMain.getInstance().getPlayersPerTeam() == 1 ? 8000L : 6000L);
                }
                this.getIslandGenerators().add(createGenerator);
                GameMain.getInstance().getGeneratorManager().addGenerator(createGenerator);
            }
        }
    }

    public void checkLose() {
        if (this.islandStatus == IslandStatus.BED_BROKEN) {
            boolean alive = false;
            for (UUID uuid : this.getTeam().getPlayerSet()) {
                Gamer g = GameAPI.getInstance().getGamerManager().getGamer(uuid, Gamer.class);
                if (!g.isAlive()) continue;
                alive = true;
                break;
            }
            if (!alive) {
                this.handleLose();
            }
        }
    }

    public Stream<Player> stream(boolean nullable) {
        if (nullable) {
            return this.getTeam().getPlayerSet().stream().map(id -> Bukkit.getPlayer((UUID)id));
        }
        return this.getTeam().getPlayerSet().stream().map(id -> Bukkit.getPlayer((UUID)id)).filter(player -> player != null);
    }

    public void broadcast(String message) {
        this.getTeam().getPlayerSet().stream().map(id -> Bukkit.getPlayer((UUID)id)).forEach(player -> {
            if (player != null) {
                player.sendMessage(message);
            }
        });
    }

    public boolean equals(Object obj) {
        if (obj instanceof Island) {
            Island island = (Island)obj;
            return island.getIslandColor() == this.islandColor;
        }
        return super.equals(obj);
    }

    public String toString() {
        return CommonConst.GSON.toJson((Object)this);
    }

    public Island clone() {
        return new Island(this.islandColor, this.spawnLocation, this.bedLocation, this.shopLocation, this.upgradeLocation, this.generatorMap, null, null, null, null);
    }

    public IslandColor getIslandColor() {
        return this.islandColor;
    }

    public Location getSpawnLocation() {
        return this.spawnLocation;
    }

    public Location getBedLocation() {
        return this.bedLocation;
    }

    public Location getShopLocation() {
        return this.shopLocation;
    }

    public Location getUpgradeLocation() {
        return this.upgradeLocation;
    }

    public Map<Material, List<Location>> getGeneratorMap() {
        return this.generatorMap;
    }

    public IslandStatus getIslandStatus() {
        return this.islandStatus;
    }

    public List<Generator> getIslandGenerators() {
        return this.islandGenerators;
    }

    public Map<IslandUpgrade, Integer> getUpgradeMap() {
        return this.upgradeMap;
    }

    public Team getTeam() {
        return this.team;
    }

    public Island(IslandColor islandColor, Location spawnLocation, Location bedLocation, Location shopLocation, Location upgradeLocation, Map<Material, List<Location>> generatorMap, IslandStatus islandStatus, List<Generator> islandGenerators, Map<IslandUpgrade, Integer> upgradeMap, Team team) {
        this.islandColor = islandColor;
        this.spawnLocation = spawnLocation;
        this.bedLocation = bedLocation;
        this.shopLocation = shopLocation;
        this.upgradeLocation = upgradeLocation;
        this.generatorMap = generatorMap;
        this.islandStatus = islandStatus;
        this.islandGenerators = islandGenerators;
        this.upgradeMap = upgradeMap;
        this.team = team;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public void setBedLocation(Location bedLocation) {
        this.bedLocation = bedLocation;
    }

    public void setShopLocation(Location shopLocation) {
        this.shopLocation = shopLocation;
    }

    public void setUpgradeLocation(Location upgradeLocation) {
        this.upgradeLocation = upgradeLocation;
    }

    public void setIslandStatus(IslandStatus islandStatus) {
        this.islandStatus = islandStatus;
    }

    public static enum IslandStatus {
        ALIVE,
        BED_BROKEN,
        LOSER;

    }
}

