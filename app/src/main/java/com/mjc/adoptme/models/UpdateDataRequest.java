package com.mjc.adoptme.models;

import com.google.gson.annotations.SerializedName;

/**
 * Clase genérica para envolver los datos en una solicitud de actualización.
 * @param <T> El tipo del objeto 'data' (DatosPersonalesData, DomicilioData, o List<ReferenciaPersonal>)
 */
public class UpdateDataRequest<T> {

    @SerializedName("cedula")
    private String cedula;

    @SerializedName("codigo")
    private String codigo;

    @SerializedName("data")
    private T data;

    public UpdateDataRequest(String cedula, String codigo, T data) {
        this.cedula = cedula;
        this.codigo = codigo;
        this.data = data;
    }

    // Getters
    public String getCedula() {
        return cedula;
    }

    public String getCodigo() {
        return codigo;
    }

    public T getData() {
        return data;
    }
}