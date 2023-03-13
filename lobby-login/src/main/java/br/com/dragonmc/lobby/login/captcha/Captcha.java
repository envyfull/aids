/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.lobby.login.captcha;

import br.com.dragonmc.core.common.utils.Callback;
import org.bukkit.entity.Player;

public interface Captcha {
    public void verify(Player var1, Callback<Boolean> var2);
}

