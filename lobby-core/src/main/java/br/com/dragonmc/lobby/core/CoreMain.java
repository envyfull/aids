/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.plugin.Plugin
 */
package br.com.dragonmc.lobby.core;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.bukkit.BukkitCommon;
import br.com.dragonmc.core.bukkit.event.UpdateEvent;
import br.com.dragonmc.lobby.core.manager.GamerManager;
import br.com.dragonmc.lobby.core.manager.ServerWatcherManager;
import br.com.dragonmc.lobby.core.listener.PlayerListener;
import br.com.dragonmc.lobby.core.listener.ServerListener;
import br.com.dragonmc.lobby.core.listener.WorldListener;
import br.com.dragonmc.lobby.core.menu.CosmeticsInventory;
import br.com.dragonmc.lobby.core.menu.LobbyInventory;
import br.com.dragonmc.lobby.core.menu.ServerInventory;
import br.com.dragonmc.core.bukkit.menu.profile.ProfileInventory;
import br.com.dragonmc.core.bukkit.utils.character.Character;
import br.com.dragonmc.core.bukkit.utils.character.NPC;
import br.com.dragonmc.core.bukkit.utils.character.handler.ActionHandler;
import br.com.dragonmc.core.bukkit.utils.helper.HologramHelper;
import br.com.dragonmc.core.bukkit.utils.hologram.Hologram;
import br.com.dragonmc.core.bukkit.utils.item.ActionItemStack;
import br.com.dragonmc.core.bukkit.utils.item.ItemBuilder;
import br.com.dragonmc.core.common.server.ServerType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public abstract class CoreMain
extends BukkitCommon
implements Listener {
    private static CoreMain instance;
    private GamerManager gamerManager;
    private ServerWatcherManager serverWatcherManager;
    private PlayerInventory playerInventory;
    private ActionItemStack lobbiesItem;
    private ActionItemStack gamesItem;
    private ActionItemStack vanishItem;
    private ActionItemStack wadgetsItem;
    public List<CharacterInfo> characterInfos = new ArrayList<CharacterInfo>();
    private int lobbyId = 1;

    @Override
    public void onLoad() {
        super.onLoad();
        this.setServerLog(true);
        instance = this;
        this.serverWatcherManager = new ServerWatcherManager();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.gamerManager = new GamerManager();
        this.lobbyId = this.getServerId(CommonPlugin.getInstance().getServerId());
        this.setPlayerInventory(player -> {
            player.getInventory().setItem(0, this.gamesItem.getItemStack());
            player.getInventory().setItem(1, ActionItemStack.create(new ItemBuilder().name("\u00a7a\u00a7%item.your-profile%\u00a7").type(Material.SKULL_ITEM).durability(3).skin(player.getName()).build(), new ActionItemStack.Interact(){

                @Override
                public boolean onInteract(Player player, Entity entity, Block block, ItemStack item, ActionItemStack.ActionType action) {
                    new ProfileInventory(player);
                    return false;
                }
            }.setInventoryClick(true)).getItemStack());
            player.getInventory().setItem(4, this.wadgetsItem.getItemStack());
            player.getInventory().setItem(7, this.vanishItem.getItemStack());
            player.getInventory().setItem(8, this.lobbiesItem.getItemStack());
        });
        this.gamesItem = ActionItemStack.create(new ItemBuilder().name("\u00a7a\u00a7%item.select-server%\u00a7").type(Material.COMPASS).build(), new ActionItemStack.Interact(){

            @Override
            public boolean onInteract(Player player, Entity entity, Block block, ItemStack item, ActionItemStack.ActionType action) {
                new ServerInventory(player);
                return true;
            }
        }.setInventoryClick(true));
        this.wadgetsItem = ActionItemStack.create(new ItemBuilder().name("\u00a7aCosm\u00e9ticos").type(Material.CHEST).build(), new ActionItemStack.Interact(){

            @Override
            public boolean onInteract(Player player, Entity entity, Block block, ItemStack item, ActionItemStack.ActionType action) {
                new CosmeticsInventory(player);
                return true;
            }
        }.setInventoryClick(true));
        this.lobbiesItem = ActionItemStack.create(new ItemBuilder().name("\u00a7a\u00a7%item.select-lobby%\u00a7").type(Material.NETHER_STAR).build(), new ActionItemStack.Interact(){

            @Override
            public boolean onInteract(Player player, Entity entity, Block block, ItemStack item, ActionItemStack.ActionType action) {
                new LobbyInventory(player);
                return true;
            }
        }.setInventoryClick(true));
        this.vanishItem = ActionItemStack.create(new ItemBuilder().name("\u00a7fPlayers: \u00a7aVis\u00edveis").type(Material.INK_SACK).durability(10).build(), new ActionItemStack.Interact(){

            @Override
            public boolean onInteract(Player player, Entity entity, Block block, ItemStack item, ActionItemStack.ActionType action) {
                ItemMeta itemMeta = item.getItemMeta();
                boolean visible = itemMeta.getDisplayName().contains("\u00a7a");
                item.setDurability(visible ? (short)8 : 10);
                itemMeta.setDisplayName(visible ? "\u00a7fPlayers: \u00a7cInvis\u00edveis" : "\u00a7fPlayers: \u00a7aVis\u00edveis");
                item.setItemMeta(itemMeta);
                if (visible) {
                    // empty if block
                }
                player.sendMessage(visible ? "\u00a7cVoc\u00ea agora n\u00e3o est\u00e1 mais vendo os jogadores" : "\u00a7aVoc\u00ea agora est\u00e1 vendo os jogadores");
                return true;
            }
        }.setInventoryClick(true));
        Bukkit.getPluginManager().registerEvents((Listener)new ServerListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new PlayerListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new WorldListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (event.getCurrentTick() % 60L == 0L) {
            this.characterInfos.forEach(info -> {
                Hologram firstLine = info.getHologram().getLines().stream().findFirst().orElse(null);
                int players = 0;
                for (ServerType type : info.getTypes()) {
                    players += this.getServerManager().getBalancer(type).getTotalNumber();
                }
                firstLine.setDisplayName("\u00a7e" + players + " jogando.");
            });
        }
    }

    public int getServerId(String serverId) {
        Pattern pattern = Pattern.compile("^([a-zA-Z]\\d+)\\..*");
        Matcher matcher = pattern.matcher(serverId);
        if (matcher.find()) {
            return Integer.valueOf(matcher.group(1).replaceAll("[^\\d]", ""));
        }
        return 1;
    }

    public Hologram createCharacter(String location, String playerName, ActionHandler interact, List<ServerType> serverList, String ... text) {
        Location loc = this.getLocationManager().getLocation(location);
        NPC citizen = new NPC(loc, playerName);
        Character character = new Character(citizen, interact);
        Hologram hologram = HologramHelper.createHologram(text[0], loc.add(0.0, 0.15, 0.0));
        int players = 0;
        for (ServerType type : serverList) {
            players += this.getServerManager().getBalancer(type).getTotalNumber();
        }
        hologram.line("\u00a7e" + players + " jogando.");
        for (int i = 1; i < text.length; ++i) {
            hologram.line(text[i]);
        }
        Bukkit.getOnlinePlayers().forEach(player -> character.show((Player)player));
        this.characterInfos.add(new CharacterInfo(character, hologram, serverList));
        return hologram;
    }

    public Hologram createCharacter(String location, String playerName, ActionHandler interact, String ... text) {
        return this.createCharacter(location, playerName, interact, new ArrayList<ServerType>(), text);
    }

    public GamerManager getGamerManager() {
        return this.gamerManager;
    }

    public ServerWatcherManager getServerWatcherManager() {
        return this.serverWatcherManager;
    }

    public PlayerInventory getPlayerInventory() {
        return this.playerInventory;
    }

    public ActionItemStack getLobbiesItem() {
        return this.lobbiesItem;
    }

    public ActionItemStack getGamesItem() {
        return this.gamesItem;
    }

    public ActionItemStack getVanishItem() {
        return this.vanishItem;
    }

    public ActionItemStack getWadgetsItem() {
        return this.wadgetsItem;
    }

    public List<CharacterInfo> getCharacterInfos() {
        return this.characterInfos;
    }

    public int getLobbyId() {
        return this.lobbyId;
    }

    public static CoreMain getInstance() {
        return instance;
    }

    public void setPlayerInventory(PlayerInventory playerInventory) {
        this.playerInventory = playerInventory;
    }

    public static interface PlayerInventory {
        public void handle(Player var1);
    }

    public class CharacterInfo {
        private Character character;
        private Hologram hologram;
        private List<ServerType> types;

        public CharacterInfo(Character character, Hologram hologram, List<ServerType> types) {
            this.character = character;
            this.hologram = hologram;
            this.types = types;
        }

        public Character getCharacter() {
            return this.character;
        }

        public Hologram getHologram() {
            return this.hologram;
        }

        public List<ServerType> getTypes() {
            return this.types;
        }
    }
}

