/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.core.common.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import br.com.dragonmc.core.common.member.status.Status;
import br.com.dragonmc.core.common.member.status.StatusType;
import br.com.dragonmc.core.common.CommonPlugin;

public class StatusManager {
    private Map<UUID, Map<StatusType, Status>> statusMap = new HashMap<UUID, Map<StatusType, Status>>();

    public Status loadStatus(UUID uniqueId, StatusType statusType) {
        if (this.statusMap.containsKey(uniqueId) && this.statusMap.get(uniqueId).containsKey((Object)statusType)) {
            return this.statusMap.get(uniqueId).get((Object)statusType);
        }
        Status status = CommonPlugin.getInstance().getMemberData().loadStatus(uniqueId, statusType);
        this.statusMap.computeIfAbsent(uniqueId, v -> new HashMap()).put(statusType, status);
        return status;
    }

    public void preloadStatus(UUID uniqueId, StatusType statusType) {
        if (this.statusMap.containsKey(uniqueId) && this.statusMap.get(uniqueId).containsKey((Object)statusType)) {
            return;
        }
        Status status = CommonPlugin.getInstance().getMemberData().loadStatus(uniqueId, statusType);
        this.statusMap.computeIfAbsent(uniqueId, v -> new HashMap()).put(statusType, status);
    }

    public void unloadStatus(UUID uniqueId) {
        this.statusMap.remove(uniqueId);
    }
}

