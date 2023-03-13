/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.core.common.member.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import br.com.dragonmc.core.common.command.CommandSender;
import br.com.dragonmc.core.common.member.Member;
import br.com.dragonmc.core.common.punish.Punish;
import br.com.dragonmc.core.common.punish.PunishType;

public class PunishConfiguration {
    private transient Member member;
    private Map<PunishType, List<Punish>> punishMap;

    public PunishConfiguration(Member member) {
        this.member = member;
        this.punishMap = new HashMap<PunishType, List<Punish>>();
    }

    public Punish getActualPunish(PunishType punishType) {
        return this.punishMap.computeIfAbsent(punishType, v -> new ArrayList()).stream().filter(punish -> !punish.isUnpunished() && !punish.hasExpired()).findFirst().orElse(null);
    }

    public Punish getPunishById(String id, PunishType punishType) {
        return this.punishMap.computeIfAbsent(punishType, v -> new ArrayList()).stream().filter(punish -> punish.getId().equals(id)).findFirst().orElse(null);
    }

    public Collection<Punish> getPunish(PunishType punishType) {
        return this.punishMap.containsKey((Object)punishType) ? (Collection)this.punishMap.get((Object)punishType) : new ArrayList<Punish>();
    }

    public Collection<Punish> getPunishById(UUID punisherId, PunishType punishType) {
        return this.punishMap.computeIfAbsent(punishType, v -> new ArrayList()).stream().filter(punish -> punish.getPunisherId() == punisherId).collect(Collectors.toList());
    }

    public Collection<Punish> getPunishByName(String punisherName, PunishType punishType) {
        return this.punishMap.computeIfAbsent(punishType, v -> new ArrayList()).stream().filter(punish -> punish.getPunisherName().equals(punisherName)).collect(Collectors.toList());
    }

    public boolean pardon(Punish punish, CommandSender sender) {
        List<Punish> list = this.punishMap.computeIfAbsent(punish.getPunishType(), v -> new ArrayList());
        if (list.stream().filter(p -> p.getId().equals(punish.getId())).findFirst().isPresent()) {            punish.unpunish(sender);
            return true;
        }
        return false;
    }

    public void punish(Punish punish) {
        this.punishMap.computeIfAbsent(punish.getPunishType(), v -> new ArrayList()).add(punish);
    }

    public void loadConfiguration(Member member) {
        this.member = member;
    }

    public Member getMember() {
        return this.member;
    }

    public Map<PunishType, List<Punish>> getPunishMap() {
        return this.punishMap;
    }
}

