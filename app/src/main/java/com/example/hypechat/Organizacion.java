package com.example.hypechat;

public class Organizacion {
    private String nombre;
    private String id;


    public Organizacion() {
    }

    public Organizacion(String nombre, String id) {
        this.nombre = nombre;
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
