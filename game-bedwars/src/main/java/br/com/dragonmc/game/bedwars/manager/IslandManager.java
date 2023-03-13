/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 */
package br.com.dragonmc.game.bedwars.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import br.com.dragonmc.game.engine.GameAPI;
import br.com.dragonmc.game.bedwars.GameMain;
import br.com.dragonmc.game.bedwars.gamer.Gamer;
import br.com.dragonmc.game.bedwars.generator.Generator;
import br.com.dragonmc.game.bedwars.island.Island;
import br.com.dragonmc.game.bedwars.island.IslandColor;
import br.com.dragonmc.game.engine.gamer.Team;
import br.com.dragonmc.core.common.member.party.Party;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class IslandManager {
    private Map<IslandColor, Island> islandMap = new HashMap<IslandColor, Island>();
    private Map<UUID, IslandColor> playerMap = new HashMap<UUID, IslandColor>();

    public Collection<Island> loadIsland() {
        ArrayList<Island> islandList = new ArrayList<Island>(GameMain.getInstance().getConfiguration().getList("islands", Island.class));
        List<Gamer> playerList = GameAPI.getInstance().getGamerManager().getGamers(Gamer.class).stream().filter(gamer -> gamer.isAlive() && gamer.getPlayer() != null).sorted((o1, o2) -> {
            Party o1Party = GameAPI.getInstance().getPlugin().getPartyManager().getPartyById(o1.getUniqueId());
            Party o2Party = GameAPI.getInstance().getPlugin().getPartyManager().getPartyById(o2.getUniqueId());
            if (o1Party == null || o2Party == null) {
                return 0;
            }
            return o1Party.getPartyId().compareTo(o2Party.getPartyId());
        }).collect(Collectors.toList());
        ArrayList<Team> teamList = new ArrayList<Team>();
        for (int i = 0; i < GameMain.getInstance().getMaxTeams(); ++i) {
            teamList.add(new Team(i, GameMain.getInstance().getPlayersPerTeam()));
        }
        boolean stop = true;
        for (Team team : teamList) {
            Island island;
            if (team.isFull()) continue;
            for (int i = 0; i < GameMain.getInstance().getPlayersPerTeam(); ++i) {
                Gamer player = playerList.stream().findFirst().orElse(null);
                if (player == null) continue;
                playerList.remove(player);
                team.addPlayer(player.getUniqueId());
            }
            if (team.getPlayerSet().size() >= 1) {
                stop = false;
            }
            if ((island = (Island)islandList.stream().findAny().orElse(null)) == null) {
                team.getPlayerSet().stream().map(id -> GameAPI.getInstance().getGamerManager().getGamer((UUID)id)).forEach(gamer -> {
                    if (gamer.getPlayer() != null) {
                        gamer.getPlayer().kickPlayer("\u00a7%bedwars.kick.island-not-found%\u00a7");
                    }
                    GameAPI.getInstance().getGamerManager().unloadGamer(gamer.getUniqueId());
                });
                continue;
            }
            island.loadIsland(team);
            island.getTeam().getPlayerSet().forEach(id -> this.playerMap.put((UUID)id, island.getIslandColor()));
            this.islandMap.put(island.getIslandColor(), island);
            islandList.remove(island);
        }
        if (stop) {
            Bukkit.shutdown();
        }
        return this.islandMap.values();
    }

    public Island getIsland(IslandColor islandColor) {
        return this.islandMap.get((Object)islandColor);
    }

    public Island getIsland(UUID uniqueId) {
        return this.playerMap.containsKey(uniqueId) ? this.islandMap.get((Object)this.playerMap.get(uniqueId)) : null;
    }

    public Collection<Island> values() {
        return this.islandMap.values();
    }

    public Island getClosestIsland(Location location) {
        return this.islandMap.values().stream().sorted((o1, o2) -> (int)(o1.getSpawnLocation().getAsLocation().distance(location) - o2.getSpawnLocation().getAsLocation().distance(location))).findFirst().orElse(null);
    }

    public Collection<Island> getIslands() {
        return this.islandMap.values();
    }

    public Optional<Generator> getClosestGenerator(Location location) {
        return GameMain.getInstance().getIslandManager().getClosestIsland(location).getIslandGenerators().stream().findFirst();
    }
}

