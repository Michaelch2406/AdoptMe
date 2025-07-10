package com.mjc.adoptme.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Entorno {

    @SerializedName("espera_bebe")
    private boolean espera_bebe;
    @SerializedName("tiene_discapacidad_fobia")
    private boolean tiene_discapacidad_fobia;
    @SerializedName("especificacion_discapacidad")
    private String especificacion_discapacidad;
    @SerializedName("decision_familiar")
    private String decision_familiar;
    @SerializedName("miembros")
    private List<MiembroFamiliar> miembros;

    // Constructor por defecto
    public Entorno() {
    }

    // Constructor completo
    public Entorno(boolean espera_bebe,
                   boolean tiene_discapacidad_fobia,
                   String especificacion_discapacidad,
                   String decision_familiar,
                   List<MiembroFamiliar> miembros) {
        this.espera_bebe = espera_bebe;
        this.tiene_discapacidad_fobia = tiene_discapacidad_fobia;
        this.especificacion_discapacidad = especificacion_discapacidad;
        this.decision_familiar = decision_familiar;
        this.miembros = miembros;
    }

    // Getters y Setters

    public boolean isEspera_bebe() {
        return espera_bebe;
    }

    public void setEspera_bebe(boolean espera_bebe) {
        this.espera_bebe = espera_bebe;
    }

    public boolean isTiene_discapacidad_fobia() {
        return tiene_discapacidad_fobia;
    }

    public void setTiene_discapacidad_fobia(boolean tiene_discapacidad_fobia) {
        this.tiene_discapacidad_fobia = tiene_discapacidad_fobia;
    }

    public String getEspecificacion_discapacidad() {
        return especificacion_discapacidad;
    }

    public void setEspecificacion_discapacidad(String especificacion_discapacidad) {
        this.especificacion_discapacidad = especificacion_discapacidad;
    }

    public String getDecision_familiar() {
        return decision_familiar;
    }

    public void setDecision_familiar(String decision_familiar) {
        this.decision_familiar = decision_familiar;
    }

    public List<MiembroFamiliar> getMiembros() {
        return miembros;
    }

    public void setMiembros(List<MiembroFamiliar> miembros) {
        this.miembros = miembros;
    }
}
