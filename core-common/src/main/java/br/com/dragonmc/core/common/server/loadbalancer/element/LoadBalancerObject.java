/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.core.common.server.loadbalancer.element;

public interface LoadBalancerObject {
    public String getServerId();

    public long getStartTime();

    public boolean canBeSelected();
}

