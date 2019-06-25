package com.example.hypechat;

public class MiembroCanal {
    private String email;
    private Boolean pertenece;
    private Boolean permition_edit;



    public MiembroCanal() {
    }

    public MiembroCanal(String email, Boolean esta,Boolean permiso_edit) {
        this.email = email;
        this.pertenece = esta;
        this.permition_edit = permiso_edit;
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

    public Boolean getPermitions() { return this.permition_edit;
    }
}
