package com.mjc.adoptme.models;

public class Fundacion {
    private String nombre_fundacion;
    private String ruc;
    private String nombre_sucursal;
    private String direccion;
    private double latitud;
    private double longitud;
    private int cantidad_animales;
    private float distancia; // Para almacenar la distancia calculada

    public Fundacion() {}

    public Fundacion(String nombre_fundacion, String ruc, String nombre_sucursal, String direccion, 
                     double latitud, double longitud, int cantidad_animales) {
        this.nombre_fundacion = nombre_fundacion;
        this.ruc = ruc;
        this.nombre_sucursal = nombre_sucursal;
        this.direccion = direccion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.cantidad_animales = cantidad_animales;
    }

    public String getNombreFundacion() { return nombre_fundacion; }
    public void setNombreFundacion(String nombre_fundacion) { this.nombre_fundacion = nombre_fundacion; }

    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }

    public String getNombreSucursal() { return nombre_sucursal; }
    public void setNombreSucursal(String nombre_sucursal) { this.nombre_sucursal = nombre_sucursal; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }

    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }

    public int getCantidadAnimales() { return cantidad_animales; }
    public void setCantidadAnimales(int cantidad_animales) { this.cantidad_animales = cantidad_animales; }

    public float getDistancia() { return distancia; }
    public void setDistancia(float distancia) { this.distancia = distancia; }
}