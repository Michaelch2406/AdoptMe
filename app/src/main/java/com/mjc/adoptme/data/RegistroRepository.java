package com.mjc.adoptme.data;

import com.mjc.adoptme.models.RegistroGeneral;

public class RegistroRepository {

    private static RegistroRepository instance;
    private RegistroGeneral registroData;

    private RegistroRepository() {
        registroData = new RegistroGeneral();
    }

    public static synchronized RegistroRepository getInstance() {
        if (instance == null) {
            instance = new RegistroRepository();
        }
        return instance;
    }

    public RegistroGeneral getRegistroData() {
        return registroData;
    }

    // Limpia los datos después de un registro exitoso o cancelación
    public void clearData() {
        instance = new RegistroRepository();
    }
}