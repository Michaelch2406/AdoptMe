package com.mjc.adoptme.models;

import java.util.List;

public class ReferenciasUpdate {
    private String cedula;
    private String codigo;
    private List<ReferenciaPersonal> data;

    public ReferenciasUpdate(String cedula, String codigo, List<ReferenciaPersonal> data) {
        this.cedula = cedula;
        this.codigo = codigo;
        this.data = data;
    }
    public String getCedula() {
        return cedula;
    }
    public String getCodigo() {
        return codigo;
    }
    public List<ReferenciaPersonal> getData() {
        return data;
    }
    public void setCedula(String cedula) {
        this.cedula = cedula;
    }
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    public void setData(List<ReferenciaPersonal> data) {
        this.data = data;
    }
}
