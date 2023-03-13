/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  com.google.gson.JsonPrimitive
 *  org.bson.BsonInvalidOperationException
 *  org.bson.Document
 */
package br.com.dragonmc.core.common.utils.json;

import br.com.dragonmc.core.common.CommonConst;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.BsonInvalidOperationException;
import org.bson.Document;

public class JsonUtils {
    public static JsonObject jsonTree(Object src) {
        return CommonConst.GSON.toJsonTree(src).getAsJsonObject();
    }

    public static Object elementToBson(JsonElement element) {
        if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isString()) {
                return primitive.getAsString();
            }
            if (primitive.isNumber()) {
                return primitive.getAsNumber();
            }
            if (primitive.isBoolean()) {
                return primitive.getAsBoolean();
            }
        } else if (element.isJsonArray()) {
            return CommonConst.GSON.fromJson(element, List.class);
        }
        try {
            return Document.parse((String)CommonConst.GSON.toJson(element));
        }
        catch (BsonInvalidOperationException ex) {
            return JsonParser.parseString((String)CommonConst.GSON.toJson(element));
        }
    }

    public static String elementToString(JsonElement element) {
        JsonPrimitive primitive;
        if (element.isJsonPrimitive() && (primitive = element.getAsJsonPrimitive()).isString()) {
            return primitive.getAsString();
        }
        return CommonConst.GSON.toJson(element);
    }

    public static <T> T mapToObject(Map<String, String> map, Class<T> clazz) {
        JsonObject obj = new JsonObject();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            try {
                obj.add(entry.getKey(), JsonParser.parseString((String)entry.getValue()));
            }
            catch (Exception e) {
                obj.addProperty(entry.getKey(), entry.getValue());
            }
        }
        return (T)CommonConst.GSON.fromJson((JsonElement)obj, clazz);
    }

    public static Map<String, String> objectToMap(Object src) {
        HashMap<String, String> map = new HashMap<String, String>();
        JsonObject obj = (JsonObject)CommonConst.GSON.toJsonTree(src);
        for (Map.Entry entry : obj.entrySet()) {
            map.put((String)entry.getKey(), CommonConst.GSON.toJson((JsonElement)entry.getValue()));
        }
        return map;
    }
}

