/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.game.engine.manager;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import br.com.dragonmc.game.engine.gamer.Gamer;

public class GamerManager {
    private Map<UUID, Gamer> gamerMap = new HashMap<UUID, Gamer>();

    public void loadGamer(Gamer gamer) {
        this.gamerMap.put(gamer.getUniqueId(), gamer);
    }

    public void unloadGamer(UUID uniqueId) {
        this.gamerMap.remove(uniqueId);
    }

    public <T extends Gamer> T getGamer(UUID uniqueId, Class<T> clazz) {
        return (this.gamerMap.containsKey(uniqueId) ? clazz.cast(this.gamerMap.get(uniqueId)) : null);
    }

    public Gamer getGamer(UUID uniqueId) {
        return this.gamerMap.get(uniqueId);
    }

    public boolean hasGamer(UUID uniqueId) {
        return this.gamerMap.containsKey(uniqueId);
    }

    public <T extends Gamer> List<T> filter(Predicate<T> filter, Class<T> clazz) {
        return this.gamerMap.values().stream().map(gamer -> clazz.cast(gamer)).filter(filter).collect(Collectors.toList());
    }

    public <T extends Gamer> List<T> sort(Comparator<? super T> filter, Class<T> clazz) {
        return (List<T>) this.gamerMap.values().stream().map(gamer -> (Gamer)clazz.cast(gamer)).sorted((Comparator<? super Gamer>) filter).collect(Collectors.toList());
    }

    public <T extends Gamer> Stream<T> stream(Class<T> clazz) {
        return (Stream<T>) this.gamerMap.values().stream().map(gamer -> (Gamer)clazz.cast(gamer));
    }

    public <T extends Gamer> Collection<T> getGamers(Class<T> clazz) {
        return (Collection<T>) this.gamerMap.values().stream().map(gamer -> (Gamer)clazz.cast(gamer)).collect(Collectors.toList());
    }
}
