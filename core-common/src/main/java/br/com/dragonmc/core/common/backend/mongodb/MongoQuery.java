/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  com.mongodb.client.MongoCollection
 *  com.mongodb.client.MongoCursor
 *  com.mongodb.client.MongoDatabase
 *  com.mongodb.client.model.Filters
 *  org.bson.Document
 *  org.bson.conversions.Bson
 *  org.bson.json.JsonWriterSettings
 */
package br.com.dragonmc.core.common.backend.mongodb;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import java.util.ArrayList;
import java.util.Collection;
import br.com.dragonmc.core.common.backend.Query;
import br.com.dragonmc.core.common.utils.json.JsonUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.json.JsonWriterSettings;

public class MongoQuery
implements Query<JsonElement> {
    private static final JsonWriterSettings SETTINGS = new JsonWriterSettings();
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    public MongoQuery(MongoConnection mongoConnection, String collectionName) {
        this.database = mongoConnection.getDb();
        this.collection = this.database.getCollection(collectionName);
    }

    public MongoQuery(MongoConnection mongoConnection, String databaseName, String collectionName) {
        this.database = mongoConnection.getDatabase(databaseName);
        this.collection = this.database.getCollection(collectionName);
    }

    @Override
    public Collection<JsonElement> find() {
        MongoCursor mongoCursor = this.collection.find().iterator();
        ArrayList<JsonElement> documentList = new ArrayList<JsonElement>();
        while (mongoCursor.hasNext()) {
            documentList.add(JsonParser.parseString((String)((Document)mongoCursor.next()).toJson(SETTINGS)));
        }
        return documentList;
    }

    @Override
    public Collection<JsonElement> find(String collection) {
        MongoCursor mongoCursor = this.database.getCollection(collection).find().iterator();
        ArrayList<JsonElement> documentList = new ArrayList<JsonElement>();
        while (mongoCursor.hasNext()) {
            documentList.add(JsonParser.parseString((String)((Document)mongoCursor.next()).toJson(SETTINGS)));
        }
        return documentList;
    }

    @Override
    public <GenericType> Collection<JsonElement> find(String key, GenericType value) {
        MongoCursor mongoCursor = this.collection.find(Filters.eq((String)key, value)).iterator();
        ArrayList<JsonElement> documentList = new ArrayList<JsonElement>();
        while (mongoCursor.hasNext()) {
            documentList.add(JsonParser.parseString((String)((Document)mongoCursor.next()).toJson(SETTINGS)));
        }
        return documentList;
    }

    @Override
    public <GenericType> Collection<JsonElement> find(String collection, String key, GenericType value) {
        MongoCursor mongoCursor = this.database.getCollection(collection).find(Filters.eq((String)key, value)).iterator();
        ArrayList<JsonElement> documentList = new ArrayList<JsonElement>();
        while (mongoCursor.hasNext()) {
            documentList.add(JsonParser.parseString((String)((Document)mongoCursor.next()).toJson(SETTINGS)));
        }
        return documentList;
    }

    @Override
    public <GenericType> JsonElement findOne(String key, GenericType value) {
        JsonElement json = null;
        Document document = (Document)this.collection.find(Filters.eq((String)key, value)).first();
        if (document != null) {
            json = JsonParser.parseString((String)document.toJson(SETTINGS));
        }
        return json;
    }

    @Override
    public <GenericType> JsonElement findOne(String collection, String key, GenericType value) {
        JsonElement json = null;
        Document document = (Document)this.database.getCollection(collection).find(Filters.eq((String)key, value)).first();
        if (document != null) {
            json = JsonParser.parseString((String)document.toJson(SETTINGS));
        }
        return json;
    }

    @Override
    public void create(String[] jsons) {
        for (String json : jsons) {
            this.collection.insertOne(Document.parse((String)json));
        }
    }

    @Override
    public void create(String collection, String[] jsons) {
        for (String json : jsons) {
            this.database.getCollection(collection).insertOne(Document.parse((String)json));
        }
    }

    @Override
    public <GenericType> void deleteOne(String key, GenericType value) {
        this.collection.deleteOne(Filters.eq((String)key, value));
    }

    @Override
    public <GenericType> void deleteOne(String collection, String key, GenericType value) {
        this.database.getCollection(collection).deleteOne(Filters.eq((String)key, value));
    }

    @Override
    public <GenericType> void updateOne(String key, GenericType value, JsonElement t) {
        JsonObject jsonObject = (JsonObject)t;
        if (jsonObject.has("fieldName") && jsonObject.has("value")) {
            Object object = JsonUtils.elementToBson(jsonObject.get("value"));
            if (object == null || jsonObject.get("value").isJsonNull()) {
                this.collection.updateOne(Filters.eq((String)key, value), (Bson)new Document("$unset", (Object)new Document(jsonObject.get("fieldName").getAsString(), (Object)"")));
            } else {
                this.collection.updateOne(Filters.eq((String)key, value), (Bson)new Document("$set", (Object)new Document(jsonObject.get("fieldName").getAsString(), object)));
            }
        }
    }

    @Override
    public <GenericType> void updateOne(String collection, String key, GenericType value, JsonElement t) {
        JsonObject jsonObject = (JsonObject)t;
        if (jsonObject.has("fieldName") && jsonObject.has("value")) {
            Object object = JsonUtils.elementToBson(jsonObject.get("value"));
            this.database.getCollection(collection).updateOne(Filters.eq((String)key, value), (Bson)new Document("$set", (Object)new Document(jsonObject.get("fieldName").getAsString(), object)));
            return;
        }
        this.database.getCollection(collection).updateOne(Filters.eq((String)key, value), (Bson)Document.parse((String)t.toString()));
    }

    @Override
    public <GenericType> Collection<JsonElement> ranking(String key, GenericType value, int limit) {
        MongoCursor mongoCursor = this.collection.find().sort(Filters.eq((String)key, value)).limit(limit).iterator();
        ArrayList<JsonElement> documentList = new ArrayList<JsonElement>();
        while (mongoCursor.hasNext()) {
            documentList.add(JsonParser.parseString((String)((Document)mongoCursor.next()).toJson(SETTINGS)));
        }
        return documentList;
    }

    public static MongoQuery createDefault(MongoConnection mongoConnection, String databaseName, String collectionName) {
        return new MongoQuery(mongoConnection, databaseName, collectionName);
    }

    public static MongoQuery createDefault(MongoConnection mongoConnection, String collectionName) {
        return new MongoQuery(mongoConnection, mongoConnection.getDataBase(), collectionName);
    }

    public MongoDatabase getDatabase() {
        return this.database;
    }

    public MongoCollection<Document> getCollection() {
        return this.collection;
    }
}

