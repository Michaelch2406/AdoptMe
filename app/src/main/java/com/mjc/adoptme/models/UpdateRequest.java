package com.mjc.adoptme.models;

import com.google.gson.annotations.SerializedName;

/**
 * Modelo genérico para el cuerpo de las peticiones de actualización.
 * @param <T> El tipo del objeto que irá en el campo "data".
 */
public class UpdateRequest<T> {

    @SerializedName("cedula")
    private String cedula;

    @SerializedName("codigo")
    private String codigo;

    @SerializedName("data")
    private T data;

    public UpdateRequest(String cedula, String codigo, T data) {
        this.cedula = cedula;
        this.codigo = codigo;
        this.data = data;
    }

    public String getCedula() {
        return cedula;
    }
    public void setCedula(String cedula) {
        this.cedula = cedula;
    }
    public String getCodigo() {
        return codigo;
    }
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    public T getData() {
        return data;
    }
    public void setData(T data) {
        this.data = data;
    }
    // Getters por si son necesarios
}