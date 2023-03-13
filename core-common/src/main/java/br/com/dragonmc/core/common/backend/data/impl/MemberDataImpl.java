/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonPrimitive
 *  com.mongodb.client.MongoCursor
 *  com.mongodb.client.model.Filters
 *  org.bson.Document
 *  org.bson.conversions.Bson
 *  redis.clients.jedis.Jedis
 *  redis.clients.jedis.Pipeline
 */
package br.com.dragonmc.core.common.backend.data.impl;

import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.common.backend.Query;
import br.com.dragonmc.core.common.backend.data.MemberData;
import br.com.dragonmc.core.common.backend.mongodb.MongoConnection;
import br.com.dragonmc.core.common.backend.mongodb.MongoQuery;
import br.com.dragonmc.core.common.backend.redis.RedisConnection;
import br.com.dragonmc.core.common.member.Member;
import br.com.dragonmc.core.common.member.MemberVoid;
import br.com.dragonmc.core.common.member.status.Status;
import br.com.dragonmc.core.common.member.status.StatusType;
import br.com.dragonmc.core.common.packet.types.ReportCreatePacket;
import br.com.dragonmc.core.common.packet.types.ReportDeletePacket;
import br.com.dragonmc.core.common.packet.types.ReportFieldPacket;
import br.com.dragonmc.core.common.permission.Group;
import br.com.dragonmc.core.common.report.Report;
import br.com.dragonmc.core.common.utils.string.StringFormat;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import br.com.dragonmc.core.common.CommonConst;
import br.com.dragonmc.core.common.utils.json.JsonBuilder;
import br.com.dragonmc.core.common.utils.json.JsonUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

public class MemberDataImpl
implements MemberData {
    private RedisConnection redisDatabase;
    private MongoQuery query;
    private MongoQuery statusQuery;
    private MongoQuery reportQuery;

    public MemberDataImpl(MongoConnection mongoConnection, RedisConnection redisDatabase) {
        this.query = MongoQuery.createDefault(mongoConnection, mongoConnection.getDataBase(), "members");
        this.statusQuery = MongoQuery.createDefault(mongoConnection, mongoConnection.getDataBase(), "status");
        this.reportQuery = MongoQuery.createDefault(mongoConnection, mongoConnection.getDataBase(), "report");
        this.redisDatabase = redisDatabase;
    }

    public MemberDataImpl(Query<JsonElement> query, RedisConnection redisDatabase) {
        this.query = (MongoQuery)query;
        this.redisDatabase = redisDatabase;
    }

    @Override
    public Member loadMember(UUID uuid) {
        return this.loadMember(uuid, MemberVoid.class);
    }

    @Override
    public <T extends Member> Collection<T> loadMembersByAddress(String ipAddress, Class<T> clazz) {
        return (Collection<T>) this.query.find("lastIpAddress", ipAddress).stream().map(json -> (Member)CommonConst.GSON.fromJson(json, clazz)).collect(Collectors.toList());
    }

    @Override
    public <T extends Member> T loadMember(UUID uuid, Class<T> clazz) {
        JsonElement found;
        Member member = CommonPlugin.getInstance().getMemberManager().getMember(uuid);
        if (member == null && (member = this.getRedisPlayer(uuid, clazz)) == null && (found = this.query.findOne("uniqueId", uuid.toString())) != null) {
            member = (Member)CommonConst.GSON.fromJson(CommonConst.GSON.toJson(found), clazz);
        }
        return (T)(member == null ? null : (Member)clazz.cast(member));
    }

    @Override
    public <T extends Member> T loadMember(String playerName, boolean ignoreCase, Class<T> clazz) {
        if (ignoreCase) {
            return this.loadMember(this.query.findOne("playerName", new Document("$regex", (Object)("^" + playerName + "$")).append("$options", (Object)"i")), clazz);
        }
        return this.loadMember(this.query.findOne("playerName", playerName), clazz);
    }

    @Override
    public Member loadMember(String playerName, boolean ignoreCase) {
        return this.loadMember(playerName, ignoreCase, MemberVoid.class);
    }

    @Override
    public <T extends Member> T loadMember(String key, String value, boolean ignoreCase, Class<T> clazz) {
        if (ignoreCase) {
            return this.loadMember(this.query.findOne(key, new Document("$regex", (Object)("^" + value + "$")).append("$options", (Object)"i")), clazz);
        }
        return this.loadMember(this.query.findOne(key, value), clazz);
    }

    public <T extends Member> T loadMember(JsonElement jsonElement, Class<T> clazz) {
        return (T)(jsonElement == null ? null : (Member)CommonConst.GSON.fromJson(CommonConst.GSON.toJson(jsonElement), clazz));
    }

    @Override
    public void reloadPlugins() {
        try (Jedis jedis = CommonPlugin.getInstance().getRedisConnection().getPool().getResource();){
            Set<String> keys = jedis.keys("account:*");
            for (String playerId : keys) {
                jedis.del("account:" + playerId);
            }
        }
        this.query.getCollection().updateMany((Bson)new Document(), Filters.eq((String)"$set", (Object)Filters.eq((String)"loginConfiguration.logged", (Object)false)));
        this.query.getCollection().updateMany((Bson)new Document(), Filters.eq((String)"$set", (Object)Filters.eq((String)"online", (Object)false)));
    }

    public <T extends Member> T getRedisPlayer(UUID uuid, Class<T> clazz) {
        Member player;
        try (Jedis jedis = this.redisDatabase.getPool().getResource()){
            if (!jedis.exists("account:" + uuid.toString())) {
                T t = null;
                return t;
            }
            Map<String, String> fields = jedis.hgetAll("account:" + uuid.toString());
            if (fields == null || fields.isEmpty() || fields.size() < Member.class.getDeclaredFields().length - 1) {
                T t = null;
                return t;
            }
            player = (Member)JsonUtils.mapToObject(fields, clazz);
        }
        return (T)((Member)clazz.cast(player));
    }

    @Override
    public boolean createMember(Member member) {
        boolean needCreate;
        boolean bl = needCreate = this.query.findOne("uniqueId", member.getUniqueId().toString()) == null;
        if (needCreate) {
            this.query.create(new String[]{CommonConst.GSON.toJson((Object)member)});
        }
        return needCreate;
    }

    @Override
    public void saveRedisMember(Member member) {
        try (Jedis jedis = this.redisDatabase.getPool().getResource();){
            jedis.hmset("account:" + member.getUniqueId().toString(), JsonUtils.objectToMap(member));
        }
    }

    @Override
    public boolean deleteMember(UUID uniqueId) {
        boolean needCreate;
        boolean bl = needCreate = this.query.findOne("uniqueId", uniqueId.toString()) == null;
        if (!needCreate) {
            this.query.deleteOne("uniqueId", uniqueId.toString());
            try (Jedis jedis = this.redisDatabase.getPool().getResource();){
                jedis.del("account:" + uniqueId.toString());
            }
            return true;
        }
        return false;
    }

    @Override
    public void updateMember(final Member member, final String fieldName) {
        CommonPlugin.getInstance().getPluginPlatform().runAsync(new Runnable(){

            @Override
            public void run() {
                JsonObject tree = JsonUtils.jsonTree(member);
                if (tree.has(fieldName)) {
                    JsonElement element = tree.get(fieldName);
                    try (Jedis jedis = MemberDataImpl.this.redisDatabase.getPool().getResource();){
                        Pipeline pipe = jedis.pipelined();
                        jedis.hset("account:" + member.getUniqueId().toString(), fieldName, JsonUtils.elementToString(element));
                        JsonObject json = new JsonObject();
                        json.add("uniqueId", (JsonElement)new JsonPrimitive(member.getUniqueId().toString()));
                        json.add("source", (JsonElement)new JsonPrimitive(CommonPlugin.getInstance().getServerId()));
                        json.add("field", (JsonElement)new JsonPrimitive(fieldName));
                        json.add("value", element);
                        pipe.publish("member_field", json.toString());
                        pipe.sync();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                MemberDataImpl.this.query.updateOne("uniqueId", member.getUniqueId().toString(), (JsonElement)new JsonBuilder().addProperty("fieldName", fieldName).add("value", tree.get(fieldName)).build());
            }
        });
    }

    @Override
    public void cacheMember(UUID uniqueId) {
        try (Jedis jedis = this.redisDatabase.getPool().getResource();){
            jedis.expire("account:" + uniqueId.toString(), 300);
        }
    }

    @Override
    public boolean checkCache(UUID uniqueId) {
        boolean bool = false;
        try (Jedis jedis = this.redisDatabase.getPool().getResource();){
            String key = "account:" + uniqueId.toString();
            if (jedis.ttl(key) >= 0L) {
                bool = jedis.persist(key) == 1L;
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return bool;
    }

    @Override
    public List<Member> getMembersByGroup(Group group) {
        ArrayList<Member> list = new ArrayList<Member>();
        MongoCursor iterator = this.query.getCollection().find(Filters.eq((String)("groups." + group.getGroupName().toLowerCase()), (Object)Filters.eq((String)"$exists", (Object)true))).limit(50).iterator();
        while (iterator.hasNext()) {
            list.add((Member)CommonConst.GSON.fromJson(CommonConst.GSON.toJson(iterator.next()), MemberVoid.class));
        }
        return list;
    }

    @Override
    public void closeConnection() {
        this.redisDatabase.close();
    }

    @Override
    public boolean isRedisCached(String playerName) {
        boolean bool = false;
        try (Jedis jedis = this.redisDatabase.getPool().getResource();){
            String key = "member:mojang-fetcher:" + playerName.toLowerCase();
            long ttl = jedis.ttl(key);
            if (ttl == -1L) {
                bool = true;
            } else if (ttl > 0L) {
                bool = jedis.persist(key) == 1L;
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return bool;
    }

    @Override
    public boolean isConnectionPremium(String playerName) {
        boolean bool = false;
        try (Jedis jedis = this.redisDatabase.getPool().getResource();){
            String key = "member:mojang-fetcher:" + playerName.toLowerCase();
            bool = StringFormat.parseBoolean(jedis.hget(key, "premium")).getAsBoolean();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return bool;
    }

    @Override
    public void setConnectionStatus(String playerName, UUID uniqueId, boolean premium) {
        try (Jedis jedis = this.redisDatabase.getPool().getResource();){
            String key = "member:mojang-fetcher:" + playerName.toLowerCase();
            jedis.hset(key, "premium", "" + premium);
            jedis.hset(key, "uniqueId", uniqueId.toString());
            jedis.expire(key, 900);
        }
    }

    @Override
    public UUID getUniqueId(String playerName) {
        UUID uniqueId = null;
        try (Jedis jedis = this.redisDatabase.getPool().getResource();){
            String key = "member:mojang-fetcher:" + playerName.toLowerCase();
            if (jedis.exists(key)) {
                uniqueId = UUID.fromString(jedis.hget(key, "uniqueId"));
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return uniqueId;
    }

    @Override
    public void cacheConnection(String playerName, boolean premium) {
        try (Jedis jedis = this.redisDatabase.getPool().getResource();){
            jedis.expire("member:mojang-fetcher:" + playerName.toLowerCase(), 3600);
        }
    }

    @Override
    public boolean isDiscordCached(String discordId) {
        boolean bool = false;
        try (Jedis jedis = this.redisDatabase.getPool().getResource();){
            String key = "discord:" + discordId;
            if (jedis.ttl(key) >= 0L) {
                bool = jedis.persist(key) == 1L;
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return bool;
    }

    @Override
    public UUID getUniqueIdFromDiscord(String discordId) {
        UUID uniqueId = null;
        try (Jedis jedis = this.redisDatabase.getPool().getResource();){
            String key = "discord:" + discordId;
            if (jedis.exists(key)) {
                uniqueId = UUID.fromString(jedis.get(key));
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return uniqueId;
    }

    @Override
    public void setDiscordCache(String discordId, UUID uniqueId) {
        try (Jedis jedis = this.redisDatabase.getPool().getResource();){
            jedis.set("discord:" + discordId, "uniqueId:" + uniqueId.toString());
        }
    }

    @Override
    public void deleteDiscordCache(String discordId) {
        try (Jedis jedis = this.redisDatabase.getPool().getResource();){
            jedis.del("discord:" + discordId);
        }
    }

    @Override
    public Status loadStatus(UUID uniqueId, StatusType statusType) {
        Document document = (Document)this.statusQuery.getDatabase().getCollection("status-" + statusType.name().toLowerCase()).find(Filters.eq((String)"uniqueId", (Object)uniqueId.toString())).first();
        if (document == null) {
            Status status = new Status(uniqueId, statusType);
            this.createStatus(status);
            return status;
        }
        return (Status)CommonConst.GSON.fromJson(CommonConst.GSON.toJson((Object)document), Status.class);
    }

    @Override
    public boolean createStatus(Status status) {
        Document document = (Document)this.statusQuery.getDatabase().getCollection("status-" + status.getStatusType().name().toLowerCase()).find(Filters.eq((String)"uniqueId", (Object)status.getUniqueId().toString())).first();
        if (document == null) {
            this.statusQuery.getDatabase().getCollection("status-" + status.getStatusType().name().toLowerCase()).insertOne(Document.parse((String)CommonConst.GSON.toJson((Object)status)));
            return true;
        }
        return false;
    }

    @Override
    public void saveStatus(final Status status, final String fieldName) {
        CommonPlugin.getInstance().getPluginPlatform().runAsync(new Runnable(){

            @Override
            public void run() {
                JsonObject tree = JsonUtils.jsonTree(status);
                JsonObject jsonObject = new JsonBuilder().addProperty("fieldName", fieldName).add("value", tree.get(fieldName)).build();
                Object object = JsonUtils.elementToBson(jsonObject.get("value"));
                if (object == null || jsonObject.get("value").isJsonNull()) {
                    MemberDataImpl.this.statusQuery.getDatabase().getCollection("status-" + status.getStatusType().name().toLowerCase()).updateOne(Filters.eq((String)"uniqueId", (Object)status.getUniqueId().toString()), (Bson)new Document("$unset", (Object)new Document(jsonObject.get("fieldName").getAsString(), (Object)"")));
                } else {
                    MemberDataImpl.this.statusQuery.getDatabase().getCollection("status-" + status.getStatusType().name().toLowerCase()).updateOne(Filters.eq((String)"uniqueId", (Object)status.getUniqueId().toString()), (Bson)new Document("$set", (Object)new Document(jsonObject.get("fieldName").getAsString(), object)));
                }
            }
        });
    }

    @Override
    public Collection<Report> loadReports() {
        return this.reportQuery.find().stream().map(json -> (Report)CommonConst.GSON.fromJson(json, Report.class)).collect(Collectors.toList());
    }

    @Override
    public void createReport(Report report) {
        JsonElement jsonElement = this.reportQuery.findOne("reportId", report.getReportId().toString());
        if (jsonElement == null) {
            this.reportQuery.create(new String[]{CommonConst.GSON.toJson((Object)report)});
        }
        CommonPlugin.getInstance().getPluginPlatform().runAsync(() -> CommonPlugin.getInstance().getServerData().sendPacket(new ReportCreatePacket(report)));
    }

    @Override
    public void deleteReport(UUID reportId) {
        CommonPlugin.getInstance().getPluginPlatform().runAsync(() -> CommonPlugin.getInstance().getServerData().sendPacket(new ReportDeletePacket(reportId)));
        this.reportQuery.deleteOne("reportId", reportId.toString());
    }

    @Override
    public void updateReport(final Report report, final String fieldName) {
        CommonPlugin.getInstance().getPluginPlatform().runAsync(new Runnable(){

            @Override
            public void run() {
                JsonObject tree = JsonUtils.jsonTree(report);
                CommonPlugin.getInstance().getServerData().sendPacket(new ReportFieldPacket(report, fieldName));
                MemberDataImpl.this.reportQuery.updateOne("reportId", report.getReportId().toString(), (JsonElement)new JsonBuilder().addProperty("fieldName", fieldName).add("value", tree.get(fieldName)).build());
            }
        });
    }

    public RedisConnection getRedisDatabase() {
        return this.redisDatabase;
    }

    @Override
    public MongoQuery getQuery() {
        return this.query;
    }

    public MongoQuery getStatusQuery() {
        return this.statusQuery;
    }

    public MongoQuery getReportQuery() {
        return this.reportQuery;
    }
}

