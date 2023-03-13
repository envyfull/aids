/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.core.common.member.status;

import java.util.HashMap;
import java.util.UUID;
import br.com.dragonmc.core.common.CommonPlugin;

public class Status {
    private final UUID uniqueId;
    private final StatusType statusType;
    private HashMap<String, Integer> integerMap;

    public Status(UUID uniqueId, StatusType statusType) {
        this.uniqueId = uniqueId;
        this.statusType = statusType;
        this.integerMap = new HashMap();
    }

    public int getInteger(String key) {
        return this.getInteger(key, 0);
    }

    public int getInteger(Enum<?> enumType) {
        return this.getInteger(enumType.name().toLowerCase(), 0);
    }

    public int getInteger(String key, int defaultValue) {
        if (this.integerMap.containsKey(key.toLowerCase())) {
            return this.integerMap.get(key.toLowerCase());
        }
        return defaultValue;
    }

    public int getInteger(Enum<?> enumType, int defaultValue) {
        if (this.integerMap.containsKey(enumType.name().toLowerCase())) {
            return this.integerMap.get(enumType.name().toLowerCase());
        }
        return defaultValue;
    }

    public void setInteger(String key, int value) {
        this.integerMap.put(key, value);
        this.save();
    }

    public void setInteger(Enum<?> enumType, int value) {
        this.integerMap.put(enumType.name().toLowerCase(), value);
        this.save();
    }

    public void addInteger(Enum<?> enumType, int value) {
        this.integerMap.put(enumType.name().toLowerCase(), this.getInteger(enumType, 0) + value);
        this.save();
    }

    public void addInteger(String key, int value) {
        this.integerMap.put(key.toLowerCase(), this.getInteger(key, 0) + value);
        this.save();
    }

    public void save() {
        this.save("integerMap");
    }

    public void save(String fieldName) {
        CommonPlugin.getInstance().getMemberData().saveStatus(this, fieldName);
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public StatusType getStatusType() {
        return this.statusType;
    }

    public HashMap<String, Integer> getIntegerMap() {
        return this.integerMap;
    }
}

