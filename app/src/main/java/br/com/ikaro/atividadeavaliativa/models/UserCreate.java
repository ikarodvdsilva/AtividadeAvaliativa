package br.com.ikaro.atividadeavaliativa.models;

import com.google.gson.annotations.SerializedName;

public class UserCreate {
    @SerializedName("name")
    private String name;
    
    @SerializedName("cpf")
    private String cpf;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("phone")
    private String phone;
    
    @SerializedName("password")
    private String password;
    
    @SerializedName("is_admin")
    private boolean isAdmin;

    public UserCreate(String name, String cpf, String email, String phone, String password, boolean isAdmin) {
        this.name = name;
        this.cpf = cpf;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
} 