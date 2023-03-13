/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.core.bukkit.menu.staff.punish;

import java.util.Comparator;
import br.com.dragonmc.core.common.punish.Punish;

public enum PunishOrdenator implements Comparator<Punish>
{
    ALPHABETIC{

        @Override
        public int compare(Punish o1, Punish o2) {
            return o1.getPlayerName().compareTo(o2.getPlayerName());
        }
    }
    ,
    EXPIRE_TIME{

        @Override
        public int compare(Punish o1, Punish o2) {
            return Long.compare(o1.getExpireAt(), o2.getExpireAt()) * -1;
        }
    }
    ,
    CREATION_TIME{

        @Override
        public int compare(Punish o1, Punish o2) {
            return Long.compare(o1.getCreatedAt(), o2.getCreatedAt());
        }
    };

}

