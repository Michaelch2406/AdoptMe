package com.mjc.adoptme.models;

import com.google.gson.annotations.SerializedName;

public class AdopcionUsuario {
    
    @SerializedName("adopcion_id")
    private int adopcionId;
    
    @SerializedName("animal_id")
    private int animalId;
    
    @SerializedName("nombre_animal")
    private String nombreAnimal;
    
    @SerializedName("estado")
    private String estado;
    
    @SerializedName("fecha_solicitud")
    private String fechaSolicitud;
    
    @SerializedName("fecha_aprobacion")
    private String fechaAprobacion;
    
    @SerializedName("fecha_completada")
    private String fechaCompletada;
    
    @SerializedName("fecha_cancelacion")
    private String fechaCancelacion;
    
    @SerializedName("fecha_devolucion")
    private String fechaDevolucion;

    public AdopcionUsuario() {}

    // Getters y Setters
    public int getAdopcionId() { return adopcionId; }
    public void setAdopcionId(int adopcionId) { this.adopcionId = adopcionId; }

    public int getAnimalId() { return animalId; }
    public void setAnimalId(int animalId) { this.animalId = animalId; }

    public String getNombreAnimal() { return nombreAnimal; }
    public void setNombreAnimal(String nombreAnimal) { this.nombreAnimal = nombreAnimal; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(String fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }

    public String getFechaAprobacion() { return fechaAprobacion; }
    public void setFechaAprobacion(String fechaAprobacion) { this.fechaAprobacion = fechaAprobacion; }

    public String getFechaCompletada() { return fechaCompletada; }
    public void setFechaCompletada(String fechaCompletada) { this.fechaCompletada = fechaCompletada; }

    public String getFechaCancelacion() { return fechaCancelacion; }
    public void setFechaCancelacion(String fechaCancelacion) { this.fechaCancelacion = fechaCancelacion; }

    public String getFechaDevolucion() { return fechaDevolucion; }
    public void setFechaDevolucion(String fechaDevolucion) { this.fechaDevolucion = fechaDevolucion; }
    
    // Helper methods for UI
    public String getEstadoDisplayText() {
        switch (estado) {
            case "SOLICITADA": return "Solicitada";
            case "EN_REVISION": return "En Revisión";
            case "APROBADA": return "Aprobada";
            case "RECHAZADA": return "Rechazada";
            case "COMPLETADA": return "Completada";
            case "CANCELADA": return "Cancelada";
            case "DEVUELTA": return "Devuelta";
            case "EN_ESPERA": return "En Espera";
            default: return estado;
        }
    }
    
    public String getEstadoColor() {
        switch (estado) {
            case "SOLICITADA": return "#2196F3"; // Blue
            case "EN_REVISION": return "#FF9800"; // Orange
            case "APROBADA": return "#4CAF50"; // Green
            case "RECHAZADA": return "#F44336"; // Red
            case "COMPLETADA": return "#8BC34A"; // Light Green
            case "CANCELADA": return "#9E9E9E"; // Gray
            case "DEVUELTA": return "#795548"; // Brown
            case "EN_ESPERA": return "#FFC107"; // Amber
            default: return "#607D8B"; // Blue Gray
        }
    }
    
    public String getFechaRelevante() {
        switch (estado) {
            case "SOLICITADA":
            case "EN_REVISION":
            case "EN_ESPERA":
                return fechaSolicitud;
            case "APROBADA":
                return fechaAprobacion != null ? fechaAprobacion : fechaSolicitud;
            case "COMPLETADA":
                return fechaCompletada != null ? fechaCompletada : fechaSolicitud;
            case "RECHAZADA":
            case "CANCELADA":
                return fechaCancelacion != null ? fechaCancelacion : fechaSolicitud;
            case "DEVUELTA":
                return fechaDevolucion != null ? fechaDevolucion : fechaSolicitud;
            default:
                return fechaSolicitud;
        }
    }
}