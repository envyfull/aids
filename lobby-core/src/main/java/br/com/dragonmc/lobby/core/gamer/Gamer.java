/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.lobby.core.gamer;

import java.util.UUID;

import br.com.dragonmc.lobby.core.wadgets.Particles;
import br.com.dragonmc.lobby.core.wadgets.Wings;
import br.com.dragonmc.core.common.member.Member;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Gamer {
    private String name;
    private UUID uniqueId;
    private boolean flying = false;
    private boolean usingParticle = false;
    private boolean cape = false;
    private Particles particle;
    private Wings wing;
    private double alpha = 0.0;

    public Gamer(Member member) {
        this.name = member.getName();
        this.uniqueId = member.getUniqueId();
    }

    public Player getPlayer() {
        return Bukkit.getPlayer((UUID)this.uniqueId);
    }

    public String getName() {
        return this.name;
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public boolean isFlying() {
        return this.flying;
    }

    public boolean isUsingParticle() {
        return this.usingParticle;
    }

    public boolean isCape() {
        return this.cape;
    }

    public Particles getParticle() {
        return this.particle;
    }

    public Wings getWing() {
        return this.wing;
    }

    public double getAlpha() {
        return this.alpha;
    }

    public void setFlying(boolean flying) {
        this.flying = flying;
    }

    public void setUsingParticle(boolean usingParticle) {
        this.usingParticle = usingParticle;
    }

    public void setCape(boolean cape) {
        this.cape = cape;
    }

    public void setParticle(Particles particle) {
        this.particle = particle;
    }

    public void setWing(Wings wing) {
        this.wing = wing;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }
}

