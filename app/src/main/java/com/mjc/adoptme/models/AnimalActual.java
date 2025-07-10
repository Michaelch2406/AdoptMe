package com.mjc.adoptme.models;

import com.google.gson.annotations.SerializedName;

public class AnimalActual {

    @SerializedName("tipo_animal_id")
    private int tipoAnimalId;

    @SerializedName("genero_id")
    private int generoId;

    private int edad;
    private boolean esterilizado;

    @SerializedName("vive_con_usuario")
    private boolean viveConUsuario;

    @SerializedName("esta_en_veterinario")
    private boolean estaEnVeterinario;

    public AnimalActual() {
    }

    public AnimalActual(int tipoAnimalId, int generoId, int edad, boolean esterilizado,
                        boolean viveConUsuario, boolean estaEnVeterinario) {
        this.tipoAnimalId = tipoAnimalId;
        this.generoId = generoId;
        this.edad = edad;
        this.esterilizado = esterilizado;
        this.viveConUsuario = viveConUsuario;
        this.estaEnVeterinario = estaEnVeterinario;
    }

    public int getTipoAnimalId() {
        return tipoAnimalId;
    }

    public void setTipoAnimalId(int tipoAnimalId) {
        this.tipoAnimalId = tipoAnimalId;
    }

    public int getGeneroId() {
        return generoId;
    }

    public void setGeneroId(int generoId) {
        this.generoId = generoId;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public boolean isEsterilizado() {
        return esterilizado;
    }

    public void setEsterilizado(boolean esterilizado) {
        this.esterilizado = esterilizado;
    }

    public boolean isViveConUsuario() {
        return viveConUsuario;
    }

    public void setViveConUsuario(boolean viveConUsuario) {
        this.viveConUsuario = viveConUsuario;
    }

    public boolean isEstaEnVeterinario() {
        return estaEnVeterinario;
    }

    public void setEstaEnVeterinario(boolean estaEnVeterinario) {
        this.estaEnVeterinario = estaEnVeterinario;
    }
}
