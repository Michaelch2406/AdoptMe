package com.mjc.adoptme.data;
import com.mjc.adoptme.models.RegistroCompleto;

public class RegistroRepository {

    private static RegistroRepository instance;
    private RegistroCompleto registroData;

    // El constructor es privado para forzar el uso de getInstance()
    private RegistroRepository() {
        registroData = new RegistroCompleto();
    }

    // Método para obtener la única instancia de la clase (patrón Singleton)
    public static synchronized RegistroRepository getInstance() {
        if (instance == null) {
            instance = new RegistroRepository();
        }
        return instance;
    }

    // Método para obtener el objeto de datos que se está construyendo
    public RegistroCompleto getRegistroData() {
        return registroData;
    }

    // Método para limpiar todos los datos y empezar un nuevo registro
    public void limpiarDatos() {
        // La forma más segura es crear una nueva instancia
        instance = new RegistroRepository();
    }
}