package com.mjc.adoptme.models;

import com.google.gson.annotations.SerializedName;

public class AdoptionStats {
    @SerializedName("total_adopciones")
    private int totalAdopciones;
    
    @SerializedName("adopciones_en_proceso")
    private int adopcionesEnProceso;
    
    @SerializedName("adopciones_completadas")
    private int adopcionesCompletadas;

    // Constructor vacío
    public AdoptionStats() {}

    // Getters y setters
    public int getTotalAdopciones() { return totalAdopciones; }
    public void setTotalAdopciones(int totalAdopciones) { this.totalAdopciones = totalAdopciones; }

    public int getAdopcionesEnProceso() { return adopcionesEnProceso; }
    public void setAdopcionesEnProceso(int adopcionesEnProceso) { this.adopcionesEnProceso = adopcionesEnProceso; }

    public int getAdopcionesCompletadas() { return adopcionesCompletadas; }
    public void setAdopcionesCompletadas(int adopcionesCompletadas) { this.adopcionesCompletadas = adopcionesCompletadas; }
}