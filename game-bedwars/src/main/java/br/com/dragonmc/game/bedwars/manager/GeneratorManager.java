/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  org.bukkit.Bukkit
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 */
package br.com.dragonmc.game.bedwars.manager;

import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import br.com.dragonmc.core.bukkit.event.UpdateEvent;
import br.com.dragonmc.game.engine.GameAPI;
import br.com.dragonmc.game.bedwars.GameMain;
import br.com.dragonmc.game.bedwars.generator.Generator;
import br.com.dragonmc.game.bedwars.generator.GeneratorType;
import br.com.dragonmc.game.bedwars.generator.impl.DiamondGenerator;
import br.com.dragonmc.game.bedwars.generator.impl.EmeraldGenerator;
import br.com.dragonmc.core.bukkit.utils.Location;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class GeneratorManager {
    private Map<GeneratorType, List<Generator>> generatorMap = new HashMap<GeneratorType, List<Generator>>();

    public GeneratorManager() {
        for (GeneratorType generatorType : GeneratorType.values()) {
            if (generatorType == GeneratorType.NORMAL) continue;
            List<Location> list = GameMain.getInstance().getConfiguration().getList(generatorType.getConfigFieldName(), new ArrayList(), true, Location.class);
            for (Location location : list) {
                this.createGenerator(generatorType, location, false);
            }
        }
    }

    public void createGenerator(GeneratorType generatorType, Location location, boolean save) {
        Generator generator = generatorType == GeneratorType.DIAMOND ? new DiamondGenerator(location.getAsLocation()) : new EmeraldGenerator(location.getAsLocation());
        this.generatorMap.computeIfAbsent(generatorType, v -> new ArrayList()).add(generator);
        if (save) {
            GameMain.getInstance().getConfiguration().addElementToList(generatorType.getConfigFieldName(), location);
        }
    }

    public boolean setLocation(GeneratorType generatorType, int index, Location fromLocation, boolean save) {
        Generator generator = (Generator)this.generatorMap.computeIfAbsent(generatorType, v -> new ArrayList()).get(index);
        if (generator == null) {
            return false;
        }
        generator.setLocation(fromLocation.getAsLocation());
        if (save) {
            return GameMain.getInstance().getConfiguration().setElementToList(generatorType.getConfigFieldName(), index, fromLocation);
        }
        return true;
    }

    public void startGenerators() {
        this.generatorMap.values().forEach(list -> list.forEach(Generator::handleHologram));
        Bukkit.getPluginManager().registerEvents((Listener)new GeneratorListener(), (Plugin)GameAPI.getInstance());
    }

    public Generator getGenerator(GeneratorType generatorType, int asInt) {
        return this.generatorMap.containsKey((Object)generatorType) ? this.generatorMap.get((Object)generatorType).get(asInt) : null;
    }

    public List<Generator> getGenerators(GeneratorType generatorType) {
        return this.generatorMap.get((Object)generatorType);
    }

    public List<Generator> getGenerators() {
        ArrayList<Generator> generator = new ArrayList<Generator>();
        for (GeneratorType generatorType : GeneratorType.values()) {
            if (!this.generatorMap.containsKey((Object)generatorType)) continue;
            generator.addAll((Collection<Generator>)this.generatorMap.get((Object)generatorType));
        }
        return generator;
    }

    public void addGenerator(Generator createGenerator) {
        this.generatorMap.computeIfAbsent(createGenerator.getGeneratorType(), v -> new ArrayList()).add(createGenerator);
    }

    public class GeneratorListener
    implements Listener {
        public GeneratorListener() {
            Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)GameAPI.getInstance());
        }

        @EventHandler
        public void onUpdate(UpdateEvent event) {
            if (event.getCurrentTick() % 3L == 0L) {
                for (Map.Entry<GeneratorType, List<Generator>> entry : ImmutableSet.copyOf(GeneratorManager.this.generatorMap.entrySet())) {
                    for (Generator generator : entry.getValue()) {
                        generator.animate();
                        generator.updateHologram();
                        if (generator.getLastGenerate() + generator.getGenerateTime() > System.currentTimeMillis()) continue;
                        generator.generate();
                    }
                }
            }
        }
    }
}

