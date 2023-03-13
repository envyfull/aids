/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.io.ByteArrayDataInput
 *  com.google.common.io.ByteStreams
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  com.google.gson.reflect.TypeToken
 *  net.md_5.bungee.BungeeCord
 *  net.md_5.bungee.api.CommandSender
 *  net.md_5.bungee.api.ProxyServer
 *  net.md_5.bungee.api.connection.ProxiedPlayer
 *  net.md_5.bungee.api.connection.Server
 *  net.md_5.bungee.api.event.PlayerDisconnectEvent
 *  net.md_5.bungee.api.event.PluginMessageEvent
 *  net.md_5.bungee.api.event.PostLoginEvent
 *  net.md_5.bungee.api.plugin.Event
 *  net.md_5.bungee.api.plugin.Listener
 *  net.md_5.bungee.event.EventHandler
 */
package br.com.dragonmc.core.bungee.listener;

import br.com.dragonmc.core.bungee.BungeeMain;
import br.com.dragonmc.core.bungee.event.packet.PacketReceiveEvent;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
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
import br.com.dragonmc.core.bungee.command.BungeeCommandSender;
import br.com.dragonmc.core.bungee.event.RedisMessageEvent;
import br.com.dragonmc.core.bungee.event.ServerUpdateEvent;
import br.com.dragonmc.core.bungee.event.player.PlayerFieldUpdateEvent;
import br.com.dragonmc.core.bungee.event.player.PlayerPunishEvent;
import br.com.dragonmc.core.bungee.member.BungeeMember;
import br.com.dragonmc.core.common.member.Member;
import br.com.dragonmc.core.common.packet.Packet;
import br.com.dragonmc.core.common.packet.PacketType;
import br.com.dragonmc.core.common.packet.types.PunishPlayerPacket;
import br.com.dragonmc.core.common.packet.types.skin.SkinChange;
import br.com.dragonmc.core.common.packet.types.staff.TeleportToTarget;
import br.com.dragonmc.core.common.punish.Punish;
import br.com.dragonmc.core.common.server.ServerType;
import br.com.dragonmc.core.common.server.loadbalancer.server.MinigameServer;
import br.com.dragonmc.core.common.server.loadbalancer.server.MinigameState;
import br.com.dragonmc.core.common.server.loadbalancer.server.ProxiedServer;
import br.com.dragonmc.core.common.utils.reflection.Reflection;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class DataListener
implements Listener {
    @EventHandler(priority=-128)
    public void onPluginMessage(PluginMessageEvent event) {
        String subChannel;
        if (!(event.getTag().equals("BungeeCord") && event.getSender() instanceof Server && event.getReceiver() instanceof ProxiedPlayer)) {
            return;
        }
        ProxiedPlayer proxiedPlayer = (ProxiedPlayer)event.getReceiver();
        Member player = CommonPlugin.getInstance().getMemberManager().getMember(proxiedPlayer.getUniqueId());
        ByteArrayDataInput in = ByteStreams.newDataInput((byte[])event.getData());
        switch (subChannel = in.readUTF()) {
            case "BungeeCommand": {
                ProxyServer.getInstance().getPluginManager().dispatchCommand((CommandSender)proxiedPlayer, in.readUTF());
                break;
            }
            case "PlayerConnect": {
                String serverId = in.readUTF();
                boolean silent = in.readBoolean();
                ProxiedServer server = BungeeMain.getInstance().getServerManager().getServer(serverId);
                if (server == null || server.getServerInfo() == null) {
                    if (!silent) {
                        player.sendMessage(player.getLanguage().t("server-not-found", "%server%", serverId));
                    }
                    return;
                }
                proxiedPlayer.connect(server.getServerInfo());
                break;
            }
            case "SearchServer": {
                String[] stringArray;
                String server = in.readUTF();
                boolean silent = in.readBoolean();
                if (server.contains("-")) {
                    stringArray = server.split("-");
                } else {
                    String[] stringArray2 = new String[1];
                    stringArray = stringArray2;
                    stringArray2[0] = server;
                }
                for (String s : stringArray) {
                    try {
                        ServerType serverType = ServerType.valueOf(s);
                        if (!this.searchServer(player, proxiedPlayer, serverType, silent)) continue;
                        return;
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
                if (silent) break;
                player.sendMessage(player.getLanguage().t("server.search-server.not-found", new String[0]));
                break;
            }
        }
    }

    public boolean searchServer(Member player, ProxiedPlayer proxiedPlayer, ServerType serverType, boolean silent) {
        ProxiedServer server = (ProxiedServer)BungeeMain.getInstance().getServerManager().getBalancer(serverType).next();
        if (server == null || server.getServerInfo() == null) {
            return false;
        }
        if (server.isFull() && !player.hasPermission("server.full")) {
            if (!silent) {
                proxiedPlayer.sendMessage(player.getLanguage().t("server-is-full", new String[0]));
            }
            return true;
        }
        if (!server.canBeSelected() && !player.hasPermission("command.admin")) {
            if (!silent) {
                proxiedPlayer.sendMessage(player.getLanguage().t("server-not-available", new String[0]));
            }
            return true;
        }
        proxiedPlayer.connect(server.getServerInfo());
        return true;
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        CommonPlugin.getInstance().getServerData().setTotalMembers(BungeeCord.getInstance().getOnlineCount());
        BungeeMain.getInstance().setPlayersRecord(Math.max(BungeeCord.getInstance().getOnlineCount(), BungeeMain.getInstance().getPlayersRecord()));
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        CommonPlugin.getInstance().getServerData().setTotalMembers(BungeeCord.getInstance().getOnlineCount() - 1);
    }

    @EventHandler
    public void onRedisMessage(RedisMessageEvent event) {
        String message = event.getMessage();
        try {
            if (message.startsWith("{") && message.endsWith("}")) {
                this.handleMessageGson(event.getChannel(), JsonParser.parseString((String)message).getAsJsonObject());
                return;
            }
        }
        catch (Exception ex) {
            CommonPlugin.getInstance().getLogger().log(Level.WARNING, "An error occured when reading json packet in redis!\n" + message, ex);
            System.out.println(ex.getLocalizedMessage());
        }
    }

    @EventHandler
    public void onPacketReceive(PacketReceiveEvent event) {
        switch (event.getPacketType()) {
            case TELEPORT_TO_TARGET: {
                TeleportToTarget teleportToTarget = (TeleportToTarget)event.getPacket();
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(teleportToTarget.getPlayerId());
                if (player == null) {
                    return;
                }
                ProxiedPlayer target = ProxyServer.getInstance().getPlayer(teleportToTarget.getTargetId());
                if (target == null) {
                    player.sendMessage("\u00a7cO jogador " + teleportToTarget.getTargetName() + " n\u00e3o foi encontrado.");
                    return;
                }
                BungeeMain.getInstance().teleport(player, target);
                break;
            }
            case SKIN_CHANGE: {
                SkinChange skinChange = (SkinChange)event.getPacket();
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(skinChange.getPlayerId());
                if (player == null) {
                    return;
                }
                BungeeMain.getInstance().loadTexture(player.getPendingConnection(), skinChange.getSkin());
                break;
            }
            case PUNISH_PLAYER: {
                PunishPlayerPacket punishPlayer = (PunishPlayerPacket)event.getPacket();
                Member member = CommonPlugin.getInstance().getMemberManager().getMember(punishPlayer.getPlayerId());
                Punish punish = punishPlayer.getPunish();
                if (member == null) {
                    member = CommonPlugin.getInstance().getMemberData().loadMember(punishPlayer.getPlayerId());
                }
                ProxyServer.getInstance().getPluginManager().callEvent((Event)new PlayerPunishEvent(member, punishPlayer.getPunish(), CommonConst.CONSOLE_ID.equals(punish.getPunisherId()) ? new BungeeCommandSender(ProxyServer.getInstance().getConsole()) : CommonPlugin.getInstance().getMemberManager().getMember(punish.getPunisherId())));
                break;
            }
        }
    }

    void handleMessageGson(String channel, JsonObject jsonObject) {
        if (CommonPlugin.getInstance().getPluginInfo().isRedisDebugEnabled()) {
            CommonPlugin.getInstance().debug("Redis message from channel " + channel + ": " + jsonObject);
        }
        block7 : switch (channel) {
            case "server_packet": {
                PacketType packetType = PacketType.valueOf(jsonObject.get("packetType").getAsString());
                Packet packet = (Packet)CommonConst.GSON.fromJson((JsonElement)jsonObject, packetType.getClassType());
                if (packet.isExclusiveServers() && (!packet.isExclusiveServers() || !packet.getServerList().contains(CommonPlugin.getInstance().getServerId()))) break;
                packet.receive();
                ProxyServer.getInstance().getPluginManager().callEvent((Event)new PacketReceiveEvent(packet));
                break;
            }
            case "member_field": {
                Object object;
                UUID uuid = UUID.fromString(jsonObject.getAsJsonPrimitive("uniqueId").getAsString());
                ProxiedPlayer p = ProxyServer.getInstance().getPlayer(uuid);
                if (p == null) {
                    return;
                }
                Member player = CommonPlugin.getInstance().getMemberManager().getMember(uuid);
                if (player == null) {
                    return;
                }
                try {
                    Field f = Reflection.getField(Member.class, jsonObject.get("field").getAsString());
                    object = CommonConst.GSON.fromJson(jsonObject.get("value"), f.getGenericType());
                    f.setAccessible(true);
                    f.set(player, object);
                    ProxyServer.getInstance().getPluginManager().callEvent((Event)new PlayerFieldUpdateEvent((BungeeMember)player, jsonObject.get("field").getAsString()));
                }
                catch (IllegalAccessException | IllegalArgumentException | SecurityException e) {
                    e.printStackTrace();
                }
                if (!jsonObject.get("field").getAsString().toLowerCase().contains("configuration")) break;
                try {
                    Field field = Member.class.getDeclaredField(jsonObject.get("field").getAsString());
                    field.setAccessible(true);
                    object = field.get(player);
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
                String source = jsonObject.get("source").getAsString();
                ServerType sourceType = ServerType.valueOf(jsonObject.get("serverType").getAsString());
                DataServerMessage.Action action = DataServerMessage.Action.valueOf(jsonObject.get("action").getAsString());
                if (sourceType == ServerType.BUNGEECORD) break;
                switch (action) {
                    case JOIN: {
                        DataServerMessage payload = (DataServerMessage)CommonConst.GSON.fromJson((JsonElement)jsonObject, new TypeToken<DataServerMessage<DataServerMessage.JoinPayload>>(){}.getType());
                        ProxiedServer server = BungeeMain.getInstance().getServerManager().getServer(source);
                        if (server == null) {
                            return;
                        }
                        server.joinPlayer(((DataServerMessage.JoinPayload)payload.getPayload()).getUniqueId());
                        server.setMaxPlayers(((DataServerMessage.JoinPayload)payload.getPayload()).getMaxPlayers());
                        break block7;
                    }
                    case LEAVE: {
                        DataServerMessage payload = (DataServerMessage)CommonConst.GSON.fromJson((JsonElement)jsonObject, new TypeToken<DataServerMessage<DataServerMessage.LeavePayload>>(){}.getType());
                        ProxiedServer server = BungeeMain.getInstance().getServerManager().getServer(source);
                        if (server == null) {
                            return;
                        }
                        server.leavePlayer(((DataServerMessage.LeavePayload)payload.getPayload()).getUniqueId());
                        server.setMaxPlayers(((DataServerMessage.LeavePayload)payload.getPayload()).getMaxPlayers());
                        break block7;
                    }
                    case JOIN_ENABLE: {
                        DataServerMessage payload = (DataServerMessage)CommonConst.GSON.fromJson((JsonElement)jsonObject, new TypeToken<DataServerMessage<DataServerMessage.JoinEnablePayload>>(){}.getType());
                        ProxiedServer server = BungeeMain.getInstance().getServerManager().getServer(source);
                        if (server == null) {
                            return;
                        }
                        server.setJoinEnabled(((DataServerMessage.JoinEnablePayload)payload.getPayload()).isEnable());
                        break block7;
                    }
                    case START: {
                        DataServerMessage payload = (DataServerMessage)CommonConst.GSON.fromJson((JsonElement)jsonObject, new TypeToken<DataServerMessage<DataServerMessage.StartPayload>>(){}.getType());
                        BungeeMain.getInstance().getServerManager().addActiveServer(((DataServerMessage.StartPayload)payload.getPayload()).getServerAddress(), ((DataServerMessage.StartPayload)payload.getPayload()).getServer().getServerId(), sourceType, ((DataServerMessage.StartPayload)payload.getPayload()).getServer().getMaxPlayers(), ((DataServerMessage.StartPayload)payload.getPayload()).getStartTime());
                        break block7;
                    }
                    case STOP: {
                        DataServerMessage payload = (DataServerMessage)CommonConst.GSON.fromJson((JsonElement)jsonObject, new TypeToken<DataServerMessage<DataServerMessage.StopPayload>>(){}.getType());
                        if (sourceType == ServerType.BUNGEECORD) break block7;
                        BungeeMain.getInstance().getServerManager().removeActiveServer(((DataServerMessage.StopPayload)payload.getPayload()).getServerId());
                        break block7;
                    }
                    case UPDATE: {
                        DataServerMessage payload = (DataServerMessage)CommonConst.GSON.fromJson((JsonElement)jsonObject, new TypeToken<DataServerMessage<DataServerMessage.UpdatePayload>>(){}.getType());
                        ProxiedServer server = BungeeMain.getInstance().getServerManager().getServer(source);
                        if (server == null) {
                            return;
                        }
                        if (!(server instanceof MinigameServer)) break block7;
                        MinigameServer minigame = (MinigameServer)server;
                        MinigameState lastState = minigame.getState();
                        minigame.setState(((DataServerMessage.UpdatePayload)payload.getPayload()).getState());
                        minigame.setTime(((DataServerMessage.UpdatePayload)payload.getPayload()).getTime());
                        minigame.setMap(((DataServerMessage.UpdatePayload)payload.getPayload()).getMap());
                        ProxyServer.getInstance().getPluginManager().callEvent((Event)new ServerUpdateEvent(minigame, ((DataServerMessage.UpdatePayload)payload.getPayload()).getMap(), ((DataServerMessage.UpdatePayload)payload.getPayload()).getTime(), lastState, ((DataServerMessage.UpdatePayload)payload.getPayload()).getState()));
                        break block7;
                    }
                }
                break;
            }
        }
    }
}

