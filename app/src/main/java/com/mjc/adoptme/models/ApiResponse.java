// Ubicación: com.mjc.adoptme.models

package com.mjc.adoptme.models;

import com.google.gson.annotations.SerializedName;

/**
 * Clase genérica para envolver todas las respuestas de la API.
 * Permite manejar el estado, el mensaje y los datos de forma estandarizada.
 * @param <T> El tipo de objeto que contendrá el campo "data".
 */
public class ApiResponse<T> {

    @SerializedName("status")
    private int status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private T data;

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}