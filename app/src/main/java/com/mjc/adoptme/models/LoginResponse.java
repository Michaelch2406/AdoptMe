package com.mjc.adoptme.models;
import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("status")
    private int status;

    @SerializedName("message")
    private String message;

    // Ahora esperamos un objeto llamado "data"
    @SerializedName("data")
    private LoginData data;

    // NOTA: Como la API no devuelve un token en esta llamada,
    // hemos eliminado el campo 'token' de aquí.

    // Getters
    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public LoginData getData() {
        return data;
    }

    // Clase anidada para el objeto "data"
    public static class LoginData {

        // El campo se llama "name_user" en el JSON
        @SerializedName("name_user")
        private String nameUser;

        // Getter
        public String getNameUser() {
            return nameUser;
        }
    }
}