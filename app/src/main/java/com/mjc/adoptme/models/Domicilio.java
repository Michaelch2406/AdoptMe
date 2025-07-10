package com.mjc.adoptme.models;

import com.google.gson.annotations.SerializedName;

public class Domicilio {

    @SerializedName("direccion")
    private String direccion;
    @SerializedName("parroquia_id")
    private int parroquia_id;
    @SerializedName("es_urbanizacion")
    private boolean es_urbanizacion;
    @SerializedName("nombre_urbanizacion")
    private String nombre_urbanizacion;
    @SerializedName("numero_bloque")
    private String numero_bloque;
    @SerializedName("numero_casa")
    private String numero_casa;
    @SerializedName("tipo_vivienda")
    private String tipo_vivienda;
    @SerializedName("metros_cuadrados_vivienda")
    private int metros_cuadrados_vivienda;
    @SerializedName("metros_cuadrados_area_verde")
    private int metros_cuadrados_area_verde;
    @SerializedName("dimension_area_comunal")
    private String dimension_area_comunal;
    @SerializedName("tipo_tenencia")
    private String tipo_tenencia;
    @SerializedName("propietario_permite_animales")
    private boolean propietario_permite_animales;
    @SerializedName("nombre_propietario")
    private String nombre_propietario;
    @SerializedName("telefono_propietario")
    private String telefono_propietario;
    @SerializedName("tiene_cerramiento")
    private boolean tiene_cerramiento;
    @SerializedName("altura_cerramiento")
    private double altura_cerramiento;
    @SerializedName("tipo_cerramiento")
    private String tipo_cerramiento;
    @SerializedName("puede_escapar_animal")
    private boolean puede_escapar_animal;
    @SerializedName("tipo_residencia")
    private String tipo_residencia;
    @SerializedName("especificacion_residencia")
    private String especificacion_residencia;

    // Constructor por defecto
    public Domicilio() {
    }

    // Constructor completo
    public Domicilio(String direccion,
                     int parroquia_id,
                     boolean es_urbanizacion,
                     String nombre_urbanizacion,
                     String numero_bloque,
                     String numero_casa,
                     String tipo_vivienda,
                     int metros_cuadrados_vivienda,
                     int metros_cuadrados_area_verde,
                     String dimension_area_comunal,
                     String tipo_tenencia,
                     boolean propietario_permite_animales,
                     String nombre_propietario,
                     String telefono_propietario,
                     boolean tiene_cerramiento,
                     double altura_cerramiento,
                     String tipo_cerramiento,
                     boolean puede_escapar_animal,
                     String tipo_residencia,
                     String especificacion_residencia) {
        this.direccion = direccion;
        this.parroquia_id = parroquia_id;
        this.es_urbanizacion = es_urbanizacion;
        this.nombre_urbanizacion = nombre_urbanizacion;
        this.numero_bloque = numero_bloque;
        this.numero_casa = numero_casa;
        this.tipo_vivienda = tipo_vivienda;
        this.metros_cuadrados_vivienda = metros_cuadrados_vivienda;
        this.metros_cuadrados_area_verde = metros_cuadrados_area_verde;
        this.dimension_area_comunal = dimension_area_comunal;
        this.tipo_tenencia = tipo_tenencia;
        this.propietario_permite_animales = propietario_permite_animales;
        this.nombre_propietario = nombre_propietario;
        this.telefono_propietario = telefono_propietario;
        this.tiene_cerramiento = tiene_cerramiento;
        this.altura_cerramiento = altura_cerramiento;
        this.tipo_cerramiento = tipo_cerramiento;
        this.puede_escapar_animal = puede_escapar_animal;
        this.tipo_residencia = tipo_residencia;
        this.especificacion_residencia = especificacion_residencia;
    }

    // Getters y Setters

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public int getParroquia_id() {
        return parroquia_id;
    }

    public void setParroquia_id(int parroquia_id) {
        this.parroquia_id = parroquia_id;
    }

    public boolean isEs_urbanizacion() {
        return es_urbanizacion;
    }

    public void setEs_urbanizacion(boolean es_urbanizacion) {
        this.es_urbanizacion = es_urbanizacion;
    }

    public String getNombre_urbanizacion() {
        return nombre_urbanizacion;
    }

    public void setNombre_urbanizacion(String nombre_urbanizacion) {
        this.nombre_urbanizacion = nombre_urbanizacion;
    }

    public String getNumero_bloque() {
        return numero_bloque;
    }

    public void setNumero_bloque(String numero_bloque) {
        this.numero_bloque = numero_bloque;
    }

    public String getNumero_casa() {
        return numero_casa;
    }

    public void setNumero_casa(String numero_casa) {
        this.numero_casa = numero_casa;
    }

    public String getTipo_vivienda() {
        return tipo_vivienda;
    }

    public void setTipo_vivienda(String tipo_vivienda) {
        this.tipo_vivienda = tipo_vivienda;
    }

    public int getMetros_cuadrados_vivienda() {
        return metros_cuadrados_vivienda;
    }

    public void setMetros_cuadrados_vivienda(int metros_cuadrados_vivienda) {
        this.metros_cuadrados_vivienda = metros_cuadrados_vivienda;
    }

    public int getMetros_cuadrados_area_verde() {
        return metros_cuadrados_area_verde;
    }

    public void setMetros_cuadrados_area_verde(int metros_cuadrados_area_verde) {
        this.metros_cuadrados_area_verde = metros_cuadrados_area_verde;
    }

    public String getDimension_area_comunal() {
        return dimension_area_comunal;
    }

    public void setDimension_area_comunal(String dimension_area_comunal) {
        this.dimension_area_comunal = dimension_area_comunal;
    }

    public String getTipo_tenencia() {
        return tipo_tenencia;
    }

    public void setTipo_tenencia(String tipo_tenencia) {
        this.tipo_tenencia = tipo_tenencia;
    }

    public boolean isPropietario_permite_animales() {
        return propietario_permite_animales;
    }

    public void setPropietario_permite_animales(boolean propietario_permite_animales) {
        this.propietario_permite_animales = propietario_permite_animales;
    }

    public String getNombre_propietario() {
        return nombre_propietario;
    }

    public void setNombre_propietario(String nombre_propietario) {
        this.nombre_propietario = nombre_propietario;
    }

    public String getTelefono_propietario() {
        return telefono_propietario;
    }

    public void setTelefono_propietario(String telefono_propietario) {
        this.telefono_propietario = telefono_propietario;
    }

    public boolean isTiene_cerramiento() {
        return tiene_cerramiento;
    }

    public void setTiene_cerramiento(boolean tiene_cerramiento) {
        this.tiene_cerramiento = tiene_cerramiento;
    }

    public double getAltura_cerramiento() {
        return altura_cerramiento;
    }

    public void setAltura_cerramiento(double altura_cerramiento) {
        this.altura_cerramiento = altura_cerramiento;
    }

    public String getTipo_cerramiento() {
        return tipo_cerramiento;
    }

    public void setTipo_cerramiento(String tipo_cerramiento) {
        this.tipo_cerramiento = tipo_cerramiento;
    }

    public boolean isPuede_escapar_animal() {
        return puede_escapar_animal;
    }

    public void setPuede_escapar_animal(boolean puede_escapar_animal) {
        this.puede_escapar_animal = puede_escapar_animal;
    }

    public String getTipo_residencia() {
        return tipo_residencia;
    }

    public void setTipo_residencia(String tipo_residencia) {
        this.tipo_residencia = tipo_residencia;
    }

    public String getEspecificacion_residencia() {
        return especificacion_residencia;
    }

    public void setEspecificacion_residencia(String especificacion_residencia) {
        this.especificacion_residencia = especificacion_residencia;
    }
}
