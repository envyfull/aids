/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.core.bukkit.utils.worldedit.schematic;

public final class FloatTag
extends Tag {
    private final float value;

    public FloatTag(String name, float value) {
        super(name);
        this.value = value;
    }

    @Override
    public Float getValue() {
        return Float.valueOf(this.value);
    }

    public String toString() {
        String name = this.getName();
        String append = "";
        if (name != null && !name.equals("")) {
            append = "(\"" + this.getName() + "\")";
        }
        return "TAG_Float" + append + ": " + this.value;
    }
}

