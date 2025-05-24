package br.com.ikaro.atividadeavaliativa.models;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class Statistics {
    @SerializedName("total_reports")
    private int totalReports;

    @SerializedName("by_type")
    private Map<String, Integer> reportsByType;

    @SerializedName("by_status")
    private Map<String, Integer> reportsByStatus;

    @SerializedName("monthly_data")
    private Map<String, Integer> reportsByMonth;

    public int getTotalReports() {
        return totalReports;
    }

    public void setTotalReports(int totalReports) {
        this.totalReports = totalReports;
    }

    public Map<String, Integer> getReportsByType() {
        return reportsByType;
    }

    public void setReportsByType(Map<String, Integer> reportsByType) {
        this.reportsByType = reportsByType;
    }

    public Map<String, Integer> getReportsByStatus() {
        return reportsByStatus;
    }

    public void setReportsByStatus(Map<String, Integer> reportsByStatus) {
        this.reportsByStatus = reportsByStatus;
    }

    public Map<String, Integer> getReportsByMonth() {
        return reportsByMonth;
    }

    public void setReportsByMonth(Map<String, Integer> reportsByMonth) {
        this.reportsByMonth = reportsByMonth;
    }
} 