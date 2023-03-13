/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.core.common.manager;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import br.com.dragonmc.core.common.utils.configuration.Configuration;
import br.com.dragonmc.core.common.utils.configuration.impl.JsonConfiguration;

public class ConfigurationManager {
    private Map<String, Configuration> configurationMap = new HashMap<String, Configuration>();

    public static void main(String[] args) {
        ConfigurationManager configurationManager = new ConfigurationManager();
        try {
            System.out.println(configurationManager.loadConfig("bedwars.json", "C:\\Users\\ALLAN\\Desktop\\Servidores\\Bedwars", false, JsonConfiguration.class).loadConfig().get("islands"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(configurationManager.getConfigByName("bedwars").get("islands"));
    }

    public Collection<String> getConfigs() {
        return this.configurationMap.keySet();
    }

    public Configuration getConfigByName(String configName) {
        if (configName.contains("\\.")) {
            configName = configName.split("\\.")[0];
        }
        return this.configurationMap.get(configName.toLowerCase());
    }

    public <T extends Configuration> T loadConfig(String fileName, String pathName, boolean saveAsDefault, Class<T> clazz) {
        return this.loadConfig(fileName, new File(pathName), saveAsDefault, clazz);
    }

    public <T extends Configuration> T loadConfig(File file, boolean saveAsDefault, Class<T> clazz) {
        return this.loadConfig(file.getName(), file.getParentFile(), saveAsDefault, clazz);
    }

    public <T extends Configuration> T loadConfig(String fileName, File file, boolean saveAsDefault, Class<T> clazz) {
        try {
            Configuration newInstance = (Configuration)clazz.getConstructor(String.class, String.class, Boolean.TYPE).newInstance(fileName, file.getAbsolutePath(), saveAsDefault);
            String configName = fileName.toLowerCase();
            if (configName.contains(".")) {
                configName = configName.split("\\.")[0];
            }
            this.configurationMap.put(configName, newInstance);
            return (T)newInstance;
        }
        catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }
}

