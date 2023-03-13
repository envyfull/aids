/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.core.common.backend;

import java.util.Collection;
import br.com.dragonmc.core.common.utils.Callback;

public interface Query<T> {
    public Collection<T> find();

    public Collection<T> find(String var1);

    public <GenericType> Collection<T> find(String var1, GenericType var2);

    public <GenericType> Collection<T> find(String var1, String var2, GenericType var3);

    public <GenericType> T findOne(String var1, GenericType var2);

    public <GenericType> T findOne(String var1, String var2, GenericType var3);

    public void create(String[] var1);

    public void create(String var1, String[] var2);

    public <GenericType> void deleteOne(String var1, GenericType var2);

    public <GenericType> void deleteOne(String var1, String var2, GenericType var3);

    public <GenericType> void updateOne(String var1, GenericType var2, T var3);

    public <GenericType> void updateOne(String var1, String var2, GenericType var3, T var4);

    public <GenericType> Collection<T> ranking(String var1, GenericType var2, int var3);

    public static class QueryResponse<T> {
        private long startTime = System.currentTimeMillis();
        private long durationTime;
        private Callback<T> callback;

        public QueryResponse(Callback<T> callback) {
            this.callback = callback;
        }

        public void callback(T t) {
            this.callback.callback(t);
        }

        public long getStartTime() {
            return this.startTime;
        }

        public long getDurationTime() {
            return this.durationTime;
        }

        public Callback<T> getCallback() {
            return this.callback;
        }
    }
}

