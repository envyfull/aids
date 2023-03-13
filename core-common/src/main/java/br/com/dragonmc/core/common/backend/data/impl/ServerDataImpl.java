/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  redis.clients.jedis.Jedis
 *  redis.clients.jedis.Pipeline
 */
package br.com.dragonmc.core.common.backend.data.impl;

import br.com.dragonmc.core.common.backend.data.DataServerMessage;
import br.com.dragonmc.core.common.backend.data.ServerData;
import br.com.dragonmc.core.common.backend.mongodb.MongoQuery;
import br.com.dragonmc.core.common.backend.redis.RedisConnection;
import br.com.dragonmc.core.common.packet.Packet;
import br.com.dragonmc.core.common.server.ServerType;
import br.com.dragonmc.core.common.server.loadbalancer.server.MinigameState;
import br.com.dragonmc.core.common.server.loadbalancer.server.ProxiedServer;
import com.google.gson.JsonElement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import br.com.dragonmc.core.common.CommonConst;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.common.utils.json.JsonBuilder;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

public class ServerDataImpl
implements ServerData {
    private RedisConnection redisDatabase;

    public ServerDataImpl(RedisConnection redisConnection) {
        this.redisDatabase = redisConnection;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public int getTime(String serverId) {
        try (Jedis jedis = this.redisDatabase.getPool().getResource();){
            Map m = jedis.hgetAll("server:" + serverId);
            if (!m.containsKey("time")) return -1;
            int n = Integer.valueOf((String)m.get("time"));
            return n;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public long getStartTime(String serverId) {
        try (Jedis jedis = this.redisDatabase.getPool().getResource();){
            Map m = jedis.hgetAll("server:" + serverId);
            if (!m.containsKey("starttime")) return -1L;
            long l = Integer.valueOf((String)m.get("starttime")).intValue();
            return l;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return -1L;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public MinigameState getState(String serverId) {
        try (Jedis jedis = this.redisDatabase.getPool().getResource();){
            Map m = jedis.hgetAll("server:" + serverId);
            if (!m.containsKey("state")) return MinigameState.NONE;
            MinigameState minigameState = MinigameState.valueOf(((String)m.get("state")).toUpperCase());
            return minigameState;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return MinigameState.NONE;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public String getMap(String serverId) {
        try (Jedis jedis = this.redisDatabase.getPool().getResource();){
            Map m = jedis.hgetAll("server:" + serverId);
            if (!m.containsKey("map")) return "Unknown";
            String string = (String)m.get("map");
            return string;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return "Unknown";
    }

    @Override
    public Map<String, Map<String, String>> loadServers() {
        HashMap<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
        try (Jedis jedis = this.redisDatabase.getPool().getResource();){
            String[] str = new String[ServerType.values().length];
            for (int i = 0; i < ServerType.values().length; ++i) {
                str[i] = "server:type:" + ServerType.values()[i].toString().toLowerCase();
            }
            for (String server : jedis.sunion(str)) {
                Map m = jedis.hgetAll("server:" + server);
                map.put(server, m);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return new HashMap<String, Map<String, String>>();
        }
        return map;
    }

    @Override
    public Set<UUID> getPlayers(String serverId) {
        HashSet<UUID> players = new HashSet<UUID>();
        try (Jedis jedis = this.redisDatabase.getPool().getResource();){
            for (String uuid : jedis.smembers("server:" + serverId + ":players")) {
                UUID uniqueId = UUID.fromString(uuid);
                players.add(uniqueId);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return players;
    }

    @Override
    public void startServer(int maxPlayers) {
        try (Jedis jedis = this.redisDatabase.getPool().getResource();){
            Pipeline pipe = jedis.pipelined();
            pipe.sadd("server:type:" + CommonPlugin.getInstance().getServerType().toString().toLowerCase(), new String[]{CommonPlugin.getInstance().getServerId()});
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("type", CommonPlugin.getInstance().getServerType().toString().toLowerCase());
            map.put("maxplayers", maxPlayers + "");
            map.put("joinenabled", CommonPlugin.getInstance().isJoinEnabled() + "");
            map.put("address", CommonPlugin.getInstance().getServerAddress());
            map.put("map", CommonPlugin.getInstance().getMap());
            map.put("time", CommonPlugin.getInstance().getServerTime() + "");
            map.put("state", CommonPlugin.getInstance().getMinigameState().toString().toLowerCase());
            map.put("starttime", System.currentTimeMillis() + "");
            pipe.hmset("server:" + CommonPlugin.getInstance().getServerId(), map);
            pipe.del("server:" + CommonPlugin.getInstance().getServerId() + ":players");
            ProxiedServer server = new ProxiedServer(CommonPlugin.getInstance().getServerId(), CommonPlugin.getInstance().getServerType(), new HashSet<UUID>(), maxPlayers, CommonPlugin.getInstance().isJoinEnabled());
            pipe.publish("server_info", CommonConst.GSON.toJson(new DataServerMessage<DataServerMessage.StartPayload>(CommonPlugin.getInstance().getServerId(), CommonPlugin.getInstance().getServerType(), DataServerMessage.Action.START, new DataServerMessage.StartPayload(CommonPlugin.getInstance().getServerAddress(), server, System.currentTimeMillis()))));
            pipe.sync();
        }
    }

    @Override
    public void updateStatus() {
        this.updateStatus(CommonPlugin.getInstance().getMinigameState(), CommonPlugin.getInstance().getMap(), CommonPlugin.getInstance().getServerTime());
    }

    @Override
    public void updateStatus(MinigameState state, int time) {
        this.updateStatus(state, CommonPlugin.getInstance().getMap(), time);
    }

    @Override
    public void updateStatus(MinigameState state, String map, int time) {
        this.updateStatus(CommonPlugin.getInstance().getServerId(), state, map, time);
    }

    @Override
    public void updateStatus(String serverId, MinigameState state, String map, int time) {
        try (Jedis jedis = this.redisDatabase.getPool().getResource();){
            Pipeline pipe = jedis.pipelined();
            pipe.hset("server:" + serverId, "map", map);
            pipe.hset("server:" + serverId, "time", Integer.toString(time));
            pipe.hset("server:" + serverId, "state", state.toString().toLowerCase());
            pipe.publish("server_info", CommonConst.GSON.toJson(new DataServerMessage<DataServerMessage.UpdatePayload>(CommonPlugin.getInstance().getServerId(), CommonPlugin.getInstance().getServerType(), DataServerMessage.Action.UPDATE, new DataServerMessage.UpdatePayload(time, map, state))));
            pipe.sync();
        }
    }

    @Override
    public void setJoinEnabled(String serverId, boolean bol) {
        try (Jedis jedis = this.redisDatabase.getPool().getResource();){
            Pipeline pipe = jedis.pipelined();
            pipe.hset("server:" + serverId, "joinenabled", Boolean.toString(bol));
            pipe.publish("server_info", CommonConst.GSON.toJson(new DataServerMessage<DataServerMessage.JoinEnablePayload>(CommonPlugin.getInstance().getServerId(), CommonPlugin.getInstance().getServerType(), DataServerMessage.Action.JOIN_ENABLE, new DataServerMessage.JoinEnablePayload(bol))));
            pipe.sync();
        }
    }

    @Override
    public void setJoinEnabled(boolean bol) {
        this.setJoinEnabled(CommonPlugin.getInstance().getServerId(), bol);
    }

    @Override
    public void stopServer() {
        try (Jedis jedis = this.redisDatabase.getPool().getResource();){
            Pipeline pipe = jedis.pipelined();
            pipe.srem("server:type:" + CommonPlugin.getInstance().getServerType().toString().toLowerCase(), new String[]{CommonPlugin.getInstance().getServerId()});
            pipe.del("server:" + CommonPlugin.getInstance().getServerId());
            pipe.del("server:" + CommonPlugin.getInstance().getServerId() + ":players");
            pipe.publish("server_info", CommonConst.GSON.toJson(new DataServerMessage<DataServerMessage.StopPayload>(CommonPlugin.getInstance().getServerId(), CommonPlugin.getInstance().getServerType(), DataServerMessage.Action.STOP, new DataServerMessage.StopPayload(CommonPlugin.getInstance().getServerId()))));
            pipe.sync();
        }
    }

    @Override
    public void setTotalMembers(int totalMembers) {
        try (Jedis jedis = this.redisDatabase.getPool().getResource();){
            Pipeline pipe = jedis.pipelined();
            pipe.publish("server_members", CommonConst.GSON.toJson((JsonElement)new JsonBuilder().addProperty("totalMembers", totalMembers).build()));
            pipe.sync();
        }
    }

    @Override
    public void joinPlayer(UUID uuid, int maxPlayers) {
        try (Jedis jedis = this.redisDatabase.getPool().getResource();){
            Pipeline pipe = jedis.pipelined();
            pipe.sadd("server:" + CommonPlugin.getInstance().getServerId() + ":players", new String[]{uuid.toString()});
            pipe.hset("server:" + CommonPlugin.getInstance().getServerId(), "maxplayers", Integer.toString(maxPlayers));
            pipe.publish("server_info", CommonConst.GSON.toJson(new DataServerMessage<DataServerMessage.JoinPayload>(CommonPlugin.getInstance().getServerId(), CommonPlugin.getInstance().getServerType(), DataServerMessage.Action.JOIN, new DataServerMessage.JoinPayload(uuid, maxPlayers))));
            pipe.sync();
        }
    }

    @Override
    public void leavePlayer(UUID uuid, int maxPlayers) {
        try (Jedis jedis = this.redisDatabase.getPool().getResource();){
            Pipeline pipe = jedis.pipelined();
            pipe.srem("server:" + CommonPlugin.getInstance().getServerId() + ":players", new String[]{uuid.toString()});
            pipe.hset("server:" + CommonPlugin.getInstance().getServerId(), "maxplayers", Integer.toString(maxPlayers));
            pipe.publish("server_info", CommonConst.GSON.toJson(new DataServerMessage<DataServerMessage.LeavePayload>(CommonPlugin.getInstance().getServerId(), CommonPlugin.getInstance().getServerType(), DataServerMessage.Action.LEAVE, new DataServerMessage.LeavePayload(uuid, maxPlayers))));
            pipe.sync();
        }
    }

    @Override
    public void sendPacket(Packet packet) {
        try (Jedis jedis = this.redisDatabase.getPool().getResource();){
            Pipeline pipe = jedis.pipelined();
            pipe.publish("server_packet", CommonConst.GSON.toJson((Object)packet));
            pipe.sync();
        }
    }

    @Override
    public void closeConnection() {
        this.redisDatabase.close();
    }

    @Override
    public MongoQuery getQuery() {
        return null;
    }
}

