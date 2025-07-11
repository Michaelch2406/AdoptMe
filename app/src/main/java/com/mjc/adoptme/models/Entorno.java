package com.mjc.adoptme.models;
import com.google.gson.annotations.SerializedName;
import com.mjc.adoptme.models.MiembroFamiliar;

import java.util.List;

public class Entorno {
    @SerializedName("espera_bebe") private boolean esperaBebe;
    @SerializedName("tiene_discapacidad_fobia") private boolean tieneDiscapacidadFobia;
    @SerializedName("especificacion_discapacidad") private String especificacionDiscapacidad;
    @SerializedName("decision_familiar") private String decisionFamiliar;
    @SerializedName("miembros") private List<MiembroFamiliar> miembros;
    // Getters y Setters...


    public Entorno() {
    }

    public  Entorno(boolean esperaBebe, boolean tieneDiscapacidadFobia, String especificacionDiscapacidad, String decisionFamiliar, List<MiembroFamiliar> miembros) {
        this.esperaBebe = esperaBebe;
        this.tieneDiscapacidadFobia = tieneDiscapacidadFobia;
        this.especificacionDiscapacidad = especificacionDiscapacidad;
        this.decisionFamiliar = decisionFamiliar;
        this.miembros = miembros;
    }
    public boolean isEsperaBebe() {
        return esperaBebe;
    }

    public void setEsperaBebe(boolean esperaBebe) {
        this.esperaBebe = esperaBebe;
    }

    public boolean isTieneDiscapacidadFobia() {
        return tieneDiscapacidadFobia;
    }

    public void setTieneDiscapacidadFobia(boolean tieneDiscapacidadFobia) {
        this.tieneDiscapacidadFobia = tieneDiscapacidadFobia;
    }

    public String getEspecificacionDiscapacidad() {
        return especificacionDiscapacidad;
    }

    public void setEspecificacionDiscapacidad(String especificacionDiscapacidad) {
        this.especificacionDiscapacidad = especificacionDiscapacidad;
    }

    public String getDecisionFamiliar() {
        return decisionFamiliar;
    }

    public void setDecisionFamiliar(String decisionFamiliar) {
        this.decisionFamiliar = decisionFamiliar;
    }

    public List<MiembroFamiliar> getMiembros() {
        return miembros;
    }

    public void setMiembros(List<MiembroFamiliar> miembros) {
        this.miembros = miembros;
    }
}