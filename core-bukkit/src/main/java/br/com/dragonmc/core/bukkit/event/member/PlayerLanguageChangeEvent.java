/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 *  org.bukkit.entity.Player
 */
package br.com.dragonmc.core.bukkit.event.member;

import br.com.dragonmc.core.bukkit.event.PlayerEvent;
import lombok.NonNull;
import br.com.dragonmc.core.common.language.Language;
import org.bukkit.entity.Player;

public class PlayerLanguageChangeEvent
extends PlayerEvent {
    private Language language;

    public PlayerLanguageChangeEvent(@NonNull Player player, @NonNull Language language) {
        super(player);
        if (player == null) {
            throw new NullPointerException("player is marked non-null but is null");
        }
        if (language == null) {
            throw new NullPointerException("language is marked non-null but is null");
        }
        this.language = language;
    }

    public Language getLanguage() {
        return this.language;
    }
}

