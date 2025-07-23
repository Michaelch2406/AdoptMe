package com.mjc.adoptme.models;

import com.google.gson.annotations.SerializedName;

public class SecurityQuestion {
    @SerializedName("id")
    private int id;
    
    @SerializedName("pregunta")
    private String pregunta;

    public SecurityQuestion() {}

    public SecurityQuestion(int id, String pregunta) {
        this.id = id;
        this.pregunta = pregunta;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPregunta() { return pregunta; }
    public void setPregunta(String pregunta) { this.pregunta = pregunta; }
}