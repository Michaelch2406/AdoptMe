package com.mjc.adoptme.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Animales {

    @SerializedName("mala_experiencia")
    private String malaExperiencia;

    @SerializedName("tiene_animales_actuales")
    private boolean tieneAnimalesActuales;

    private String especificacion;
    private List<AnimalActual> actuales;
    private List<AnimalHistorial> historial;

    public Animales() {
    }

    public Animales(String malaExperiencia, boolean tieneAnimalesActuales, String especificacion,
                    List<AnimalActual> actuales, List<AnimalHistorial> historial) {
        this.malaExperiencia = malaExperiencia;
        this.tieneAnimalesActuales = tieneAnimalesActuales;
        this.especificacion = especificacion;
        this.actuales = actuales;
        this.historial = historial;
    }

    public String getMalaExperiencia() {
        return malaExperiencia;
    }

    public void setMalaExperiencia(String malaExperiencia) {
        this.malaExperiencia = malaExperiencia;
    }

    public boolean isTieneAnimalesActuales() {
        return tieneAnimalesActuales;
    }

    public void setTieneAnimalesActuales(boolean tieneAnimalesActuales) {
        this.tieneAnimalesActuales = tieneAnimalesActuales;
    }

    public String getEspecificacion() {
        return especificacion;
    }

    public void setEspecificacion(String especificacion) {
        this.especificacion = especificacion;
    }

    public List<AnimalActual> getActuales() {
        return actuales;
    }

    public void setActuales(List<AnimalActual> actuales) {
        this.actuales = actuales;
    }

    public List<AnimalHistorial> getHistorial() {
        return historial;
    }

    public void setHistorial(List<AnimalHistorial> historial) {
        this.historial = historial;
    }
}
