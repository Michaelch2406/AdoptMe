package com.mjc.adoptme.models;

import com.google.gson.annotations.SerializedName;

public class Referencia {

    @SerializedName("nombres")
    private String nombres;
    @SerializedName("apellidos")
    private String apellidos;
    @SerializedName("parentesco")
    private String parentesco;
    @SerializedName("telefono_convencional")
    private String telefono_convencional;
    @SerializedName("telefono_movil")
    private String telefono_movil;
    @SerializedName("email")
    private String email;

    // Constructor por defecto
    public Referencia() {
    }

    // Constructor completo
    public Referencia(String nombres,
                      String apellidos,
                      String parentesco,
                      String telefono_convencional,
                      String telefono_movil,
                      String email) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.parentesco = parentesco;
        this.telefono_convencional = telefono_convencional;
        this.telefono_movil = telefono_movil;
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

    public String getTelefono_convencional() {
        return telefono_convencional;
    }

    public void setTelefono_convencional(String telefono_convencional) {
        this.telefono_convencional = telefono_convencional;
    }

    public String getTelefono_movil() {
        return telefono_movil;
    }

    public void setTelefono_movil(String telefono_movil) {
        this.telefono_movil = telefono_movil;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
