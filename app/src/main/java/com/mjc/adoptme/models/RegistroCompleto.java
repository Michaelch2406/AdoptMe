package com.mjc.adoptme.models;

import com.google.gson.annotations.SerializedName;
import com.mjc.adoptme.models.Domicilio;
import com.mjc.adoptme.models.Entorno;

import java.util.List;

public class RegistroCompleto {

    @SerializedName("nombres")
    private String nombres;

    @SerializedName("apellidos")
    private String apellidos;

    @SerializedName("cedula")
    private String cedula;

    @SerializedName("fecha_nacimiento")
    private String fechaNacimiento;

    @SerializedName("lugar_nacimiento")
    private String lugarNacimiento;

    @SerializedName("nacionalidad")
    private String nacionalidad;

    @SerializedName("email")
    private String email;

    @SerializedName("password_hash")
    private String passwordHash;

    @SerializedName("telefono_convencional")
    private String telefonoConvencional;

    @SerializedName("telefono_movil")
    private String telefonoMovil;

    @SerializedName("ocupacion")
    private String ocupacion;

    @SerializedName("nivel_instruccion")
    private String nivelInstruccion;

    @SerializedName("lugar_trabajo")
    private String lugarTrabajo;

    @SerializedName("direccion_trabajo")
    private String direccionTrabajo;

    @SerializedName("telefono_trabajo")
    private String telefonoTrabajo;

    @SerializedName("referencias")
    private List<ReferenciaPersonal> referencias;

    @SerializedName("entorno")
    private Entorno entorno;

    @SerializedName("domicilio")
    private Domicilio domicilio;

    @SerializedName("animales")
    private InfoAnimales animales;

    // Genera Getters y Setters para todos los campos...
    // En Android Studio: Clic derecho -> Generate -> Getter and Setter -> Seleccionar todos

    public RegistroCompleto() {
    }

    public RegistroCompleto (String nombres, String apellidos, String cedula, String fechaNacimiento, String lugarNacimiento, String nacionalidad, String email, String passwordHash, String telefonoConvencional, String telefonoMovil, String ocupacion, String nivelInstruccion, String lugarTrabajo, String direccionTrabajo, String telefonoTrabajo) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.cedula = cedula;
        this.fechaNacimiento = fechaNacimiento;
        this.lugarNacimiento = lugarNacimiento;
        this.nacionalidad = nacionalidad;
        this.email = email;
        this.passwordHash = passwordHash;
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

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
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

    public List<ReferenciaPersonal> getReferencias() {
        return referencias;
    }

    public void setReferencias(List<ReferenciaPersonal> referencias) {
        this.referencias = referencias;
    }

    public Entorno getEntorno() {
        return entorno;
    }

    public void setEntorno(Entorno entorno) {
        this.entorno = entorno;
    }

    public Domicilio getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(Domicilio domicilio) {
        this.domicilio = domicilio;
    }

    public InfoAnimales getAnimales() {
        return animales;
    }

    public void setAnimales(InfoAnimales animales) {
        this.animales = animales;
    }
}