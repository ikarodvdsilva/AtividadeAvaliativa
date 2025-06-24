package br.com.ikaro.atividadeavaliativa.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Report {
    @SerializedName("id")
    private int id;
    
    @SerializedName("title")
    private String title;
    
    @SerializedName("type")
    private String type;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("location")
    private String location;
    
    @SerializedName("user_id")
    private int userId;
    
    @SerializedName("anonymous")
    private boolean anonymous;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("created_at")
    private String createdAt;
    
    @SerializedName("admin_notes")
    private String adminNotes;
    
    @SerializedName("images")
    private List<ReportImage> images;

    // Construtor padrão
    public Report() {}

    // Construtor com parâmetros
    public Report(int id, String title, String type, String description, String location,
                 int userId, boolean anonymous, String status, String createdAt, String adminNotes) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.description = description;
        this.location = location;
        this.userId = userId;
        this.anonymous = anonymous;
        this.status = status;
        this.createdAt = createdAt;
        this.adminNotes = adminNotes;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public boolean isAnonymous() { return anonymous; }
    public void setAnonymous(boolean anonymous) { this.anonymous = anonymous; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }
    
    public List<ReportImage> getImages() { return images; }
    public void setImages(List<ReportImage> images) { this.images = images; }
}
