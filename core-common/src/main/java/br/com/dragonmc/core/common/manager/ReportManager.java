/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.core.common.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.common.report.Report;

public class ReportManager {
    private Map<UUID, Report> reportMap = new HashMap<UUID, Report>();

    public Collection<Report> getReports() {
        return this.reportMap.values();
    }

    public void loadReport(Report report) {
        this.reportMap.put(report.getReportId(), report);
    }

    public void createReport(Report report) {
        this.loadReport(report);
        CommonPlugin.getInstance().getMemberData().createReport(report);
    }

    public void loadReports() {
        Collection<Report> loadReports = CommonPlugin.getInstance().getMemberData().loadReports();
        for (Report report : loadReports) {
            this.loadReport(report);
        }
    }

    public Report getReportById(UUID uniqueId) {
        return this.reportMap.get(uniqueId);
    }

    public Report getReportByName(String playerName) {
        return this.reportMap.values().stream().filter(report -> report.getPlayerName().equals(playerName)).findFirst().orElse(null);
    }

    public void deleteReport(UUID reportId) {
        this.reportMap.remove(reportId);
    }

    public void notify(UUID uniqueId) {
        Report report = this.getReportById(uniqueId);
        if (report != null) {
            report.notifyPunish();
        }
    }
}

