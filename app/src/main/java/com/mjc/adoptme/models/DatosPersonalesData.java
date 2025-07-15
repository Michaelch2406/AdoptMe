package com.mjc.adoptme.models;

import com.google.gson.annotations.SerializedName;

public class DatosPersonalesData {
    // Los campos que se envían y reciben para Datos Personales
    @SerializedName("nombres") private String nombres;
    @SerializedName("apellidos") private String apellidos;
    @SerializedName("fecha_nacimiento") private String fechaNacimiento;
    @SerializedName("lugar_nacimiento") private String lugarNacimiento;
    @SerializedName("nacionalidad") private String nacionalidad;
    @SerializedName("email") private String email;
    @SerializedName("telefono_convencional") private String telefonoConvencional;
    @SerializedName("telefono_movil") private String telefonoMovil;
    @SerializedName("ocupacion") private String ocupacion;
    @SerializedName("nivel_instruccion") private String nivelInstruccion;
    @SerializedName("lugar_trabajo") private String lugarTrabajo;
    @SerializedName("direccion_trabajo") private String direccionTrabajo;
    @SerializedName("telefono_trabajo") private String telefonoTrabajo;

    // Generar Getters y Setters para todos los campos...

    // Constructor
    public DatosPersonalesData() {
    }

    public DatosPersonalesData(String nombres, String apellidos, String fechaNacimiento, String lugarNacimiento, String nacionalidad, String email, String telefonoConvencional, String telefonoMovil, String ocupacion, String nivelInstruccion, String lugarTrabajo, String direccionTrabajo, String telefonoTrabajo) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
        this.lugarNacimiento = lugarNacimiento;
        this.nacionalidad = nacionalidad;
        this.email = email;
        this.telefonoConvencional = telefonoConvencional;
        this.telefonoMovil = telefonoMovil;
        this.ocupacion = ocupacion;
        this.nivelInstruccion = nivelInstruccion;
        this.lugarTrabajo = lugarTrabajo;
        this.direccionTrabajo = direccionTrabajo;
        this.telefonoTrabajo = telefonoTrabajo;
    }

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

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getLugarNacimiento() {
        return lugarNacimiento;
    }

    public void setLugarNacimiento(String lugarNacimiento) {
        this.lugarNacimiento = lugarNacimiento;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getOcupacion() {
        return ocupacion;
    }

    public void setOcupacion(String ocupacion) {
        this.ocupacion = ocupacion;
    }

    public String getNivelInstruccion() {
        return nivelInstruccion;
    }

    public void setNivelInstruccion(String nivelInstruccion) {
        this.nivelInstruccion = nivelInstruccion;
    }

    public String getLugarTrabajo() {
        return lugarTrabajo;
    }

    public void setLugarTrabajo(String lugarTrabajo) {
        this.lugarTrabajo = lugarTrabajo;
    }

    public String getDireccionTrabajo() {
        return direccionTrabajo;
    }

    public void setDireccionTrabajo(String direccionTrabajo) {
        this.direccionTrabajo = direccionTrabajo;
    }

    public String getTelefonoTrabajo() {
        return telefonoTrabajo;
    }

    public void setTelefonoTrabajo(String telefonoTrabajo) {
        this.telefonoTrabajo = telefonoTrabajo;
    }
    // (En Android Studio: Clic derecho -> Generate -> Getter and Setter -> Seleccionar todos)
}