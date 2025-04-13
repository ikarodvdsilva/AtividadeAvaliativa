package br.com.ikaro.atividadeavaliativa.models;

public class ReportImage {
    private int id;
    private int reportId;
    private String imagePath;

    public ReportImage(int id, int reportId, String imagePath) {
        this.id = id;
        this.reportId = reportId;
        this.imagePath = imagePath;
    }

    public int getId() {
        return id;
    }

    public int getReportId() {
        return reportId;
    }

    public String getImagePath() {
        return imagePath;
    }
}
