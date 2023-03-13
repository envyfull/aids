/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 */
package br.com.dragonmc.lobby.main;

import java.util.Arrays;

import br.com.dragonmc.lobby.main.listener.ScoreboardListener;
import br.com.dragonmc.lobby.core.CoreMain;
import br.com.dragonmc.lobby.core.menu.RankupInventory;
import br.com.dragonmc.core.bukkit.utils.character.handler.ActionHandler;
import br.com.dragonmc.core.common.server.ServerType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class LobbyMain
extends CoreMain {
    private static LobbyMain instance;

    @Override
    public void onEnable() {
        instance = this;
        super.onEnable();
        Bukkit.getPluginManager().registerEvents((Listener)new ScoreboardListener(), (Plugin)this);
        this.createCharacter("npc-hg", "AnjooGaming", new ActionHandler(){

            @Override
            public boolean onInteract(Player player, boolean right) {
                LobbyMain.this.sendPlayerToServer(player, ServerType.HG_LOBBY);
                return false;
            }
        }, Arrays.asList(ServerType.HG, ServerType.HG_LOBBY), "\u00a7bHG");
        this.createCharacter("npc-pvp", "Budokkan", new ActionHandler(){

            @Override
            public boolean onInteract(Player player, boolean right) {
                LobbyMain.this.sendPlayerToServer(player, ServerType.PVP_LOBBY);
                return false;
            }
        }, Arrays.asList(ServerType.PVP_LOBBY, ServerType.ARENA, ServerType.FPS, ServerType.LAVA), "\u00a7bPvP");
        this.createCharacter("npc-duels", "stopeey", new ActionHandler(){

            @Override
            public boolean onInteract(Player player, boolean right) {
                LobbyMain.this.sendPlayerToServer(player, ServerType.DUELS);
                return false;
            }
        }, Arrays.asList(ServerType.DUELS), "\u00a7bTreino");
        this.createCharacter("npc-bedwars", "Abodicom4You", new ActionHandler(){

            @Override
            public boolean onInteract(Player player, boolean right) {
                LobbyMain.this.sendPlayerToServer(player, ServerType.BW_LOBBY);
                return false;
            }
        }, Arrays.asList(ServerType.BW_LOBBY, ServerType.BW_SOLO, ServerType.BW_DUOS, ServerType.BW_TRIO, ServerType.BW_SQUAD, ServerType.BW_1X1, ServerType.BW_2X2), "\u00a7bBedwars");
        this.createCharacter("npc-skywars", "Jauaum", new ActionHandler(){

            @Override
            public boolean onInteract(Player player, boolean right) {
                LobbyMain.this.sendPlayerToServer(player, ServerType.SW_LOBBY);
                return false;
            }
        }, Arrays.asList(ServerType.SW_LOBBY, ServerType.SW_SOLO, ServerType.SW_DUOS, ServerType.SW_SQUAD), "\u00a7bSkywars");
        this.createCharacter("npc-rankup", "LouixZ", new ActionHandler(){

            @Override
            public boolean onInteract(Player player, boolean right) {
                new RankupInventory(player);
                return false;
            }
        }, Arrays.asList(ServerType.RANKUP), "\u00a7bRankup");
    }

    public static LobbyMain getInstance() {
        return instance;
    }
}

