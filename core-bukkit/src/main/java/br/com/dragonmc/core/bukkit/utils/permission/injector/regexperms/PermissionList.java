/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Multimaps
 *  org.bukkit.permissions.Permission
 *  org.bukkit.plugin.PluginManager
 */
package br.com.dragonmc.core.bukkit.utils.permission.injector.regexperms;

import br.com.dragonmc.core.bukkit.utils.permission.injector.FieldReplacer;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;

public class PermissionList
extends HashMap<String, Permission> {
    private static final long serialVersionUID = 1L;
    private static FieldReplacer<PluginManager, Map> INJECTOR;
    private static final Map<Class<?>, FieldReplacer<Permission, Map>> CHILDREN_MAPS;
    private final Multimap<String, Entry<String, Boolean>> childParentMapping = Multimaps.synchronizedMultimap((Multimap)HashMultimap.create());

    public PermissionList() {
    }

    public PermissionList(Map<? extends String, ? extends Permission> existing) {
        super(existing);
    }

    private FieldReplacer<Permission, Map> getFieldReplacer(Permission perm) {
        FieldReplacer<Permission, Map> ret = CHILDREN_MAPS.get(perm.getClass());
        if (ret == null) {
            ret = new FieldReplacer(perm.getClass(), "children", Map.class);
            CHILDREN_MAPS.put(perm.getClass(), ret);
        }
        return ret;
    }

    private void removeAllChildren(String perm) {
        Iterator it = this.childParentMapping.entries().iterator();
        while (it.hasNext()) {
            if (!((String)((Entry)((Entry)it.next()).getValue()).getKey()).equals(perm)) continue;
            it.remove();
        }
    }

    public static PermissionList inject(PluginManager manager) {
        if (INJECTOR == null) {
            INJECTOR = new FieldReplacer(manager.getClass(), "permissions", Map.class);
        }
        Map existing = INJECTOR.get(manager);
        PermissionList list = new PermissionList(existing);
        INJECTOR.set(manager, list);
        return list;
    }

    @Override
    public Permission put(String k, Permission v) {
        for (Entry<String, Boolean> ent : v.getChildren().entrySet()) {
            this.childParentMapping.put(ent.getKey(), new SimpleEntry(v.getName(), ent.getValue()));
        }
        FieldReplacer<Permission, Map> repl = this.getFieldReplacer(v);
        repl.set(v, new NotifyingChildrenMap(v));
        return super.put(k, v);
    }

    @Override
    public Permission remove(Object k) {
        Permission ret = (Permission)super.remove(k);
        if (ret != null) {
            this.removeAllChildren(k.toString());
            this.getFieldReplacer(ret).set(ret, new LinkedHashMap(ret.getChildren()));
        }
        return ret;
    }

    @Override
    public void clear() {
        this.childParentMapping.clear();
        super.clear();
    }

    public Collection<Entry<String, Boolean>> getParents(String permission) {
        return this.childParentMapping.get(permission.toLowerCase());
    }

    static {
        CHILDREN_MAPS = new HashMap();
    }

    private class NotifyingChildrenMap
    extends LinkedHashMap<String, Boolean> {
        private static final long serialVersionUID = 1L;
        private final Permission perm;

        public NotifyingChildrenMap(Permission perm) {
            super(perm.getChildren());
            this.perm = perm;
        }

        @Override
        public Boolean remove(Object perm) {
            this.removeFromMapping(String.valueOf(perm));
            return (Boolean)super.remove(perm);
        }

        private void removeFromMapping(String child) {
            Iterator it = PermissionList.this.childParentMapping.get(child).iterator();
            while (it.hasNext()) {
                if (!((String)((Map.Entry)it.next()).getKey()).equals(this.perm.getName())) continue;
                it.remove();
            }
        }

        @Override
        public Boolean put(String perm, Boolean val) {
            PermissionList.this.childParentMapping.put(perm, new SimpleEntry<String, Boolean>(this.perm.getName(), val));
            return super.put(perm, val);
        }

        @Override
        public void clear() {
            PermissionList.this.removeAllChildren(this.perm.getName());
            super.clear();
        }
    }
}

