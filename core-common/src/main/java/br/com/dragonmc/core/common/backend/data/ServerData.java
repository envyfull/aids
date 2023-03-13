/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.core.common.backend.data;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import br.com.dragonmc.core.common.backend.mongodb.MongoQuery;
import br.com.dragonmc.core.common.packet.Packet;
import br.com.dragonmc.core.common.server.loadbalancer.server.MinigameState;

public interface ServerData
extends Data<MongoQuery> {
    public int getTime(String var1);

    public long getStartTime(String var1);

    public MinigameState getState(String var1);

    public String getMap(String var1);

    public Map<String, Map<String, String>> loadServers();

    public Set<UUID> getPlayers(String var1);

    public void startServer(int var1);

    public void updateStatus(String var1, MinigameState var2, String var3, int var4);

    public void updateStatus(MinigameState var1, String var2, int var3);

    public void updateStatus(MinigameState var1, int var2);

    public void updateStatus();

    public void setJoinEnabled(String var1, boolean var2);

    public void setJoinEnabled(boolean var1);

    public void stopServer();

    public void setTotalMembers(int var1);

    public void joinPlayer(UUID var1, int var2);

    public void leavePlayer(UUID var1, int var2);

    public void sendPacket(Packet var1);

    public void closeConnection();
}

