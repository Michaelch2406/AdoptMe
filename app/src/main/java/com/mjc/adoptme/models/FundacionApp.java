package com.mjc.adoptme.models;

import com.google.gson.annotations.SerializedName;

public class FundacionApp {
    @SerializedName("nombre_fundacion")
    private String nombreFundacion;
    
    @SerializedName("ruc")
    private String ruc;
    
    @SerializedName("nombre_sucursal")
    private String nombreSucursal;
    
    @SerializedName("direccion")
    private String direccion;
    
    @SerializedName("latitud")
    private double latitud;
    
    @SerializedName("longitud")
    private double longitud;
    
    @SerializedName("cantidad_animales")
    private int cantidadAnimales;

    // Constructor vacío
    public FundacionApp() {}

    // Getters y setters
    public String getNombreFundacion() { return nombreFundacion; }
    public void setNombreFundacion(String nombreFundacion) { this.nombreFundacion = nombreFundacion; }

    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }

    public String getNombreSucursal() { return nombreSucursal; }
    public void setNombreSucursal(String nombreSucursal) { this.nombreSucursal = nombreSucursal; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }

    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }

    public int getCantidadAnimales() { return cantidadAnimales; }
    public void setCantidadAnimales(int cantidadAnimales) { this.cantidadAnimales = cantidadAnimales; }
}