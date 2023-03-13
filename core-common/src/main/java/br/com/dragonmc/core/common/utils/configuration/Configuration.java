/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package br.com.dragonmc.core.common.utils.configuration;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import lombok.NonNull;
import br.com.dragonmc.core.common.utils.FileCreator;
import br.com.dragonmc.core.common.utils.NumberConversions;

public interface Configuration {
    public static final FileCreator FILE_CREATOR = new DefaultFileCreator();

    public Configuration loadConfig() throws FileNotFoundException, Exception;

    public boolean saveConfig() throws FileNotFoundException, Exception;

    public String getFileName();

    public String getFilePath();

    public Configuration defaultSave(boolean var1);

    public Map<String, Object> getValues();

    public <T extends Configuration> T watch() throws Exception;

    public <T> boolean set(@NonNull String var1, T var2);

    public Object getAsObject(@NonNull String var1, Object var2, boolean var3);

    public <T> T get(@NonNull String var1, T var2, boolean var3);

    default public <T> T get(@NonNull String fieldName, T defaultValue) {
        if (fieldName == null) {
            throw new NullPointerException("fieldName is marked non-null but is null");
        }
        return this.get(fieldName, defaultValue, false);
    }

    default public Object get(@NonNull String fieldName) {
        if (fieldName == null) {
            throw new NullPointerException("fieldName is marked non-null but is null");
        }
        return this.get(fieldName, null, false);
    }

    default public String getString(@NonNull String fieldName, String defaultValue, boolean saveDefaultValue) {
        if (fieldName == null) {
            throw new NullPointerException("fieldName is marked non-null but is null");
        }
        return (String)String.class.cast(this.get(fieldName, defaultValue, saveDefaultValue));
    }

    default public String getString(@NonNull String fieldName) {
        if (fieldName == null) {
            throw new NullPointerException("fieldName is marked non-null but is null");
        }
        return this.getString(fieldName, null, false);
    }

    default public String getString(@NonNull String fieldName, String defaultValue) {
        if (fieldName == null) {
            throw new NullPointerException("fieldName is marked non-null but is null");
        }
        return this.getString(fieldName, defaultValue, false);
    }

    default public <T> T getAs(@NonNull String fieldName, T defaultValue, boolean saveDefaultValue, Class<T> clazz) {
        if (fieldName == null) {
            throw new NullPointerException("fieldName is marked non-null but is null");
        }
        return clazz.cast(this.get(fieldName, defaultValue, saveDefaultValue));
    }

    default public <T> T getAs(@NonNull String fieldName, T defaultValue, Class<T> clazz) {
        if (fieldName == null) {
            throw new NullPointerException("fieldName is marked non-null but is null");
        }
        return this.getAs(fieldName, defaultValue, false, clazz);
    }

    default public <T> T getAs(@NonNull String fieldName, Class<T> clazz) {
        if (fieldName == null) {
            throw new NullPointerException("fieldName is marked non-null but is null");
        }
        return this.getAs(fieldName, null, false, clazz);
    }

    default public OptionalInt getInt(@NonNull String fieldName, int defaultValue, boolean saveDefaultValue) {
        if (fieldName == null) {
            throw new NullPointerException("fieldName is marked non-null but is null");
        }
        Object object = this.getAsObject(fieldName, defaultValue, saveDefaultValue);
        return OptionalInt.of(object instanceof Number ? NumberConversions.toInt(object) : 0);
    }

    default public OptionalInt getInt(@NonNull String fieldName) {
        if (fieldName == null) {
            throw new NullPointerException("fieldName is marked non-null but is null");
        }
        return this.getInt(fieldName, 0, false);
    }

    default public OptionalInt getInt(@NonNull String fieldName, int defaultValue) {
        if (fieldName == null) {
            throw new NullPointerException("fieldName is marked non-null but is null");
        }
        return this.getInt(fieldName, defaultValue, false);
    }

    default public OptionalDouble getDouble(@NonNull String fieldName, double defaultValue, boolean saveDefaultValue) {
        if (fieldName == null) {
            throw new NullPointerException("fieldName is marked non-null but is null");
        }
        Object object = this.getAsObject(fieldName, defaultValue, saveDefaultValue);
        return OptionalDouble.of(object instanceof Number ? NumberConversions.toDouble(object) : 0.0);
    }

    default public OptionalDouble getDouble(@NonNull String fieldName, Double defaultValue) {
        if (fieldName == null) {
            throw new NullPointerException("fieldName is marked non-null but is null");
        }
        return this.getDouble(fieldName, defaultValue, false);
    }

    default public OptionalDouble getDouble(@NonNull String fieldName) {
        if (fieldName == null) {
            throw new NullPointerException("fieldName is marked non-null but is null");
        }
        return this.getDouble(fieldName, 0.0, false);
    }

    default public OptionalLong getLong(@NonNull String fieldName, long defaultValue, boolean saveDefaultValue) {
        if (fieldName == null) {
            throw new NullPointerException("fieldName is marked non-null but is null");
        }
        Object object = this.getAsObject(fieldName, defaultValue, saveDefaultValue);
        return OptionalLong.of(object instanceof Number ? NumberConversions.toLong(object) : 0L);
    }

    default public OptionalLong getLong(@NonNull String fieldName, Long defaultValue) {
        if (fieldName == null) {
            throw new NullPointerException("fieldName is marked non-null but is null");
        }
        return this.getLong(fieldName, defaultValue, false);
    }

    default public OptionalLong getLong(@NonNull String fieldName) {
        if (fieldName == null) {
            throw new NullPointerException("fieldName is marked non-null but is null");
        }
        return this.getLong(fieldName, 0L, false);
    }

    default public float getFloat(@NonNull String fieldName, float defaultValue, boolean saveDefaultValue) {
        if (fieldName == null) {
            throw new NullPointerException("fieldName is marked non-null but is null");
        }
        Object object = this.getAsObject(fieldName, Float.valueOf(defaultValue), saveDefaultValue);
        return object instanceof Number ? NumberConversions.toFloat(object) : 0.0f;
    }

    default public float getFloat(@NonNull String fieldName, float defaultValue) {
        if (fieldName == null) {
            throw new NullPointerException("fieldName is marked non-null but is null");
        }
        return this.getFloat(fieldName, defaultValue, false);
    }

    default public float getFloat(@NonNull String fieldName) {
        if (fieldName == null) {
            throw new NullPointerException("fieldName is marked non-null but is null");
        }
        return this.getFloat(fieldName, 0.0f, false);
    }

    default public boolean getBoolean(@NonNull String fieldName, Boolean defaultValue, boolean saveDefaultValue) {
        if (fieldName == null) {
            throw new NullPointerException("fieldName is marked non-null but is null");
        }
        return (Boolean)Boolean.class.cast(this.getAs(fieldName, defaultValue, saveDefaultValue, Boolean.class));
    }

    default public boolean getBoolean(@NonNull String fieldName, boolean defaultValue) {
        if (fieldName == null) {
            throw new NullPointerException("fieldName is marked non-null but is null");
        }
        return this.getBoolean(fieldName, defaultValue, false);
    }

    default public boolean getBoolean(@NonNull String fieldName) {
        if (fieldName == null) {
            throw new NullPointerException("fieldName is marked non-null but is null");
        }
        return this.getBoolean(fieldName, false, false);
    }

    public <T> List<T> getList(String var1, List<T> var2, boolean var3, Class<T> var4);

    default public <T> List<T> getList(String fieldName, Class<T> clazz) {
        return this.getList(fieldName, new ArrayList(), false, clazz);
    }

    public <T> boolean addElementToList(String var1, T var2);

    public <T> boolean setElementToList(String var1, int var2, T var3);
}

