/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.permissions.Permissible
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginManager
 */
package br.com.dragonmc.core.bukkit.utils.permission.injector.regexperms;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicReference;
import br.com.dragonmc.core.bukkit.utils.permission.injector.FieldReplacer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class PEXPermissionSubscriptionMap
extends HashMap<String, Map<Permissible, Boolean>> {
    private static final long serialVersionUID = 1L;
    private static FieldReplacer<PluginManager, Map> INJECTOR;
    private static final AtomicReference<PEXPermissionSubscriptionMap> INSTANCE;
    private final Plugin plugin;
    private final PluginManager manager;

    private PEXPermissionSubscriptionMap(Plugin plugin, PluginManager manager, Map<String, Map<Permissible, Boolean>> backing) {
        super(backing);
        this.plugin = plugin;
        this.manager = manager;
    }

    public static PEXPermissionSubscriptionMap inject(Plugin plugin, PluginManager manager) {
        Map backing;
        PEXPermissionSubscriptionMap map = INSTANCE.get();
        if (map != null) {
            return map;
        }
        if (INJECTOR == null) {
            INJECTOR = new FieldReplacer(manager.getClass(), "permSubs", Map.class);
        }
        if ((backing = INJECTOR.get(manager)) instanceof PEXPermissionSubscriptionMap) {
            return (PEXPermissionSubscriptionMap)backing;
        }
        PEXPermissionSubscriptionMap wrappedMap = new PEXPermissionSubscriptionMap(plugin, manager, backing);
        if (INSTANCE.compareAndSet(null, wrappedMap)) {
            INJECTOR.set(manager, wrappedMap);
            return wrappedMap;
        }
        return INSTANCE.get();
    }

    public void uninject() {
        if (INSTANCE.compareAndSet(this, null)) {
            HashMap unwrappedMap = new HashMap(this.size());
            for (Entry entry : this.entrySet()) {
                if (!(entry.getValue() instanceof PEXSubscriptionValueMap)) continue;
                unwrappedMap.put(entry.getKey(), ((PEXSubscriptionValueMap)entry.getValue()).backing);
            }
            INJECTOR.set(this.manager, unwrappedMap);
        }
    }

    @Override
    public Map<Permissible, Boolean> get(Object key) {
        if (key == null) {
            return null;
        }
        Map result = (Map)super.get(key);
        if (result == null) {
            result = new PEXSubscriptionValueMap((String)key, new WeakHashMap<Permissible, Boolean>());
            super.put((String)key, result);
        } else if (!(result instanceof PEXSubscriptionValueMap)) {
            result = new PEXSubscriptionValueMap((String)key, result);
            super.put((String)key, result);
        }
        return result;
    }

    @Override
    public Map<Permissible, Boolean> put(String key, Map<Permissible, Boolean> value) {
        if (!(value instanceof PEXSubscriptionValueMap)) {
            value = new PEXSubscriptionValueMap(key, value);
        }
        return super.put(key, value);
    }

    static {
        INSTANCE = new AtomicReference();
    }

    public class PEXSubscriptionValueMap
    implements Map<Permissible, Boolean> {
        private final String permission;
        private final Map<Permissible, Boolean> backing;

        public PEXSubscriptionValueMap(String permission, Map<Permissible, Boolean> backing) {
            this.permission = permission;
            this.backing = backing;
        }

        @Override
        public int size() {
            return this.backing.size();
        }

        @Override
        public boolean isEmpty() {
            return this.backing.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            return this.backing.containsKey(key) || key instanceof Permissible && ((Permissible)key).isPermissionSet(this.permission);
        }

        @Override
        public boolean containsValue(Object value) {
            return this.backing.containsValue(value);
        }

        @Override
        public Boolean put(Permissible key, Boolean value) {
            return this.backing.put(key, value);
        }

        @Override
        public Boolean remove(Object key) {
            return this.backing.remove(key);
        }

        @Override
        public void putAll(Map<? extends Permissible, ? extends Boolean> m) {
            this.backing.putAll(m);
        }

        @Override
        public void clear() {
            this.backing.clear();
        }

        @Override
        public Boolean get(Object key) {
            Permissible p;
            if (key instanceof Permissible && (p = (Permissible)key).isPermissionSet(this.permission)) {
                return p.hasPermission(this.permission);
            }
            return this.backing.get(key);
        }

        @Override
        public Set<Permissible> keySet() {
            HashSet<Player> pexMatches = new HashSet<Player>(PEXPermissionSubscriptionMap.this.plugin.getServer().getOnlinePlayers().size());
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.hasPermission(this.permission)) continue;
                pexMatches.add(player);
            }
            return Sets.union(pexMatches, this.backing.keySet());
        }

        @Override
        public Collection<Boolean> values() {
            return this.backing.values();
        }

        @Override
        public Set<Entry<Permissible, Boolean>> entrySet() {
            return this.backing.entrySet();
        }
    }
}

