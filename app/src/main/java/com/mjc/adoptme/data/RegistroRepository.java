package com.mjc.adoptme.data;

import com.mjc.adoptme.models.RegistroCompleto;

public class RegistroRepository {
    private static RegistroRepository instance;
    private RegistroCompleto registroData;

    private RegistroRepository() {
        registroData = new RegistroCompleto();
    }

    public static synchronized RegistroRepository getInstance() {
        if (instance == null) {
            instance = new RegistroRepository();
        }
        return instance;
    }

    public RegistroCompleto getRegistroData() {
        return registroData;
    }

    public void setRegistroData(RegistroCompleto registroData) {
        this.registroData = registroData;
    }

    public void clearData() {
        registroData = new RegistroCompleto();
    }

    public void limpiarDatos() {
        registroData = new RegistroCompleto();
    }
}