/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.core.common.server.loadbalancer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import br.com.dragonmc.core.common.server.loadbalancer.element.LoadBalancerObject;

public abstract class BaseBalancer<T extends LoadBalancerObject>
implements LoadBalancer<T> {
    private Map<String, T> objects;
    protected List<T> nextObj;

    public BaseBalancer() {
        this.objects = new HashMap<String, T>();
        this.nextObj = new ArrayList<T>();
    }

    public BaseBalancer(Map<String, T> map) {
        this.addAll(map);
    }

    public void add(String id, T obj) {
        this.objects.put(id, obj);
        this.update();
    }

    public T get(String id) {
        return (T)((LoadBalancerObject)this.objects.get(id));
    }

    public void remove(String id) {
        this.objects.remove(id);
        this.update();
    }

    public void addAll(Map<String, T> map) {
        if (this.objects != null) {
            this.objects.clear();
        }
        this.objects = map;
        this.update();
    }

    public List<T> getList() {
        return this.nextObj;
    }

    public void update() {
        if (this.nextObj != null) {
            this.nextObj.clear();
        }
        this.nextObj = new ArrayList<T>();
        this.nextObj.addAll(this.objects.values().stream().sorted((o1, o2) -> Long.compare(o1.getStartTime(), o2.getStartTime())).collect(Collectors.toList()));
    }

    public abstract int getTotalNumber();
}

