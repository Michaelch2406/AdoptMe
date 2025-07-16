package com.mjc.adoptme.models;

import com.google.gson.annotations.SerializedName;

public class Domicilio {
    @SerializedName("direccion")
    private String direccion;

    @SerializedName("parroquia_id")
    private int parroquiaId;

    @SerializedName("es_urbanizacion")
    private boolean esUrbanizacion;

    @SerializedName("nombre_urbanizacion")
    private String nombreUrbanizacion;

    @SerializedName("numero_bloque")
    private String numeroBloque;

    @SerializedName("numero_casa")
    private String numeroCasa;

    @SerializedName("tipo_vivienda")
    private String tipoVivienda;

    @SerializedName("metros_cuadrados_vivienda")
    private int metrosCuadradosVivienda;

    @SerializedName("metros_cuadrados_area_verde")
    private int metrosCuadradosAreaVerde;

    @SerializedName("dimension_area_comunal")
    private Integer dimensionAreaComunal;

    @SerializedName("tipo_tenencia")
    private String tipoTenencia;

    @SerializedName("propietario_permite_animales")
    private boolean propietarioPermiteAnimales;

    @SerializedName("nombre_propietario")
    private String nombrePropietario;

    @SerializedName("telefono_propietario")
    private String telefonoPropietario;

    @SerializedName("tiene_cerramiento")
    private boolean tieneCerramiento;

    @SerializedName("altura_cerramiento")
    private double alturaCerramiento;

    @SerializedName("tipo_cerramiento")
    private String tipoCerramiento;

    @SerializedName("puede_escapar_animal")
    private boolean puedeEscaparAnimal;

    @SerializedName("tipo_residencia")
    private String tipoResidencia;

    @SerializedName("especificacion_residencia")
    private String especificacionResidencia;

    // Constructor vacío
    public Domicilio() {
    }

    // Getters y Setters
    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public int getParroquiaId() {
        return parroquiaId;
    }

    public void setParroquiaId(int parroquiaId) {
        this.parroquiaId = parroquiaId;
    }

    public boolean isEsUrbanizacion() {
        return esUrbanizacion;
    }

    public void setEsUrbanizacion(boolean esUrbanizacion) {
        this.esUrbanizacion = esUrbanizacion;
    }

    public String getNombreUrbanizacion() {
        return nombreUrbanizacion;
    }

    public void setNombreUrbanizacion(String nombreUrbanizacion) {
        this.nombreUrbanizacion = nombreUrbanizacion;
    }

    public String getNumeroBloque() {
        return numeroBloque;
    }

    public void setNumeroBloque(String numeroBloque) {
        this.numeroBloque = numeroBloque;
    }

    public String getNumeroCasa() {
        return numeroCasa;
    }

    public void setNumeroCasa(String numeroCasa) {
        this.numeroCasa = numeroCasa;
    }

    public String getTipoVivienda() {
        return tipoVivienda;
    }

    public void setTipoVivienda(String tipoVivienda) {
        this.tipoVivienda = tipoVivienda;
    }

    public int getMetrosCuadradosVivienda() {
        return metrosCuadradosVivienda;
    }

    public void setMetrosCuadradosVivienda(int metrosCuadradosVivienda) {
        this.metrosCuadradosVivienda = metrosCuadradosVivienda;
    }

    public int getMetrosCuadradosAreaVerde() {
        return metrosCuadradosAreaVerde;
    }

    public void setMetrosCuadradosAreaVerde(int metrosCuadradosAreaVerde) {
        this.metrosCuadradosAreaVerde = metrosCuadradosAreaVerde;
    }

    public Integer getDimensionAreaComunal() {
        return dimensionAreaComunal;
    }

    public void setDimensionAreaComunal(Integer dimensionAreaComunal) {
        this.dimensionAreaComunal = dimensionAreaComunal;
    }

    public String getTipoTenencia() {
        return tipoTenencia;
    }

    public void setTipoTenencia(String tipoTenencia) {
        this.tipoTenencia = tipoTenencia;
    }

    public boolean isPropietarioPermiteAnimales() {
        return propietarioPermiteAnimales;
    }

    public void setPropietarioPermiteAnimales(boolean propietarioPermiteAnimales) {
        this.propietarioPermiteAnimales = propietarioPermiteAnimales;
    }

    public String getNombrePropietario() {
        return nombrePropietario;
    }

    public void setNombrePropietario(String nombrePropietario) {
        this.nombrePropietario = nombrePropietario;
    }

    public String getTelefonoPropietario() {
        return telefonoPropietario;
    }

    public void setTelefonoPropietario(String telefonoPropietario) {
        this.telefonoPropietario = telefonoPropietario;
    }

    public boolean isTieneCerramiento() {
        return tieneCerramiento;
    }

    public void setTieneCerramiento(boolean tieneCerramiento) {
        this.tieneCerramiento = tieneCerramiento;
    }

    public double getAlturaCerramiento() {
        return alturaCerramiento;
    }

    public void setAlturaCerramiento(double alturaCerramiento) {
        this.alturaCerramiento = alturaCerramiento;
    }

    public String getTipoCerramiento() {
        return tipoCerramiento;
    }

    public void setTipoCerramiento(String tipoCerramiento) {
        this.tipoCerramiento = tipoCerramiento;
    }

    public boolean isPuedeEscaparAnimal() {
        return puedeEscaparAnimal;
    }

    public void setPuedeEscaparAnimal(boolean puedeEscaparAnimal) {
        this.puedeEscaparAnimal = puedeEscaparAnimal;
    }

    public String getTipoResidencia() {
        return tipoResidencia;
    }

    public void setTipoResidencia(String tipoResidencia) {
        this.tipoResidencia = tipoResidencia;
    }

    public String getEspecificacionResidencia() {
        return especificacionResidencia;
    }

    public void setEspecificacionResidencia(String especificacionResidencia) {
        this.especificacionResidencia = especificacionResidencia;
    }
}