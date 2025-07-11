package com.mjc.adoptme.models;

import com.google.gson.annotations.SerializedName;

public class MiembroFamiliar {
    @SerializedName("nombres") private String nombres;
    @SerializedName("apellidos") private String apellidos;
    @SerializedName("edad") private int edad;
    @SerializedName("parentesco") private String parentesco;
    // Getters y Setters...

    // Constructor por defecto
    public MiembroFamiliar() {
    }

    // Constructor completo
    public MiembroFamiliar(String nombres,
                           String apellidos,
                           int edad,
                           String parentesco) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.edad = edad;
        this.parentesco = parentesco;
    }

    // Getters y Setters

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getParentesco() {
        return parentesco;
    }

    public void setParentesco(String parentesco) {
        this.parentesco = parentesco;
    }
}
