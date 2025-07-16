package com.mjc.adoptme.models;

public class DomicilioUpdate {
    private String cedula;
    private String codigo;
    private Domicilio data;

    public DomicilioUpdate(String cedula, String codigo, Domicilio data) {
        this.cedula = cedula;
        this.codigo = codigo;
        this.data = data;
    }
}
