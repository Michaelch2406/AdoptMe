package com.mjc.adoptme.models;

public class AnimalAPI {
    private int id;
    private String nombre;
    private String identificador_collar;
    private int tipo_animal_id;
    private int genero_id;
    private int rango_edad_id;
    private int tamano_id;
    private int sucursal_id;
    private int edad_aproximada;
    private double peso;
    private String color;
    private String descripcion;
    private String informacion_extra;
    private boolean esterilizado;
    private String fecha_esterilizacion;
    private String estado;
    private String fecha_ingreso;
    private String fecha_adoptado;
    private int cantidad_devoluciones;
    private boolean activo;
    private String fecha_creacion;
    private String fecha_actualizacion;

    public AnimalAPI() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getIdentificadorCollar() { return identificador_collar; }
    public void setIdentificadorCollar(String identificador_collar) { this.identificador_collar = identificador_collar; }

    public int getTipoAnimalId() { return tipo_animal_id; }
    public void setTipoAnimalId(int tipo_animal_id) { this.tipo_animal_id = tipo_animal_id; }

    public int getGeneroId() { return genero_id; }
    public void setGeneroId(int genero_id) { this.genero_id = genero_id; }

    public int getRangoEdadId() { return rango_edad_id; }
    public void setRangoEdadId(int rango_edad_id) { this.rango_edad_id = rango_edad_id; }

    public int getTamanoId() { return tamano_id; }
    public void setTamanoId(int tamano_id) { this.tamano_id = tamano_id; }

    public int getSucursalId() { return sucursal_id; }
    public void setSucursalId(int sucursal_id) { this.sucursal_id = sucursal_id; }

    public int getEdadAproximada() { return edad_aproximada; }
    public void setEdadAproximada(int edad_aproximada) { this.edad_aproximada = edad_aproximada; }

    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getInformacionExtra() { return informacion_extra; }
    public void setInformacionExtra(String informacion_extra) { this.informacion_extra = informacion_extra; }

    public boolean isEsterilizado() { return esterilizado; }
    public void setEsterilizado(boolean esterilizado) { this.esterilizado = esterilizado; }

    public String getFechaEsterilizacion() { return fecha_esterilizacion; }
    public void setFechaEsterilizacion(String fecha_esterilizacion) { this.fecha_esterilizacion = fecha_esterilizacion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getFechaIngreso() { return fecha_ingreso; }
    public void setFechaIngreso(String fecha_ingreso) { this.fecha_ingreso = fecha_ingreso; }

    public String getFechaAdoptado() { return fecha_adoptado; }
    public void setFechaAdoptado(String fecha_adoptado) { this.fecha_adoptado = fecha_adoptado; }

    public int getCantidadDevoluciones() { return cantidad_devoluciones; }
    public void setCantidadDevoluciones(int cantidad_devoluciones) { this.cantidad_devoluciones = cantidad_devoluciones; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public String getFechaCreacion() { return fecha_creacion; }
    public void setFechaCreacion(String fecha_creacion) { this.fecha_creacion = fecha_creacion; }

    public String getFechaActualizacion() { return fecha_actualizacion; }
    public void setFechaActualizacion(String fecha_actualizacion) { this.fecha_actualizacion = fecha_actualizacion; }
}