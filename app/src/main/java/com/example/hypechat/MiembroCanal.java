package com.example.hypechat;

public class MiembroCanal {
    private String email;
    private Boolean pertenece;


    public MiembroCanal() {
    }

    public MiembroCanal(String email, Boolean esta) {
        this.email = email;
        this.pertenece = esta;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean perteneceAlCanal() {
        return pertenece;
    }

    public void setPertenece(Boolean esta) {
        this.pertenece = esta;
    }
}
