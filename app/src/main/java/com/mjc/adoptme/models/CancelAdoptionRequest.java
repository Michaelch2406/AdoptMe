package com.mjc.adoptme.models;

import com.google.gson.annotations.SerializedName;

public class CancelAdoptionRequest {
    @SerializedName("adopcion_id")
    private int adopcionId;
    
    @SerializedName("estado")
    private String estado;
    
    @SerializedName("motivo_cancelacion")
    private String motivoCancelacion;

    public CancelAdoptionRequest(int adopcionId, String estado, String motivoCancelacion) {
        this.adopcionId = adopcionId;
        this.estado = estado;
        this.motivoCancelacion = motivoCancelacion;
    }

    // Getters y setters
    public int getAdopcionId() { return adopcionId; }
    public void setAdopcionId(int adopcionId) { this.adopcionId = adopcionId; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getMotivoCancelacion() { return motivoCancelacion; }
    public void setMotivoCancelacion(String motivoCancelacion) { this.motivoCancelacion = motivoCancelacion; }
}