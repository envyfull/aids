/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.core.bukkit.menu.staff.server;

import java.util.Comparator;
import br.com.dragonmc.core.common.server.loadbalancer.server.ProxiedServer;

public enum ServerOrdenator implements Comparator<ProxiedServer>
{
    ALPHABETIC{

        @Override
        public int compare(ProxiedServer o1, ProxiedServer o2) {
            return o1.getServerId().compareTo(o2.getServerId());
        }
    }
    ,
    ONLINE_TIME{

        @Override
        public int compare(ProxiedServer o1, ProxiedServer o2) {
            return Long.compare(o1.getStartTime(), o2.getStartTime());
        }
    }
    ,
    TYPE{

        @Override
        public int compare(ProxiedServer o1, ProxiedServer o2) {
            return Integer.compare(o1.getServerType().ordinal(), o2.getServerType().ordinal());
        }
    };

}

