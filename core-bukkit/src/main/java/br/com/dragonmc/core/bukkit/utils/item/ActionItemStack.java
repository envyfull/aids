/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.utility.MinecraftReflection
 *  com.comphenix.protocol.wrappers.nbt.NbtCompound
 *  com.comphenix.protocol.wrappers.nbt.NbtFactory
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.HandlerList
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.block.BlockBreakEvent
 *  org.bukkit.event.block.BlockPlaceEvent
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.player.PlayerInteractEntityEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.metadata.FixedMetadataValue
 *  org.bukkit.metadata.MetadataValue
 *  org.bukkit.plugin.Plugin
 */
package br.com.dragonmc.core.bukkit.utils.item;

import br.com.dragonmc.core.bukkit.BukkitCommon;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class ActionItemStack {
    private static final Map<Integer, Interact> HANDLERS = new HashMap<Integer, Interact>();
    private static Listener listener;
    private Interact interactHandler;
    private ItemStack itemStack;

    public ActionItemStack(ItemStack stack, Interact handler) {
        this.itemStack = ActionItemStack.setTag(stack, ActionItemStack.registerHandler(handler));
        if (this.itemStack == null) {
            this.itemStack = stack;
        }
        this.interactHandler = handler;
    }

    public static int registerHandler(Interact handler) {
        if (HANDLERS.containsValue(handler)) {
            return HANDLERS.entrySet().stream().filter(entry -> entry.getValue() == handler).map(Map.Entry::getKey).findFirst().orElse(-1);
        }
        handler.setHandler(HANDLERS.size() + 1);
        HANDLERS.put(HANDLERS.size() + 1, handler);
        if (listener == null) {
            listener = new ActionItemListener();
            Bukkit.getPluginManager().registerEvents(listener, (Plugin) BukkitCommon.getInstance());
        }
        return HANDLERS.size();
    }

    public static void unregisterHandler(Integer id) {
        HANDLERS.remove(id);
        if (listener != null && HANDLERS.isEmpty()) {
            HandlerList.unregisterAll((Listener)listener);
            listener = null;
        }
    }

    public static void unregisterHandler(Interact handler) {
        Iterator<Map.Entry<Integer, Interact>> iterator = HANDLERS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Interact> entry = iterator.next();
            if (entry.getValue() != handler) continue;
            iterator.remove();
            break;
        }
    }

    public static Interact getHandler(Integer id) {
        return HANDLERS.get(id);
    }

    public static ItemStack setTag(ItemStack stack, int id) {
        try {
            if (stack == null || stack.getType() == Material.AIR) {
                throw new Exception();
            }
            Constructor caller = MinecraftReflection.getCraftItemStackClass().getDeclaredConstructor(ItemStack.class);
            caller.setAccessible(true);
            ItemStack item = (ItemStack)caller.newInstance(stack);
            NbtCompound compound = (NbtCompound)NbtFactory.fromItemTag((ItemStack)item);
            compound.put("interactHandler", id);
            return item;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ActionItemStack create(ItemStack stack, Interact handler) {
        return new ActionItemStack(stack, handler);
    }

    public Interact getInteractHandler() {
        return this.interactHandler;
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    private static class ActionItemListener
    implements Listener {
        private ActionItemListener() {
        }

        @EventHandler
        public void onPlayerInteract(PlayerInteractEvent event) {
            if (event.getItem() == null || event.getItem().getType() == Material.AIR) {
                return;
            }
            ItemStack stack = event.getItem();
            try {
                NbtCompound compound = this.getNbtCompound(stack);
                if (compound.containsKey("interactHandler")) {
                    Interact handler = ActionItemStack.getHandler(compound.getInteger("interactHandler"));
                    if (handler == null || handler.getInteractType() == InteractType.PLAYER) {
                        return;
                    }
                    Player player = event.getPlayer();
                    Action action = event.getAction();
                    event.setCancelled(handler.onInteract(player, null, event.getClickedBlock(), stack, action.name().contains("RIGHT") ? ActionType.RIGHT : ActionType.LEFT));
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        @EventHandler
        public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
            if (event.getPlayer().getItemInHand() == null || event.getPlayer().getItemInHand().getType() == Material.AIR) {
                return;
            }
            ItemStack stack = event.getPlayer().getItemInHand();
            try {
                NbtCompound compound = this.getNbtCompound(stack);
                if (compound.containsKey("interactHandler")) {
                    Interact handler = ActionItemStack.getHandler(compound.getInteger("interactHandler"));
                    if (handler == null || handler.getInteractType() == InteractType.CLICK) {
                        return;
                    }
                    Player player = event.getPlayer();
                    event.setCancelled(handler.onInteract(player, event.getRightClicked(), null, stack, ActionType.CLICK_PLAYER));
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
        public void onInventoryClick(InventoryClickEvent event) {
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
                return;
            }
            ItemStack stack = event.getCurrentItem();
            try {
                NbtCompound compound = this.getNbtCompound(stack);
                if (compound.containsKey("interactHandler")) {
                    Interact handler = ActionItemStack.getHandler(compound.getInteger("interactHandler"));
                    if (handler == null) {
                        compound.remove("interactHandler");
                        return;
                    }
                    if (handler.getInteractType() == InteractType.PLAYER || !handler.isInventoryClick()) {
                        return;
                    }
                    Player player = (Player)event.getWhoClicked();
                    event.setCancelled(handler.onInteract(player, null, null, stack, ActionType.LEFT));
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
        public void onBlockPlace(BlockPlaceEvent event) {
            if (event.getItemInHand() == null || event.getItemInHand().getType() == Material.AIR) {
                return;
            }
            ItemStack stack = event.getItemInHand();
            try {
                NbtCompound compound = this.getNbtCompound(stack);
                if (compound.containsKey("interactHandler")) {
                    Block b = event.getBlock();
                    int id = compound.getInteger("interactHandler");
                    b.setMetadata("interactHandler", (MetadataValue)new FixedMetadataValue((Plugin)BukkitCommon.getInstance(), (Object)id));
                    b.getDrops().clear();
                    b.getDrops().add(ActionItemStack.setTag(new ItemStack(event.getBlock().getType(), 1, (short)event.getBlock().getData()), id));
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
        public void onBlockBreak(BlockBreakEvent event) {
            Block b = event.getBlock();
            if (!b.hasMetadata("interactHandler")) {
                return;
            }
            b.getDrops().clear();
            b.getDrops().add(ActionItemStack.setTag(new ItemStack(event.getBlock().getType(), 1, (short)event.getBlock().getData()), ((MetadataValue)b.getMetadata("interactHandler").get(0)).asInt()));
        }

        private NbtCompound getNbtCompound(ItemStack stack) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            Constructor caller = MinecraftReflection.getCraftItemStackClass().getDeclaredConstructor(ItemStack.class);
            caller.setAccessible(true);
            ItemStack item = (ItemStack)caller.newInstance(stack);
            NbtCompound compound = (NbtCompound)NbtFactory.fromItemTag((ItemStack)item);
            return compound;
        }
    }

    public static enum InteractType {
        PLAYER,
        CLICK;

    }

    public static enum ActionType {
        CLICK_PLAYER,
        RIGHT,
        LEFT;

    }

    public static abstract class Interact {
        private InteractType interactType;
        private boolean inventoryClick;
        private int handler;

        public Interact() {
            this.interactType = InteractType.CLICK;
        }

        public Interact setHandler(int handler) {
            this.handler = handler;
            return this;
        }

        public Interact(InteractType interactType) {
            this.interactType = interactType;
        }

        public Interact setInventoryClick(boolean inventoryClick) {
            this.inventoryClick = inventoryClick;
            return this;
        }

        public abstract boolean onInteract(Player var1, Entity var2, Block var3, ItemStack var4, ActionType var5);

        public InteractType getInteractType() {
            return this.interactType;
        }

        public boolean isInventoryClick() {
            return this.inventoryClick;
        }

        public int getHandler() {
            return this.handler;
        }
    }
}

