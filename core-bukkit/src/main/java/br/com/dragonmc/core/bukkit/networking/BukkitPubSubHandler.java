/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  com.google.gson.reflect.TypeToken
 *  org.bukkit.Bukkit
 *  org.bukkit.event.Event
 *  redis.clients.jedis.JedisPubSub
 */
package br.com.dragonmc.core.bukkit.networking;

import br.com.dragonmc.core.bukkit.BukkitCommon;
import br.com.dragonmc.core.bukkit.event.member.PlayerUpdateFieldEvent;
import br.com.dragonmc.core.bukkit.event.member.PlayerUpdatedFieldEvent;
import br.com.dragonmc.core.bukkit.event.server.PlayerChangeEvent;
import br.com.dragonmc.core.bukkit.event.server.ServerEvent;
import br.com.dragonmc.core.bukkit.event.server.ServerPacketReceiveEvent;
import br.com.dragonmc.core.bukkit.member.BukkitMember;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Field;
import java.util.UUID;
import java.util.logging.Level;

import br.com.dragonmc.core.common.CommonConst;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.common.backend.data.DataServerMessage;
import br.com.dragonmc.core.common.member.Member;
import br.com.dragonmc.core.common.packet.Packet;
import br.com.dragonmc.core.common.packet.PacketType;
import br.com.dragonmc.core.common.server.ServerType;
import br.com.dragonmc.core.common.server.loadbalancer.server.MinigameServer;
import br.com.dragonmc.core.common.server.loadbalancer.server.ProxiedServer;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import redis.clients.jedis.JedisPubSub;

public class BukkitPubSubHandler
extends JedisPubSub {
    public void onMessage(String channel, String message) {
        try {
            JsonObject jsonObject = (JsonObject)JsonParser.parseString((String)message);
            if (CommonPlugin.getInstance().getPluginInfo().isRedisDebugEnabled()) {
                CommonPlugin.getInstance().debug("Redis message from channel " + channel + ": " + jsonObject);
            }
            block9 : switch (channel) {
                case "server_packet": {
                    PacketType packetType = PacketType.valueOf(jsonObject.get("packetType").getAsString());
                    Packet packet = (Packet)CommonConst.GSON.fromJson((JsonElement)jsonObject, packetType.getClassType());
                    if (packet.isExclusiveServers() && (!packet.isExclusiveServers() || !packet.getServerList().contains(CommonPlugin.getInstance().getServerId()))) break;
                    packet.receive();
                    Bukkit.getPluginManager().callEvent((Event)new ServerPacketReceiveEvent(packetType, packet));
                    break;
                }
                case "server_members": {
                    BukkitCommon.getInstance().getServerManager().setTotalMembers(jsonObject.get("totalMembers").getAsInt());
                    Bukkit.getPluginManager().callEvent((Event)new PlayerChangeEvent(jsonObject.get("totalMembers").getAsInt()));
                    break;
                }
                case "member_field": {
                    Field field;
                    if (!jsonObject.has("source") || jsonObject.get("source").getAsString().equals(CommonPlugin.getInstance().getServerId())) break;
                    UUID uuid = UUID.fromString(jsonObject.get("uniqueId").getAsString());
                    BukkitMember player = CommonPlugin.getInstance().getMemberManager().getMember(uuid, BukkitMember.class);
                    boolean pass = false;
                    if (player == null) break;
                    try {
                        field = this.getField(Member.class, jsonObject.get("field").getAsString());
                        Object oldObject = field.get(player);
                        Object object = CommonConst.GSON.fromJson(jsonObject.get("value"), field.getGenericType());
                        PlayerUpdateFieldEvent event = new PlayerUpdateFieldEvent(Bukkit.getServer().getPlayer(uuid), player, field.getName(), oldObject, object);
                        Bukkit.getPluginManager().callEvent((Event)event);
                        if (!event.isCancelled()) {
                            field.set(player, event.getObject());
                            Bukkit.getPluginManager().callEvent((Event)new PlayerUpdatedFieldEvent(Bukkit.getServer().getPlayer(uuid), player, field.getName(), oldObject, object));
                            pass = true;
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (!pass || !jsonObject.get("field").getAsString().toLowerCase().contains("configuration")) break;
                    try {
                        field = Member.class.getDeclaredField(jsonObject.get("field").getAsString());
                        field.setAccessible(true);
                        Object object = field.get(player);
                        Field memberField = object.getClass().getDeclaredField("member");
                        memberField.setAccessible(true);
                        memberField.set(field.get(player), player);
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    break;
                }
                case "server_info": {
                    if (!BukkitCommon.getInstance().isServerLog()) {
                        return;
                    }
                    ServerType sourceType = ServerType.valueOf(jsonObject.get("serverType").getAsString());
                    if (sourceType == ServerType.BUNGEECORD) {
                        return;
                    }
                    String source = jsonObject.get("source").getAsString();
                    DataServerMessage.Action action = DataServerMessage.Action.valueOf(jsonObject.get("action").getAsString());
                    switch (action) {
                        case JOIN: {
                            DataServerMessage payload = (DataServerMessage)CommonConst.GSON.fromJson((JsonElement)jsonObject, new TypeToken<DataServerMessage<DataServerMessage.JoinPayload>>(){}.getType());
                            ProxiedServer server = BukkitCommon.getInstance().getServerManager().getServer(source);
                            if (server == null) {
                                return;
                            }
                            server.joinPlayer(((DataServerMessage.JoinPayload)payload.getPayload()).getUniqueId());
                            Bukkit.getPluginManager().callEvent((Event)new ServerEvent(source, sourceType, server, payload, action));
                            break block9;
                        }
                        case LEAVE: {
                            DataServerMessage payload = (DataServerMessage)CommonConst.GSON.fromJson((JsonElement)jsonObject, new TypeToken<DataServerMessage<DataServerMessage.LeavePayload>>(){}.getType());
                            ProxiedServer server = BukkitCommon.getInstance().getServerManager().getServer(source);
                            if (server == null) {
                                return;
                            }
                            server.leavePlayer(((DataServerMessage.LeavePayload)payload.getPayload()).getUniqueId());
                            Bukkit.getPluginManager().callEvent((Event)new ServerEvent(source, sourceType, server, payload, action));
                            break block9;
                        }
                        case JOIN_ENABLE: {
                            DataServerMessage payload = (DataServerMessage)CommonConst.GSON.fromJson((JsonElement)jsonObject, new TypeToken<DataServerMessage<DataServerMessage.JoinEnablePayload>>(){}.getType());
                            ProxiedServer server = BukkitCommon.getInstance().getServerManager().getServer(source);
                            if (server == null) {
                                return;
                            }
                            server.setJoinEnabled(((DataServerMessage.JoinEnablePayload)payload.getPayload()).isEnable());
                            break block9;
                        }
                        case START: {
                            DataServerMessage payload = (DataServerMessage)CommonConst.GSON.fromJson((JsonElement)jsonObject, new TypeToken<DataServerMessage<DataServerMessage.StartPayload>>(){}.getType());
                            Bukkit.getPluginManager().callEvent((Event)new ServerEvent(source, sourceType, BukkitCommon.getInstance().getServerManager().addActiveServer(((DataServerMessage.StartPayload)payload.getPayload()).getServerAddress(), ((DataServerMessage.StartPayload)payload.getPayload()).getServer().getServerId(), sourceType, ((DataServerMessage.StartPayload)payload.getPayload()).getServer().getMaxPlayers(), ((DataServerMessage.StartPayload)payload.getPayload()).getStartTime()), payload, action));
                            break block9;
                        }
                        case STOP: {
                            DataServerMessage payload = (DataServerMessage)CommonConst.GSON.fromJson((JsonElement)jsonObject, new TypeToken<DataServerMessage<DataServerMessage.StopPayload>>(){}.getType());
                            BukkitCommon.getInstance().getServerManager().removeActiveServer(((DataServerMessage.StopPayload)payload.getPayload()).getServerId());
                            Bukkit.getPluginManager().callEvent((Event)new ServerEvent(source, sourceType, null, payload, action));
                            break block9;
                        }
                        case UPDATE: {
                            DataServerMessage payload = (DataServerMessage)CommonConst.GSON.fromJson((JsonElement)jsonObject, new TypeToken<DataServerMessage<DataServerMessage.UpdatePayload>>(){}.getType());
                            ProxiedServer server = BukkitCommon.getInstance().getServerManager().getServer(source);
                            if (server == null) {
                                return;
                            }
                            if (!(server instanceof MinigameServer)) break block9;
                            ((MinigameServer)server).setState(((DataServerMessage.UpdatePayload)payload.getPayload()).getState());
                            ((MinigameServer)server).setTime(((DataServerMessage.UpdatePayload)payload.getPayload()).getTime());
                            ((MinigameServer)server).setMap(((DataServerMessage.UpdatePayload)payload.getPayload()).getMap());
                            Bukkit.getPluginManager().callEvent((Event)new ServerEvent(source, sourceType, server, payload, action));
                            break block9;
                        }
                    }
                    break;
                }
            }
        }
        catch (Exception ex) {
            CommonPlugin.getInstance().getLogger().log(Level.WARNING, "An error occured when reading json packet in redis " + channel + "!\n" + message, ex);
            System.out.println(ex.getLocalizedMessage());
        }
    }

    private Field getField(Class<?> clazz, String fieldName) {
        while (clazz != null && clazz != Object.class) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field;
            }
            catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }
}

