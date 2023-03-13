/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType$Play$Server
 *  com.comphenix.protocol.ProtocolLibrary
 *  com.comphenix.protocol.events.PacketContainer
 *  com.comphenix.protocol.utility.MinecraftReflection
 *  com.comphenix.protocol.wrappers.WrappedChatComponent
 *  com.google.common.collect.ImmutableList
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.inventory.InventoryType
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 */
package br.com.dragonmc.core.bukkit.utils.menu;

import br.com.dragonmc.core.bukkit.event.player.PlayerOpenInventoryEvent;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.common.collect.ImmutableList;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import br.com.dragonmc.core.bukkit.utils.menu.click.MenuClickHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class MenuInventory {
    private int rows;
    private InventoryType inventoryType = InventoryType.CHEST;
    private String title;
    private Inventory inventory;
    private boolean onePerPlayer;
    private Map<Integer, MenuItem> slotItem;
    private MenuUpdateHandler updateHandler;
    private MenuCloseHandler closeHandler;
    private boolean reopenInventory = false;
    private static Map<String, Long> openDelay = new HashMap<String, Long>();

    public MenuInventory(String title, int rows) {
        this(title, rows, InventoryType.CHEST, false);
    }

    public MenuInventory(String title, InventoryType inventoryType) {
        this(title, 3, inventoryType, false);
    }

    public MenuInventory(String title, int rows, InventoryType inventoryType, boolean onePerPlayer) {
        this.rows = rows;
        this.inventoryType = inventoryType;
        this.slotItem = new HashMap<Integer, MenuItem>();
        this.title = title;
        this.onePerPlayer = onePerPlayer;
        if (!onePerPlayer) {
            this.inventory = Bukkit.createInventory((InventoryHolder)new MenuHolder(this), (int)(rows * 9), (String)"");
        }
    }

    public void addItem(MenuItem item) {
        this.setItem(this.firstEmpty(), item);
    }

    public void addItem(ItemStack item) {
        this.setItem(this.firstEmpty(), item);
    }

    public void addItem(ItemStack item, MenuClickHandler handler) {
        this.setItem(this.firstEmpty(), item, handler);
    }

    public void setItem(ItemStack item, int slot) {
        this.setItem(slot, new MenuItem(item));
    }

    public void setItem(int slot, ItemStack item) {
        this.setItem(slot, new MenuItem(item));
    }

    public void setItem(int slot, ItemStack item, MenuClickHandler handler) {
        this.setItem(slot, new MenuItem(item, handler));
    }

    public void setItem(MenuItem item, int slot) {
        this.setItem(slot, item);
    }

    public void removeItem(int slot) {
        this.slotItem.remove(slot);
        if (!this.onePerPlayer) {
            this.inventory.setItem(slot, new ItemStack(Material.AIR));
        }
    }

    public void setItem(int slot, MenuItem item) {
        this.slotItem.put(slot, item);
        if (!this.onePerPlayer) {
            this.inventory.setItem(slot, item.getStack());
        }
    }

    public void setRows(int rows) {
        this.rows = rows;
        if (!this.onePerPlayer) {
            ImmutableList copyOf = ImmutableList.copyOf(this.slotItem.entrySet());
            this.inventory = Bukkit.createInventory((InventoryHolder)new MenuHolder(this), (int)(rows * 9), (String)"");
            this.slotItem.clear();
            for (Map.Entry<Integer, MenuItem> item : (Iterable<Map.Entry<Integer, MenuItem>>)copyOf)
                this.setItem((int)((Integer)item.getKey()), (MenuItem)item.getValue());
            }
        }


    public int firstEmpty() {
        if (!this.onePerPlayer) {
            return this.inventory.firstEmpty();
        }
        for (int i = 0; i < this.rows * 9; ++i) {
            if (this.slotItem.containsKey(i)) continue;
            return i;
        }
        return -1;
    }

    public boolean hasItem(int slot) {
        return this.slotItem.containsKey(slot);
    }

    public MenuItem getItem(int slot) {
        return this.slotItem.get(slot);
    }

    public void clear() {
        this.slotItem.clear();
        if (!this.onePerPlayer) {
            this.inventory.clear();
        }
    }

    public void open(Player p) {
        if (this.isCooldown(p.getName())) {
            return;
        }
        if (this.onePerPlayer) {
            if (p.getOpenInventory() == null || p.getOpenInventory().getTopInventory().getType() != this.inventoryType || p.getOpenInventory().getTopInventory().getSize() != this.rows * 9 || p.getOpenInventory().getTopInventory().getHolder() == null || !(p.getOpenInventory().getTopInventory().getHolder() instanceof MenuHolder) || !((MenuHolder)p.getOpenInventory().getTopInventory().getHolder()).isOnePerPlayer()) {
                this.createAndOpenInventory(p);
            } else {
                Inventory topInventory = p.getOpenInventory().getTopInventory();
                for (int i = 0; i < this.rows * 9; ++i) {
                    if (this.slotItem.containsKey(i)) {
                        ItemStack oldItem = topInventory.getItem(i);
                        ItemStack newItem = this.slotItem.get(i).getStack();
                        if (oldItem == null || newItem == null) {
                            topInventory.setItem(i, newItem);
                            continue;
                        }
                        if (oldItem.getType() == newItem.getType() && oldItem.getDurability() == newItem.getDurability() && oldItem.getAmount() == newItem.getAmount()) continue;
                        topInventory.setItem(i, newItem);
                        continue;
                    }
                    topInventory.setItem(i, null);
                }
                if (!topInventory.getName().equals(this.getTitle())) {
                    this.updateTitle(p);
                }
            }
            Bukkit.getPluginManager().callEvent((Event)new PlayerOpenInventoryEvent(p, p.getOpenInventory().getTopInventory()));
            ((MenuHolder)p.getOpenInventory().getTopInventory().getHolder()).setMenu(this);
        } else {
            p.openInventory(this.inventory);
            this.updateTitle(p);
            Bukkit.getPluginManager().callEvent((Event)new PlayerOpenInventoryEvent(p, this.inventory));
        }
        this.setCooldown(p.getName());
    }

    public void updateSlot(Player player, int slot) {
        if (this.slotItem.containsKey(slot)) {
            player.getOpenInventory().getTopInventory().setItem(slot, this.slotItem.get(slot).getStack());
        } else {
            player.getOpenInventory().getTopInventory().setItem(slot, null);
        }
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void updateTitle(Player p) {
        try {
            PacketContainer packet = new PacketContainer(PacketType.Play.Server.OPEN_WINDOW);
            packet.getChatComponents().write(0, WrappedChatComponent.fromText((String)this.title));
            Method getHandle = MinecraftReflection.getCraftPlayerClass().getMethod("getHandle", new Class[0]);
            Object entityPlayer = getHandle.invoke(p, new Object[0]);
            Field activeContainerField = entityPlayer.getClass().getField("activeContainer");
            Object activeContainer = activeContainerField.get(entityPlayer);
            Field windowIdField = activeContainer.getClass().getField("windowId");
            int id = windowIdField.getInt(activeContainer);
            packet.getStrings().write(0, ("minecraft:" + this.inventoryType.name().toLowerCase()));
            packet.getIntegers().write(0, id);
            packet.getIntegers().write(1, (this.rows * 9));
            ProtocolLibrary.getProtocolManager().sendServerPacket(p, packet);
            p.updateInventory();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createAndOpenInventory(Player p) {
        Inventory playerInventory = this.inventoryType == InventoryType.CHEST ? Bukkit.createInventory((InventoryHolder)new MenuHolder(this), (int)(this.rows * 9), (String)this.title) : Bukkit.createInventory((InventoryHolder)new MenuHolder(this), (InventoryType)this.inventoryType, (String)this.title);
        for (Map.Entry<Integer, MenuItem> entry : this.slotItem.entrySet()) {
            playerInventory.setItem(entry.getKey().intValue(), entry.getValue().getStack());
        }
        p.openInventory(playerInventory);
    }

    public void close(Player p) {
        if (this.onePerPlayer) {
            this.destroy(p);
            p = null;
        }
    }

    public void onClose(Player player) {
        if (this.closeHandler != null) {
            this.closeHandler.onClose(player);
        }
    }

    public void destroy(Player p) {
        if (p.getOpenInventory().getTopInventory().getHolder() != null && p.getOpenInventory().getTopInventory().getHolder() instanceof MenuHolder) {
            ((MenuHolder)p.getOpenInventory().getTopInventory().getHolder()).destroy();
        }
    }

    public boolean isCooldown(String playerName) {
        return openDelay.containsKey(playerName) && openDelay.get(playerName) > System.currentTimeMillis();
    }

    public void setCooldown(String playerName) {
        openDelay.put(playerName, System.currentTimeMillis() + 200L);
    }

    public int getRows() {
        return this.rows;
    }

    public InventoryType getInventoryType() {
        return this.inventoryType;
    }

    public String getTitle() {
        return this.title;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public boolean isOnePerPlayer() {
        return this.onePerPlayer;
    }

    public Map<Integer, MenuItem> getSlotItem() {
        return this.slotItem;
    }

    public MenuUpdateHandler getUpdateHandler() {
        return this.updateHandler;
    }

    public MenuCloseHandler getCloseHandler() {
        return this.closeHandler;
    }

    public void setUpdateHandler(MenuUpdateHandler updateHandler) {
        this.updateHandler = updateHandler;
    }

    public void setCloseHandler(MenuCloseHandler closeHandler) {
        this.closeHandler = closeHandler;
    }

    public boolean isReopenInventory() {
        return this.reopenInventory;
    }

    public void setReopenInventory(boolean reopenInventory) {
        this.reopenInventory = reopenInventory;
    }
}

