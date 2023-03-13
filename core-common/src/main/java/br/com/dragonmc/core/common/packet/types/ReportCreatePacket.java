/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.core.common.packet.types;

import br.com.dragonmc.core.common.report.Report;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.common.packet.Packet;
import br.com.dragonmc.core.common.packet.PacketType;

public class ReportCreatePacket
extends Packet {
    private Report report;

    public ReportCreatePacket(Report report) {
        super(PacketType.REPORT_CREATE);
        this.report = report;
    }

    @Override
    public void receive() {
        CommonPlugin.getInstance().getReportManager().loadReport(this.report);
    }
}

