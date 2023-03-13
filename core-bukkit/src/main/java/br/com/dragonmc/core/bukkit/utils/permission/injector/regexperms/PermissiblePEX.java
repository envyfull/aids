/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.Validate
 *  org.bukkit.entity.Player
 *  org.bukkit.permissions.Permissible
 *  org.bukkit.permissions.PermissibleBase
 *  org.bukkit.permissions.Permission
 *  org.bukkit.permissions.PermissionAttachment
 *  org.bukkit.permissions.PermissionAttachmentInfo
 *  org.bukkit.permissions.ServerOperator
 */
package br.com.dragonmc.core.bukkit.utils.permission.injector.regexperms;

import java.io.Serial;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import br.com.dragonmc.core.bukkit.utils.permission.PermissionManager;
import br.com.dragonmc.core.bukkit.utils.permission.injector.FieldReplacer;
import br.com.dragonmc.core.bukkit.utils.permission.injector.PermissionCheckResult;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.permissions.ServerOperator;

public class PermissiblePEX
extends PermissibleBase {
    private static final FieldReplacer<PermissibleBase, Map> PERMISSIONS_FIELD = new FieldReplacer<PermissibleBase, Map>(PermissibleBase.class, "permissions", Map.class);
    private static final FieldReplacer<PermissibleBase, List> ATTACHMENTS_FIELD = new FieldReplacer<PermissibleBase, List>(PermissibleBase.class, "attachments", List.class);
    private static final Method CALC_CHILD_PERMS_METH;
    private final Map<String, PermissionAttachmentInfo> permissions;
    private final List<PermissionAttachment> attachments;
    private static final AtomicBoolean LAST_CALL_ERRORED;
    protected final Player player;
    protected final PermissionManager plugin;
    private Permissible previousPermissible = null;
    protected final Map<String, PermissionCheckResult> cache = new ConcurrentHashMap<String, PermissionCheckResult>();
    private final Object permissionsLock = new Object();

    public PermissiblePEX(Player player, PermissionManager plugin) {
        super((ServerOperator)player);
        this.player = player;
        this.plugin = plugin;
        this.permissions = new LinkedHashMap<String, PermissionAttachmentInfo>(){
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public PermissionAttachmentInfo put(String k, PermissionAttachmentInfo v) {
                PermissionAttachmentInfo existing = (PermissionAttachmentInfo)this.get(k);
                if (existing != null) {
                    return existing;
                }
                return super.put(k, v);
            }
        };
        PERMISSIONS_FIELD.set(this, this.permissions);
        this.attachments = ATTACHMENTS_FIELD.get(this);
        this.recalculatePermissions();
    }

    public Permissible getPreviousPermissible() {
        return this.previousPermissible;
    }

    public void setPreviousPermissible(Permissible previousPermissible) {
        this.previousPermissible = previousPermissible;
    }

    public boolean hasPermission(String permission) {
        PermissionCheckResult res = this.permissionValue(permission);
        switch (res) {
            case TRUE: 
            case FALSE: {
                return res.toBoolean();
            }
        }
        if (super.isPermissionSet(permission)) {
            boolean ret = super.hasPermission(permission);
            return ret;
        }
        Permission perm = this.player.getServer().getPluginManager().getPermission(permission);
        return perm == null ? Permission.DEFAULT_PERMISSION.getValue(this.player.isOp()) : perm.getDefault().getValue(this.player.isOp());
    }

    public boolean hasPermission(Permission permission) {
        PermissionCheckResult res = this.permissionValue(permission.getName());
        switch (res) {
            case TRUE: 
            case FALSE: {
                return res.toBoolean();
            }
        }
        if (super.isPermissionSet(permission.getName())) {
            boolean ret = super.hasPermission(permission);
            return ret;
        }
        return permission.getDefault().getValue(this.player.isOp());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void recalculatePermissions() {
        if (this.cache != null && this.permissions != null && this.attachments != null) {
            Object object = this.permissionsLock;
            synchronized (object) {
                this.clearPermissions();
                this.cache.clear();
                ListIterator<PermissionAttachment> it = this.attachments.listIterator(this.attachments.size());
                while (it.hasPrevious()) {
                    PermissionAttachment attach = it.previous();
                    this.calculateChildPerms(attach.getPermissions(), false, attach);
                }
                for (Permission p : this.player.getServer().getPluginManager().getDefaultPermissions(this.isOp())) {
                    this.permissions.put(p.getName(), new PermissionAttachmentInfo((Permissible)this.player, p.getName(), null, true));
                    this.calculateChildPerms(p.getChildren(), false, null);
                }
            }
        }
    }

    protected void calculateChildPerms(Map<String, Boolean> children, boolean invert, PermissionAttachment attachment) {
        try {
            CALC_CHILD_PERMS_METH.invoke((Object)this, children, invert, attachment);
        }
        catch (IllegalAccessException illegalAccessException) {
        }
        catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isPermissionSet(String permission) {
        return super.isPermissionSet(permission) || this.permissionValue(permission) != PermissionCheckResult.UNDEFINED;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        Object object = this.permissionsLock;
        synchronized (object) {
            return new LinkedHashSet<PermissionAttachmentInfo>(this.permissions.values());
        }
    }

    private PermissionCheckResult checkSingle(String expression, String permission, boolean value) {
        if (this.plugin.getPermissionMatcher().isMatches(expression, permission)) {
            PermissionCheckResult res = PermissionCheckResult.fromBoolean(value);
            return res;
        }
        return PermissionCheckResult.UNDEFINED;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected PermissionCheckResult permissionValue(String permission) {
        try {
            Validate.notNull(permission, (String)"Permissions being checked must not be null!");
            permission = permission.toLowerCase();
            PermissionCheckResult res = this.cache.get(permission);
            if (res != null) {
                return res;
            }
            res = PermissionCheckResult.UNDEFINED;
            Iterator<Map.Entry<String, Boolean>> iterator = (Iterator<Map.Entry<String, Boolean>>) this.permissionsLock;
            synchronized (iterator) {
                PermissionAttachmentInfo pai;
                Iterator<PermissionAttachmentInfo> iterator2 = this.permissions.values().iterator();
                while (iterator2.hasNext() && (res = this.checkSingle((pai = iterator2.next()).getPermission(), permission, pai.getValue())) == PermissionCheckResult.UNDEFINED) {
                }
            }
            if (res == PermissionCheckResult.UNDEFINED) {
                for (Map.Entry<String, Boolean> ent : this.plugin.getRegexPerms().getPermissionList().getParents(permission)) {
                    res = this.permissionValue(ent.getKey());
                    if (res == PermissionCheckResult.UNDEFINED) continue;
                    res = PermissionCheckResult.fromBoolean(!(res.toBoolean() ^ ent.getValue()));
                    break;
                }
            }
            this.cache.put(permission, res);
            LAST_CALL_ERRORED.set(false);
            return res;
        }
        catch (Throwable t) {
            if (LAST_CALL_ERRORED.compareAndSet(false, true)) {
                t.printStackTrace();
            }
            return PermissionCheckResult.UNDEFINED;
        }
    }

    static {
        try {
            CALC_CHILD_PERMS_METH = PermissibleBase.class.getDeclaredMethod("calculateChildPermissions", Map.class, Boolean.TYPE, PermissionAttachment.class);
        }
        catch (NoSuchMethodException e) {
            throw new ExceptionInInitializerError(e);
        }
        CALC_CHILD_PERMS_METH.setAccessible(true);
        LAST_CALL_ERRORED = new AtomicBoolean(false);
    }
}

