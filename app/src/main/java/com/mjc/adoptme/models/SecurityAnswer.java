package com.mjc.adoptme.models;

import com.google.gson.annotations.SerializedName;

public class SecurityAnswer {
    @SerializedName("id")
    private int id;
    
    @SerializedName("respuesta")
    private String respuesta;

    public SecurityAnswer() {}

    public SecurityAnswer(int id, String respuesta) {
        this.id = id;
        this.respuesta = respuesta;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRespuesta() { return respuesta; }
    public void setRespuesta(String respuesta) { this.respuesta = respuesta; }
}