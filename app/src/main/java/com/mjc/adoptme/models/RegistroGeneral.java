package com.mjc.adoptme.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class RegistroGeneral {

    @SerializedName("nombres")
    private String nombres;
    @SerializedName("apellidos")
    private String apellidos;
    @SerializedName("cedula")
    private String cedula;
    @SerializedName("fecha_nacimiento")
    private String fecha_nacimiento;
    @SerializedName("lugar_nacimiento")
    private String lugar_nacimiento;
    @SerializedName("nacionalidad")
    private String nacionalidad;
    @SerializedName("email")
    private String email;
    @SerializedName("password_hash")
    private String password_hash;
    @SerializedName("telefono_convencional")
    private String telefono_convencional;
    @SerializedName("telefono_movil")
    private String telefono_movil;
    @SerializedName("ocupacion")
    private String ocupacion;
    @SerializedName("nivel_instruccion")
    private String nivel_instruccion;
    @SerializedName("lugar_trabajo")
    private String lugar_trabajo;
    @SerializedName("direccion_trabajo")
    private String direccion_trabajo;
    @SerializedName("telefono_trabajo")
    private String telefono_trabajo;
    @SerializedName("referencias")
    private List<Referencia> referencias;
    @SerializedName("entorno")
    private Entorno entorno;
    @SerializedName("domicilio")
    private Domicilio domicilio;
    @SerializedName("animales")
    private Animales animales;

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

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getFecha_nacimiento() {
        return fecha_nacimiento;
    }

    public void setFecha_nacimiento(String fecha_nacimiento) {
        this.fecha_nacimiento = fecha_nacimiento;
    }

    public String getLugar_nacimiento() {
        return lugar_nacimiento;
    }

    public void setLugar_nacimiento(String lugar_nacimiento) {
        this.lugar_nacimiento = lugar_nacimiento;
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

    public String getPassword_hash() {
        return password_hash;
    }

    public void setPassword_hash(String password_hash) {
        this.password_hash = password_hash;
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

    public String getOcupacion() {
        return ocupacion;
    }

    public void setOcupacion(String ocupacion) {
        this.ocupacion = ocupacion;
    }

    public String getNivel_instruccion() {
        return nivel_instruccion;
    }

    public void setNivel_instruccion(String nivel_instruccion) {
        this.nivel_instruccion = nivel_instruccion;
    }

    public String getLugar_trabajo() {
        return lugar_trabajo;
    }

    public void setLugar_trabajo(String lugar_trabajo) {
        this.lugar_trabajo = lugar_trabajo;
    }

    public String getDireccion_trabajo() {
        return direccion_trabajo;
    }

    public void setDireccion_trabajo(String direccion_trabajo) {
        this.direccion_trabajo = direccion_trabajo;
    }

    public String getTelefono_trabajo() {
        return telefono_trabajo;
    }

    public void setTelefono_trabajo(String telefono_trabajo) {
        this.telefono_trabajo = telefono_trabajo;
    }

    public List<Referencia> getReferencias() {
        return referencias;
    }

    public void setReferencias(List<Referencia> referencias) {
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

    public Animales getAnimales() {
        return animales;
    }

    public void setAnimales(Animales animales) {
        this.animales = animales;
    }
}
