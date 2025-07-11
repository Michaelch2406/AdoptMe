package com.mjc.adoptme.models;
import com.google.gson.annotations.SerializedName;

public class AnimalHistorial {
    @SerializedName("tipo_animal_id") private int tipoAnimalId;
    @SerializedName("edad") private int edad;
    @SerializedName("razon") private String razon;
    @SerializedName("especificacion") private String especificacion;
    // Getters y Setters...
    // Constructor por defecto
    public AnimalHistorial() {
    }
    public AnimalHistorial(int tipoAnimalId, int edad, String razon, String especificacion) {
        this.tipoAnimalId = tipoAnimalId;
        this.edad = edad;
        this.razon = razon;
        this.especificacion = especificacion;
    }
    public int getTipoAnimalId() {
        return tipoAnimalId;
    }
    public void setTipoAnimalId(int tipoAnimalId) {
        this.tipoAnimalId = tipoAnimalId;
    }
    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }
    public String getRazon() {
        return razon;
    }
    public void setRazon(String razon) {
        this.razon = razon;
    }
    public String getEspecificacion() {
        return especificacion;
    }
    public void setEspecificacion(String especificacion) {
        this.especificacion = especificacion;
    }
}