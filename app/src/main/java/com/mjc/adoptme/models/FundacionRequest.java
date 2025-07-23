package com.mjc.adoptme.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class FundacionRequest {
    @SerializedName("lat")
    private double lat;
    
    @SerializedName("lng")
    private double lng;
    
    @SerializedName("cedula")
    private String cedula;
    
    @SerializedName("distancia")
    private float distancia;
    
    @SerializedName("excluded_animals")
    private List<Integer> excludedAnimals;

    public FundacionRequest(double lat, double lng, String cedula, float distancia) {
        this.lat = lat;
        this.lng = lng;
        this.cedula = cedula;
        this.distancia = distancia;
        this.excludedAnimals = null; // Send null instead of empty string
    }

    // Getters y setters
    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }

    public double getLng() { return lng; }
    public void setLng(double lng) { this.lng = lng; }

    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }

    public float getDistancia() { return distancia; }
    public void setDistancia(float distancia) { this.distancia = distancia; }
    
    public List<Integer> getExcludedAnimals() { return excludedAnimals; }
    public void setExcludedAnimals(List<Integer> excludedAnimals) { this.excludedAnimals = excludedAnimals; }
}