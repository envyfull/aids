/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.core.common.server.loadbalancer.type;

import br.com.dragonmc.core.common.server.loadbalancer.element.LoadBalancerObject;
import br.com.dragonmc.core.common.server.loadbalancer.element.NumberConnection;
import br.com.dragonmc.core.common.server.loadbalancer.BaseBalancer;

public class RoundRobin<T extends LoadBalancerObject & NumberConnection>
extends BaseBalancer<T> {
    private int next = 0;

    @Override
    public T next() {
        LoadBalancerObject obj = null;
        if (this.nextObj != null && !this.nextObj.isEmpty()) {
            while (this.next < this.nextObj.size()) {
                obj = (LoadBalancerObject)this.nextObj.get(this.next);
                ++this.next;
                if (obj == null) continue;
                if (obj.canBeSelected()) break;
                obj = null;
            }
        }
        if (this.next + 1 >= this.nextObj.size()) {
            this.next = 0;
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

