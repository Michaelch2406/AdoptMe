package com.mjc.adoptme.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SaveSecurityAnswersRequest {
    @SerializedName("cedula")
    private String cedula;
    
    @SerializedName("preguntas")
    private List<SecurityAnswer> preguntas;

    public SaveSecurityAnswersRequest() {}

    public SaveSecurityAnswersRequest(String cedula, List<SecurityAnswer> preguntas) {
        this.cedula = cedula;
        this.preguntas = preguntas;
    }

    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }

    public List<SecurityAnswer> getPreguntas() { return preguntas; }
    public void setPreguntas(List<SecurityAnswer> preguntas) { this.preguntas = preguntas; }
}