package br.com.ikaro.atividadeavaliativa.models;

public class Report {
    private int id;
    private String title;
    private String type;
    private String description;
    private String location;
    private int userId;
    private boolean anonymous;
    private String status;
    private String createdAt;
    private String adminNotes;

    public Report(int id, String title, String type, String description, String location, int userId, boolean anonymous, String status, String createdAt, String adminNotes) {
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

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public int getUserId() {
        return userId;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getAdminNotes() {
        return adminNotes;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes;
    }
}
