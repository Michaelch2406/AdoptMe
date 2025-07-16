package com.mjc.adoptme.models;

import com.google.gson.annotations.SerializedName;

public class Pais {
    @SerializedName("id")
    private int id;
    
    @SerializedName("nombre")
    private String nombre;
    
    @SerializedName("codigo_iso")
    private String codigoIso;

    public Pais() {
    }

    public Pais(int id, String nombre, String codigoIso) {
        this.id = id;
        this.nombre = nombre;
        this.codigoIso = codigoIso;
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

    public String getCodigoIso() {
        return codigoIso;
    }

    public void setCodigoIso(String codigoIso) {
        this.codigoIso = codigoIso;
    }

    @Override
    public String toString() {
        return nombre;
    }
}