package br.com.ikaro.atividadeavaliativa.models;

public class User {
    private int id;
    private String name;
    private String cpf;
    private String email;
    private String phone;
    private boolean isAdmin;

    public User(int id, String name, String cpf, String email, String phone, boolean isAdmin) {
        this.id = id;
        this.name = name;
        this.cpf = cpf;
        this.email = email;
        this.phone = phone;
        this.isAdmin = isAdmin;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCpf() {
        return cpf;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}
