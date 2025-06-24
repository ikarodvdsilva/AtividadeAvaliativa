package br.com.ikaro.atividadeavaliativa.models;

import com.google.gson.annotations.SerializedName;

public class ReportImage {
    @SerializedName("id")
    private int id;
    
    @SerializedName("report_id")
    private int reportId;
    
    @SerializedName("image_path")
    private String imagePath;

    // Construtor padrão
    public ReportImage() {}

    // Construtor com parâmetros
    public ReportImage(int id, int reportId, String imagePath) {
        this.id = id;
        this.reportId = reportId;
        this.imagePath = imagePath;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getReportId() { return reportId; }
    public void setReportId(int reportId) { this.reportId = reportId; }
    
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
}
