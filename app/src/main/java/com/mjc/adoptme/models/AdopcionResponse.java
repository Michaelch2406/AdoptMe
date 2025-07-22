package com.mjc.adoptme.models;

import com.google.gson.annotations.SerializedName;

public class AdopcionResponse {
    @SerializedName("adopcion_id")
    private int adopcion_id;

    public AdopcionResponse() {}

    public AdopcionResponse(int adopcion_id) {
        this.adopcion_id = adopcion_id;
    }

    public int getAdopcionId() { return adopcion_id; }
    public void setAdopcionId(int adopcion_id) { this.adopcion_id = adopcion_id; }
}