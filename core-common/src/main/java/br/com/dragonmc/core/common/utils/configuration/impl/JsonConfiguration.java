/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.gson.JsonElement
 *  com.google.gson.internal.LinkedTreeMap
 *  com.google.gson.stream.JsonReader
 *  lombok.NonNull
 */
package br.com.dragonmc.core.common.utils.configuration.impl;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.stream.JsonReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.NonNull;
import br.com.dragonmc.core.common.CommonConst;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.common.utils.FileWatcher;
import br.com.dragonmc.core.common.utils.configuration.Configuration;

public class JsonConfiguration
implements Configuration {
    private final String fileName;
    private final String filePath;
    private boolean defaultSave;
    private Runnable watchChanges;
    private Map<String, Object> map;
    private Set<String> verifySet = new HashSet<String>();

    public static void main(String[] args) {
        Configuration jsonConfiguration = new JsonConfiguration("bedwars.json", "C:\\Users\\ALLAN\\Desktop\\high\\Server\\Bedwars 2\\plugins\\GameAPI").defaultSave(true);
        try {
            jsonConfiguration.loadConfig();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(jsonConfiguration.getInt("maxPlayers", 8));
        jsonConfiguration.set("maxPlayers", 8);
    }

    public JsonConfiguration(String fileName, String filePath, boolean defaultSave) {
        this(fileName, filePath);
        this.defaultSave = defaultSave;
    }

    @Override
    public Configuration loadConfig() throws FileNotFoundException, Exception {
        FileInputStream fileInputStream = new FileInputStream(FILE_CREATOR.createFile(this.fileName, this.filePath));
        InputStreamReader inputStreamReader = new InputStreamReader((InputStream)fileInputStream, "UTF-8");
        JsonReader jsonReader = new JsonReader((Reader)inputStreamReader);
        this.map = (Map)CommonConst.GSON_PRETTY.fromJson(jsonReader, Map.class);
        for (Map.Entry<String, Object> entry : this.map.entrySet()) {
            Object object = entry.getValue();
            if (!(object instanceof LinkedTreeMap)) continue;
            JsonElement jsonElement = CommonConst.GSON.toJsonTree(object);
            entry.setValue(jsonElement);
        }
        jsonReader.close();
        inputStreamReader.close();
        fileInputStream.close();
        this.verifySet.clear();
        return this;
    }

    @Override
    public boolean saveConfig() throws FileNotFoundException, Exception {
        String json = CommonConst.GSON_PRETTY.toJson(this.map);
        FileOutputStream fileOutputStream = new FileOutputStream(FILE_CREATOR.createFile(this.fileName, this.filePath));
        OutputStreamWriter outputStreamReader = new OutputStreamWriter((OutputStream)fileOutputStream, "UTF-8");
        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamReader);
        bufferedWriter.write(json);
        bufferedWriter.flush();
        bufferedWriter.close();
        fileOutputStream.close();
        outputStreamReader.close();
        return true;
    }

    @Override
    public Configuration defaultSave(boolean defaultSave) {
        this.defaultSave = defaultSave;
        return this;
    }

    @Override
    public Map<String, Object> getValues() {
        return ImmutableMap.copyOf(this.map);
    }

    @Override
    public <T> boolean set(@NonNull String fieldName, T value) {
        if (fieldName == null) {
            throw new NullPointerException("fieldName is marked non-null but is null");
        }
        this.map.put(fieldName, value);
        if (this.defaultSave) {
            try {
                return this.saveConfig();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public Object getAsObject(@NonNull String fieldName, Object defaultValue, boolean saveDefaultValue) {
        if (fieldName == null) {
            throw new NullPointerException("fieldName is marked non-null but is null");
        }
        if (this.map.containsKey(fieldName)) {
            return this.map.get(fieldName);
        }
        if (saveDefaultValue && defaultValue != null) {
            this.set(fieldName, defaultValue);
        }
        return defaultValue;
    }

    @Override
    public <T> T get(@NonNull String fieldName, T defaultValue, boolean saveDefaultValue) {
        if (fieldName == null) {
            throw new NullPointerException("fieldName is marked non-null but is null");
        }
        if (this.map.containsKey(fieldName)) {
            return (T)this.map.get(fieldName);
        }
        if (saveDefaultValue && defaultValue != null) {
            this.set(fieldName, defaultValue);
        }
        return defaultValue;
    }

    @Override
    public <T> List<T> getList(String fieldName, List<T> defaultValue, boolean saveDefaultValue, Class<T> clazz) {
        Object object;
        if (this.map.containsKey(fieldName) && (object = this.map.get(fieldName)) instanceof List) {
            if (!this.verifySet.contains(fieldName)) {
                List list = (List)object;
                int index = 0;
                for (Object t : list) {
                    if (t instanceof LinkedTreeMap) {
                        JsonElement jsonElement = CommonConst.GSON_PRETTY.toJsonTree(t);
                        list.set(index, CommonConst.GSON_PRETTY.fromJson(jsonElement, clazz));
                    }
                    ++index;
                }
                this.verifySet.add(fieldName);
            }
            return (List)List.class.cast(object);
        }
        if (saveDefaultValue && defaultValue != null) {
            this.set(fieldName, defaultValue);
        }
        return defaultValue;
    }

    @Override
    public <T> boolean addElementToList(String fieldName, T value) {
        ((List)this.map.computeIfAbsent(fieldName, v -> new ArrayList())).add(value);
        if (this.defaultSave) {
            try {
                return this.saveConfig();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public <T> boolean setElementToList(String fieldName, int index, T value) {
        ((List)this.map.computeIfAbsent(fieldName, v -> new ArrayList())).set(index, value);
        if (this.defaultSave) {
            try {
                return this.saveConfig();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return true;
    }

    public JsonConfiguration watch() throws Exception {
        if (this.watchChanges != null) {
            throw new Exception("Cannot disable watch file");
        }
        this.watchChanges = new FileWatcher(FILE_CREATOR.createFile(this.fileName, this.filePath)){

            @Override
            public void onChange() {
                try {
                    JsonConfiguration.this.loadConfig();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        CommonPlugin.getInstance().getPluginPlatform().run(this.watchChanges, 100L, 100L);
        return this;
    }

    @Override
    public String getFileName() {
        return this.fileName;
    }

    @Override
    public String getFilePath() {
        return this.filePath;
    }

    public boolean isDefaultSave() {
        return this.defaultSave;
    }

    public Runnable getWatchChanges() {
        return this.watchChanges;
    }

    public Map<String, Object> getMap() {
        return this.map;
    }

    public Set<String> getVerifySet() {
        return this.verifySet;
    }

    public JsonConfiguration(String fileName, String filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
    }
}

