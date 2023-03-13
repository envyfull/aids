/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.core.common.server.loadbalancer.type;

import br.com.dragonmc.core.common.server.loadbalancer.element.LoadBalancerObject;
import br.com.dragonmc.core.common.server.loadbalancer.element.NumberConnection;
import br.com.dragonmc.core.common.server.loadbalancer.BaseBalancer;

public class LeastConnection<T extends LoadBalancerObject & NumberConnection>
extends BaseBalancer<T> {
    @Override
    public T next() {
        LoadBalancerObject obj = null;
        if (this.nextObj != null && !this.nextObj.isEmpty()) {
            for (LoadBalancerObject item : this.nextObj) {
                if (!item.canBeSelected()) continue;
                if (obj == null) {
                    obj = item;
                    continue;
                }
                if (((NumberConnection)((Object)obj)).getActualNumber() < ((NumberConnection)((Object)item)).getActualNumber()) continue;
                obj = item;
            }
        }
        return (T)obj;
    }

    @Override
    public int getTotalNumber() {
        int number = 0;
        for (LoadBalancerObject item : this.nextObj) {
            number += ((NumberConnection)((Object)item)).getActualNumber();
        }
        return number;
    }
}

