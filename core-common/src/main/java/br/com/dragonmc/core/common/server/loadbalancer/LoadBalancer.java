/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.core.common.server.loadbalancer;

import br.com.dragonmc.core.common.server.loadbalancer.element.LoadBalancerObject;

public interface LoadBalancer<T extends LoadBalancerObject> {
    public T next();
}

