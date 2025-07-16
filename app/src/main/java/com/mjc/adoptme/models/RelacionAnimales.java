package com.mjc.adoptme.models;

import com.google.gson.annotations.SerializedName;

public class RelacionAnimales {
    @SerializedName("tiene_mascotas")
    private boolean tieneMascotas;

    @SerializedName("numero_mascotas")
    private Integer numeroMascotas;

    @SerializedName("tipos_mascotas")
    private String tiposMascotas;

    @SerializedName("mascotas_esterilizadas")
    private Boolean mascotasEsterilizadas;

    @SerializedName("tuvo_mascotas")
    private boolean tuvoMascotas;

    @SerializedName("que_paso_mascotas")
    private String quePasoMascotas;

    @SerializedName("experiencia_perros")
    private boolean experienciaPerros;

    @SerializedName("descripcion_experiencia")
    private String descripcionExperiencia;

    @SerializedName("razon_adopcion")
    private String razonAdopcion;

    @SerializedName("caracteristicas_mascota")
    private String caracteristicasMascota;

    @SerializedName("tiempo_solo")
    private String tiempoSolo;

    @SerializedName("lugar_dormir")
    private String lugarDormir;

    @SerializedName("gastos_mensuales")
    private String gastosMensuales;

    @SerializedName("responsable_cuidado")
    private String responsableCuidado;

    @SerializedName("plan_vacaciones")
    private String planVacaciones;

    @SerializedName("compromiso_esterilizacion")
    private boolean compromisoEsterilizacion;

    @SerializedName("compromiso_seguimiento")
    private boolean compromisoSeguimiento;

    // Constructor vacío
    public RelacionAnimales() {
    }

    // Getters y Setters
    public boolean isTieneMascotas() {
        return tieneMascotas;
    }

    public void setTieneMascotas(boolean tieneMascotas) {
        this.tieneMascotas = tieneMascotas;
    }

    public Integer getNumeroMascotas() {
        return numeroMascotas;
    }

    public void setNumeroMascotas(Integer numeroMascotas) {
        this.numeroMascotas = numeroMascotas;
    }

    public String getTiposMascotas() {
        return tiposMascotas;
    }

    public void setTiposMascotas(String tiposMascotas) {
        this.tiposMascotas = tiposMascotas;
    }

    public Boolean getMascotasEsterilizadas() {
        return mascotasEsterilizadas;
    }

    public void setMascotasEsterilizadas(Boolean mascotasEsterilizadas) {
        this.mascotasEsterilizadas = mascotasEsterilizadas;
    }

    public boolean isTuvoMascotas() {
        return tuvoMascotas;
    }

    public void setTuvoMascotas(boolean tuvoMascotas) {
        this.tuvoMascotas = tuvoMascotas;
    }

    public String getQuePasoMascotas() {
        return quePasoMascotas;
    }

    public void setQuePasoMascotas(String quePasoMascotas) {
        this.quePasoMascotas = quePasoMascotas;
    }

    public boolean isExperienciaPerros() {
        return experienciaPerros;
    }

    public void setExperienciaPerros(boolean experienciaPerros) {
        this.experienciaPerros = experienciaPerros;
    }

    public String getDescripcionExperiencia() {
        return descripcionExperiencia;
    }

    public void setDescripcionExperiencia(String descripcionExperiencia) {
        this.descripcionExperiencia = descripcionExperiencia;
    }

    public String getRazonAdopcion() {
        return razonAdopcion;
    }

    public void setRazonAdopcion(String razonAdopcion) {
        this.razonAdopcion = razonAdopcion;
    }

    public String getCaracteristicasMascota() {
        return caracteristicasMascota;
    }

    public void setCaracteristicasMascota(String caracteristicasMascota) {
        this.caracteristicasMascota = caracteristicasMascota;
    }

    public String getTiempoSolo() {
        return tiempoSolo;
    }

    public void setTiempoSolo(String tiempoSolo) {
        this.tiempoSolo = tiempoSolo;
    }

    public String getLugarDormir() {
        return lugarDormir;
    }

    public void setLugarDormir(String lugarDormir) {
        this.lugarDormir = lugarDormir;
    }

    public String getGastosMensuales() {
        return gastosMensuales;
    }

    public void setGastosMensuales(String gastosMensuales) {
        this.gastosMensuales = gastosMensuales;
    }

    public String getResponsableCuidado() {
        return responsableCuidado;
    }

    public void setResponsableCuidado(String responsableCuidado) {
        this.responsableCuidado = responsableCuidado;
    }

    public String getPlanVacaciones() {
        return planVacaciones;
    }

    public void setPlanVacaciones(String planVacaciones) {
        this.planVacaciones = planVacaciones;
    }

    public boolean isCompromisoEsterilizacion() {
        return compromisoEsterilizacion;
    }

    public void setCompromisoEsterilizacion(boolean compromisoEsterilizacion) {
        this.compromisoEsterilizacion = compromisoEsterilizacion;
    }

    public boolean isCompromisoSeguimiento() {
        return compromisoSeguimiento;
    }

    public void setCompromisoSeguimiento(boolean compromisoSeguimiento) {
        this.compromisoSeguimiento = compromisoSeguimiento;
    }
}