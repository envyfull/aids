/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.core.common.backend.data;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import br.com.dragonmc.core.common.backend.mongodb.MongoQuery;
import br.com.dragonmc.core.common.member.Member;
import br.com.dragonmc.core.common.member.status.Status;
import br.com.dragonmc.core.common.member.status.StatusType;
import br.com.dragonmc.core.common.permission.Group;
import br.com.dragonmc.core.common.report.Report;

public interface MemberData
extends Data<MongoQuery> {
    public Member loadMember(UUID var1);

    public <T extends Member> T loadMember(UUID var1, Class<T> var2);

    public Member loadMember(String var1, boolean var2);

    public <T extends Member> T loadMember(String var1, boolean var2, Class<T> var3);

    public <T extends Member> Collection<T> loadMembersByAddress(String var1, Class<T> var2);

    public <T extends Member> T loadMember(String var1, String var2, boolean var3, Class<T> var4);

    public List<Member> getMembersByGroup(Group var1);

    public boolean createMember(Member var1);

    public boolean deleteMember(UUID var1);

    public void updateMember(Member var1, String var2);

    public void reloadPlugins();

    public void cacheMember(UUID var1);

    public void saveRedisMember(Member var1);

    public boolean checkCache(UUID var1);

    public boolean isRedisCached(String var1);

    public boolean isConnectionPremium(String var1);

    public void setConnectionStatus(String var1, UUID var2, boolean var3);

    public void cacheConnection(String var1, boolean var2);

    public UUID getUniqueId(String var1);

    public boolean isDiscordCached(String var1);

    public UUID getUniqueIdFromDiscord(String var1);

    public void setDiscordCache(String var1, UUID var2);

    public void deleteDiscordCache(String var1);

    public Status loadStatus(UUID var1, StatusType var2);

    public boolean createStatus(Status var1);

    public void saveStatus(Status var1, String var2);

    public Collection<Report> loadReports();

    public void createReport(Report var1);

    public void deleteReport(UUID var1);

    public void updateReport(Report var1, String var2);

    public void closeConnection();
}

