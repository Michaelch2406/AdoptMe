package com.mjc.adoptme.models;

import com.google.gson.annotations.SerializedName;

public class UpdateAdoptionStatusRequest {
    @SerializedName("estado")
    private String estado;
    
    @SerializedName("motivo_cancelacion")
    private String motivoCancelacion;

    public UpdateAdoptionStatusRequest(String estado, String motivoCancelacion) {
        this.estado = estado;
        this.motivoCancelacion = motivoCancelacion;
    }

    // Getters y setters
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getMotivoCancelacion() { return motivoCancelacion; }
    public void setMotivoCancelacion(String motivoCancelacion) { this.motivoCancelacion = motivoCancelacion; }
}