package com.mjc.adoptme.models;

import com.google.gson.annotations.SerializedName;

public class Provincia {
    @SerializedName("id")
    private int id;
    
    @SerializedName("nombre")
    private String nombre;

    public Provincia() {
    }

    public Provincia(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
}