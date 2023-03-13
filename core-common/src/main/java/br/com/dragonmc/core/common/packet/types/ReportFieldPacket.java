/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 */
package br.com.dragonmc.core.common.packet.types;

import br.com.dragonmc.core.common.report.Report;
import br.com.dragonmc.core.common.utils.reflection.Reflection;
import com.google.gson.JsonObject;
import java.lang.reflect.Field;
import java.util.UUID;

import br.com.dragonmc.core.common.CommonConst;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.common.packet.Packet;
import br.com.dragonmc.core.common.packet.PacketType;
import br.com.dragonmc.core.common.utils.json.JsonBuilder;
import br.com.dragonmc.core.common.utils.json.JsonUtils;

public class ReportFieldPacket
extends Packet {
    private UUID reportId;
    private JsonObject jsonObject;

    public ReportFieldPacket(Report report, String fieldName) {
        super(PacketType.REPORT_FIELD);
        JsonObject tree = JsonUtils.jsonTree(report);
        this.reportId = report.getReportId();
        this.jsonObject = new JsonBuilder().addProperty("fieldName", fieldName).add("value", tree.get(fieldName)).build();
    }

    @Override
    public void receive() {
        Report report = CommonPlugin.getInstance().getReportManager().getReportById(this.reportId);
        if (report != null) {
            try {
                Field f = Reflection.getField(Report.class, this.jsonObject.get("fieldName").getAsString());
                Object object = CommonConst.GSON.fromJson(this.jsonObject.get("value"), f.getGenericType());
                f.setAccessible(true);
                f.set(report, object);
            }
            catch (IllegalAccessException | IllegalArgumentException | SecurityException e) {
                e.printStackTrace();
            }
        }
    }
}

