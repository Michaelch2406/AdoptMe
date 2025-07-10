package com.mjc.adoptme.models;

import java.util.Date;

public class Animal {
    private int id;
    private String codigoIdentificacion; // ATSX-00001
    private int fundacionId;
    private String fundacionNombre;
    private int tipoAnimalId;
    private String tipoAnimalNombre; // Canino, Felino
    private Integer razaId;
    private String razaNombre;

    // Información básica
    private String nombre;
    private Date fechaNacimiento;
    private Integer edadAproximadaMeses;
    private String genero; // macho, hembra
    private String color;
    private Double pesoKg;
    private String tamaño; // pequeño, mediano, grande, gigante
    private String edadEtiqueta; // CACHORRO, ADULTO

    // Estado del animal
    private boolean esterilizado;
    private Date fechaEsterilizacion;
    private String discapacidades;
    private String caracteristicasEspeciales;
    private String historiaRescate;

    // Estado de adopción
    private boolean disponibleAdopcion;
    private boolean adoptado;
    private Date fechaIngreso;
    private Date fechaAdopcion;

    // Información adicional
    private String descripcion;
    private String cuidadosEspeciales;
    private String informacionExtra;

    // Ubicación de la fundación
    private double latitud;
    private double longitud;
    private String ubicacionFundacion;

    // Información de salud
    private boolean vacunado;
    private boolean desparasitado;

    // Comportamiento
    private boolean buenoConNiños;
    private boolean buenoConAnimales;

    // Imagen principal
    private String imagenPrincipalUrl;

    // Calculado
    private float distancia; // Distancia desde el usuario

    // Sistema
    private boolean activo;
    private Date createdAt;
    private Date updatedAt;

    // Constructor vacío
    // REEMPLAZA el constructor vacío con ESTE
    public Animal(int id, String nombre, String razaNombre, String tipoAnimalNombre,
                  int edadAproximadaMeses, String edadEtiqueta, String tamaño, String genero,
                  String descripcion, String fundacionNombre, String ubicacionFundacion,
                  boolean vacunado, boolean desparasitado, boolean esterilizado,
                  boolean buenoConNiños, boolean buenoConAnimales, String imagenPrincipalUrl,
                  double latitud, double longitud, float distancia) {
        this.id = id;
        this.nombre = nombre;
        this.razaNombre = razaNombre;
        this.tipoAnimalNombre = tipoAnimalNombre;
        this.edadAproximadaMeses = edadAproximadaMeses;
        this.edadEtiqueta = edadEtiqueta; // Campo nuevo
        this.tamaño = tamaño;
        this.genero = genero;
        this.descripcion = descripcion;
        this.fundacionNombre = fundacionNombre;
        this.ubicacionFundacion = ubicacionFundacion;
        this.vacunado = vacunado;
        this.desparasitado = desparasitado;
        this.esterilizado = esterilizado;
        this.buenoConNiños = buenoConNiños;
        this.buenoConAnimales = buenoConAnimales;
        this.imagenPrincipalUrl = imagenPrincipalUrl;
        this.latitud = latitud;
        this.longitud = longitud;
        this.distancia = distancia;
    }
    // Constructor con parámetros básicos
    public Animal(int id, String codigoIdentificacion, String nombre,
                  String tipoAnimalNombre, String genero, Integer edadAproximadaMeses,
                  String tamaño, String descripcion) {
        this.id = id;
        this.codigoIdentificacion = codigoIdentificacion;
        this.nombre = nombre;
        this.tipoAnimalNombre = tipoAnimalNombre;
        this.genero = genero;
        this.edadAproximadaMeses = edadAproximadaMeses;
        this.tamaño = tamaño;
        this.descripcion = descripcion;
    }

    // Métodos helper
    public String getEdadTexto() {
        if (edadAproximadaMeses == null) return "Edad desconocida";

        if (edadAproximadaMeses < 12) {
            return edadAproximadaMeses + " mes" + (edadAproximadaMeses != 1 ? "es" : "");
        } else {
            int años = edadAproximadaMeses / 12;
            return años + " año" + (años != 1 ? "s" : "");
        }
    }

    public String getGeneroTexto() {
        return "macho".equals(genero) ? "Macho" : "Hembra";
    }

    public String getTamañoTexto() {
        if (tamaño == null) return "";
        switch (tamaño) {
            case "pequeño": return "Pequeño";
            case "mediano": return "Mediano";
            case "grande": return "Grande";
            case "gigante": return "Gigante";
            default: return tamaño;
        }
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigoIdentificacion() { return codigoIdentificacion; }
    public void setCodigoIdentificacion(String codigoIdentificacion) {
        this.codigoIdentificacion = codigoIdentificacion;
    }

    public int getFundacionId() { return fundacionId; }
    public void setFundacionId(int fundacionId) { this.fundacionId = fundacionId; }

    public String getFundacionNombre() { return fundacionNombre; }
    public void setFundacionNombre(String fundacionNombre) {
        this.fundacionNombre = fundacionNombre;
    }

    public int getTipoAnimalId() { return tipoAnimalId; }
    public void setTipoAnimalId(int tipoAnimalId) { this.tipoAnimalId = tipoAnimalId; }

    public String getTipoAnimalNombre() { return tipoAnimalNombre; }
    public void setTipoAnimalNombre(String tipoAnimalNombre) {
        this.tipoAnimalNombre = tipoAnimalNombre;
    }

    public Integer getRazaId() { return razaId; }
    public void setRazaId(Integer razaId) { this.razaId = razaId; }

    public String getRazaNombre() { return razaNombre; }
    public void setRazaNombre(String razaNombre) { this.razaNombre = razaNombre; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Date getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Integer getEdadAproximadaMeses() { return edadAproximadaMeses; }
    public void setEdadAproximadaMeses(Integer edadAproximadaMeses) {
        this.edadAproximadaMeses = edadAproximadaMeses;
    }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public Double getPesoKg() { return pesoKg; }
    public void setPesoKg(Double pesoKg) { this.pesoKg = pesoKg; }

    public String getTamaño() { return tamaño; }
    public void setTamaño(String tamaño) { this.tamaño = tamaño; }

    public boolean isEsterilizado() { return esterilizado; }
    public void setEsterilizado(boolean esterilizado) { this.esterilizado = esterilizado; }

    public Date getFechaEsterilizacion() { return fechaEsterilizacion; }
    public void setFechaEsterilizacion(Date fechaEsterilizacion) {
        this.fechaEsterilizacion = fechaEsterilizacion;
    }

    public String getDiscapacidades() { return discapacidades; }
    public void setDiscapacidades(String discapacidades) {
        this.discapacidades = discapacidades;
    }

    public String getCaracteristicasEspeciales() { return caracteristicasEspeciales; }
    public void setCaracteristicasEspeciales(String caracteristicasEspeciales) {
        this.caracteristicasEspeciales = caracteristicasEspeciales;
    }

    public String getHistoriaRescate() { return historiaRescate; }
    public void setHistoriaRescate(String historiaRescate) {
        this.historiaRescate = historiaRescate;
    }

    public boolean isDisponibleAdopcion() { return disponibleAdopcion; }
    public void setDisponibleAdopcion(boolean disponibleAdopcion) {
        this.disponibleAdopcion = disponibleAdopcion;
    }

    public boolean isAdoptado() { return adoptado; }
    public void setAdoptado(boolean adoptado) { this.adoptado = adoptado; }

    public Date getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(Date fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    public Date getFechaAdopcion() { return fechaAdopcion; }
    public void setFechaAdopcion(Date fechaAdopcion) { this.fechaAdopcion = fechaAdopcion; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getCuidadosEspeciales() { return cuidadosEspeciales; }
    public void setCuidadosEspeciales(String cuidadosEspeciales) {
        this.cuidadosEspeciales = cuidadosEspeciales;
    }

    public String getInformacionExtra() { return informacionExtra; }
    public void setInformacionExtra(String informacionExtra) {
        this.informacionExtra = informacionExtra;
    }

    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }

    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }

    public String getUbicacionFundacion() { return ubicacionFundacion; }
    public void setUbicacionFundacion(String ubicacionFundacion) {
        this.ubicacionFundacion = ubicacionFundacion;
    }

    public boolean isVacunado() { return vacunado; }
    public void setVacunado(boolean vacunado) { this.vacunado = vacunado; }

    public boolean isDesparasitado() { return desparasitado; }
    public void setDesparasitado(boolean desparasitado) { this.desparasitado = desparasitado; }

    public boolean isBuenoConNiños() { return buenoConNiños; }
    public void setBuenoConNiños(boolean buenoConNiños) {
        this.buenoConNiños = buenoConNiños;
    }

    public boolean isBuenoConAnimales() { return buenoConAnimales; }
    public void setBuenoConAnimales(boolean buenoConAnimales) {
        this.buenoConAnimales = buenoConAnimales;
    }

    public String getImagenPrincipalUrl() { return imagenPrincipalUrl; }
    public void setImagenPrincipalUrl(String imagenPrincipalUrl) {
        this.imagenPrincipalUrl = imagenPrincipalUrl;
    }

    public float getDistancia() { return distancia; }
    public void setDistancia(float distancia) { this.distancia = distancia; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
    public String getEdadEtiqueta() { return edadEtiqueta; }
}