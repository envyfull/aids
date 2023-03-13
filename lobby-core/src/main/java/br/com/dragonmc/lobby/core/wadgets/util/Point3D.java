/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.lobby.core.wadgets.util;

public class Point3D {
    public float x;
    public float y;
    public float z;

    public Point3D(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point3D rotate(float rot) {
        double cos = Math.cos(rot);
        double sin = Math.sin(rot);
        return new Point3D((float)((double)this.x * cos + (double)this.z * sin), this.y, (float)((double)this.x * -sin + (double)this.z * cos));
    }
}

