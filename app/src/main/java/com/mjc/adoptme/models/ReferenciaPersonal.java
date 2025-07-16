package com.mjc.adoptme.models;

import com.google.gson.annotations.SerializedName;

public class ReferenciaPersonal {
    @SerializedName("nombres")
    private String nombres;

    @SerializedName("apellidos")
    private String apellidos;

    @SerializedName("parentesco")
    private String parentesco;

    @SerializedName("telefono_convencional")
    private String telefonoConvencional;

    @SerializedName("telefono_movil")
    private String telefonoMovil;

    @SerializedName("email")
    private String email;

    // Constructor vacío
    public ReferenciaPersonal() {
    }

    // Constructor completo
    public ReferenciaPersonal(String nombres, String apellidos, String parentesco,
                              String telefonoConvencional, String telefonoMovil, String email) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.parentesco = parentesco;
        this.telefonoConvencional = telefonoConvencional;
        this.telefonoMovil = telefonoMovil;
        this.email = email;
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

    public String getParentesco() {
        return parentesco;
    }

    public void setParentesco(String parentesco) {
        this.parentesco = parentesco;
    }

    public String getTelefonoConvencional() {
        return telefonoConvencional;
    }

    public void setTelefonoConvencional(String telefonoConvencional) {
        this.telefonoConvencional = telefonoConvencional;
    }

    public String getTelefonoMovil() {
        return telefonoMovil;
    }

    public void setTelefonoMovil(String telefonoMovil) {
        this.telefonoMovil = telefonoMovil;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}