/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.core.common.command;

import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.common.packet.types.staff.Stafflog;
import br.com.dragonmc.core.common.server.ServerType;

public interface CommandClass {
    default public void staffLog(String message) {
        CommonPlugin.getInstance().getMemberManager().staffLog(message);
    }

    default public void staffLog(String message, boolean bungeecord) {
        if (bungeecord && CommonPlugin.getInstance().getServerType() != ServerType.BUNGEECORD) {
            CommonPlugin.getInstance().getServerData().sendPacket(new Stafflog("\u00a77[" + message + "\u00a77]"));
        } else {
            CommonPlugin.getInstance().getMemberManager().staffLog(message);
        }
    }
}

