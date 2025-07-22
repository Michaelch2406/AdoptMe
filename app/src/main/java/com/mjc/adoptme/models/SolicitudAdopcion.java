package com.mjc.adoptme.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SolicitudAdopcion {
    @SerializedName("animal_id")
    private int animal_id;
    
    @SerializedName("cedula_adoptante")
    private String cedula_adoptante;
    
    @SerializedName("motivaciones")
    private Motivaciones motivaciones;
    
    @SerializedName("cuidados")
    private List<String> cuidados;

    public SolicitudAdopcion() {}

    public SolicitudAdopcion(int animal_id, String cedula_adoptante, Motivaciones motivaciones, List<String> cuidados) {
        this.animal_id = animal_id;
        this.cedula_adoptante = cedula_adoptante;
        this.motivaciones = motivaciones;
        this.cuidados = cuidados;
    }

    public int getAnimalId() { return animal_id; }
    public void setAnimalId(int animal_id) { this.animal_id = animal_id; }

    public String getCedulaAdoptante() { return cedula_adoptante; }
    public void setCedulaAdoptante(String cedula_adoptante) { this.cedula_adoptante = cedula_adoptante; }

    public Motivaciones getMotivaciones() { return motivaciones; }
    public void setMotivaciones(Motivaciones motivaciones) { this.motivaciones = motivaciones; }

    public List<String> getCuidados() { return cuidados; }
    public void setCuidados(List<String> cuidados) { this.cuidados = cuidados; }

    public static class Motivaciones {
        @SerializedName("motivo_adopcion")
        private String motivo_adopcion;
        
        @SerializedName("plan_cambio_domicilio")
        private String plan_cambio_domicilio;
        
        @SerializedName("plan_viajes")
        private String plan_viajes;
        
        @SerializedName("plan_viajes_largos")
        private String plan_viajes_largos;
        
        @SerializedName("horas_solo_diarias")
        private int horas_solo_diarias;
        
        @SerializedName("ubicacion_dia")
        private String ubicacion_dia;
        
        @SerializedName("ubicacion_noche")
        private String ubicacion_noche;
        
        @SerializedName("lugar_dormir")
        private String lugar_dormir;
        
        @SerializedName("lugar_necesidades")
        private String lugar_necesidades;
        
        @SerializedName("frecuencia_baño")
        private String frecuencia_baño;
        
        @SerializedName("frecuencia_corte_pelo")
        private String frecuencia_corte_pelo;
        
        @SerializedName("tipo_alimentacion")
        private String tipo_alimentacion;
        
        @SerializedName("conoce_toxicos")
        private boolean conoce_toxicos;
        
        @SerializedName("toxicos_conocidos")
        private String toxicos_conocidos;
        
        @SerializedName("años_vida_estimados")
        private int años_vida_estimados;
        
        @SerializedName("plan_enfermedad")
        private String plan_enfermedad;
        
        @SerializedName("responsable_costos")
        private String responsable_costos;
        
        @SerializedName("presupuesto_mensual")
        private String presupuesto_mensual;
        
        @SerializedName("recursos_veterinarios")
        private boolean recursos_veterinarios;
        
        @SerializedName("acepta_visitas")
        private boolean acepta_visitas;
        
        @SerializedName("acepta_esterilizacion")
        private boolean acepta_esterilizacion;
        
        @SerializedName("razon_esterilizacion")
        private String razon_esterilizacion;
        
        @SerializedName("conoce_beneficios_esterilizacion")
        private boolean conoce_beneficios_esterilizacion;
        
        @SerializedName("plan_mal_comportamiento")
        private String plan_mal_comportamiento;
        
        @SerializedName("consciente_maltrato")
        private boolean consciente_maltrato;

        public Motivaciones() {}

        // Getters y setters
        public String getMotivoAdopcion() { return motivo_adopcion; }
        public void setMotivoAdopcion(String motivo_adopcion) { this.motivo_adopcion = motivo_adopcion; }

        public String getPlanCambioDomicilio() { return plan_cambio_domicilio; }
        public void setPlanCambioDomicilio(String plan_cambio_domicilio) { this.plan_cambio_domicilio = plan_cambio_domicilio; }

        public String getPlanViajes() { return plan_viajes; }
        public void setPlanViajes(String plan_viajes) { this.plan_viajes = plan_viajes; }

        public String getPlanViajesLargos() { return plan_viajes_largos; }
        public void setPlanViajesLargos(String plan_viajes_largos) { this.plan_viajes_largos = plan_viajes_largos; }

        public int getHorasSoloDiarias() { return horas_solo_diarias; }
        public void setHorasSoloDiarias(int horas_solo_diarias) { this.horas_solo_diarias = horas_solo_diarias; }

        public String getUbicacionDia() { return ubicacion_dia; }
        public void setUbicacionDia(String ubicacion_dia) { this.ubicacion_dia = ubicacion_dia; }

        public String getUbicacionNoche() { return ubicacion_noche; }
        public void setUbicacionNoche(String ubicacion_noche) { this.ubicacion_noche = ubicacion_noche; }

        public String getLugarDormir() { return lugar_dormir; }
        public void setLugarDormir(String lugar_dormir) { this.lugar_dormir = lugar_dormir; }

        public String getLugarNecesidades() { return lugar_necesidades; }
        public void setLugarNecesidades(String lugar_necesidades) { this.lugar_necesidades = lugar_necesidades; }

        public String getFrecuenciaBano() { return frecuencia_baño; }
        public void setFrecuenciaBano(String frecuencia_baño) { this.frecuencia_baño = frecuencia_baño; }

        public String getFrecuenciaCorte_pelo() { return frecuencia_corte_pelo; }
        public void setFrecuenciaCorte_pelo(String frecuencia_corte_pelo) { this.frecuencia_corte_pelo = frecuencia_corte_pelo; }

        public String getTipoAlimentacion() { return tipo_alimentacion; }
        public void setTipoAlimentacion(String tipo_alimentacion) { this.tipo_alimentacion = tipo_alimentacion; }

        public boolean isConoceToxicos() { return conoce_toxicos; }
        public void setConoceToxicos(boolean conoce_toxicos) { this.conoce_toxicos = conoce_toxicos; }

        public String getToxicosConocidos() { return toxicos_conocidos; }
        public void setToxicosConocidos(String toxicos_conocidos) { this.toxicos_conocidos = toxicos_conocidos; }

        public int getAnosVidaEstimados() { return años_vida_estimados; }
        public void setAnosVidaEstimados(int años_vida_estimados) { this.años_vida_estimados = años_vida_estimados; }

        public String getPlanEnfermedad() { return plan_enfermedad; }
        public void setPlanEnfermedad(String plan_enfermedad) { this.plan_enfermedad = plan_enfermedad; }

        public String getResponsableCostos() { return responsable_costos; }
        public void setResponsableCostos(String responsable_costos) { this.responsable_costos = responsable_costos; }

        public String getPresupuestoMensual() { return presupuesto_mensual; }
        public void setPresupuestoMensual(String presupuesto_mensual) { this.presupuesto_mensual = presupuesto_mensual; }

        public boolean isRecursosVeterinarios() { return recursos_veterinarios; }
        public void setRecursosVeterinarios(boolean recursos_veterinarios) { this.recursos_veterinarios = recursos_veterinarios; }

        public boolean isAceptaVisitas() { return acepta_visitas; }
        public void setAceptaVisitas(boolean acepta_visitas) { this.acepta_visitas = acepta_visitas; }

        public boolean isAceptaEsterilizacion() { return acepta_esterilizacion; }
        public void setAceptaEsterilizacion(boolean acepta_esterilizacion) { this.acepta_esterilizacion = acepta_esterilizacion; }

        public String getRazonEsterilizacion() { return razon_esterilizacion; }
        public void setRazonEsterilizacion(String razon_esterilizacion) { this.razon_esterilizacion = razon_esterilizacion; }

        public boolean isConoceBeneficiosEsterilizacion() { return conoce_beneficios_esterilizacion; }
        public void setConoceBeneficiosEsterilizacion(boolean conoce_beneficios_esterilizacion) { this.conoce_beneficios_esterilizacion = conoce_beneficios_esterilizacion; }

        public String getPlanMalComportamiento() { return plan_mal_comportamiento; }
        public void setPlanMalComportamiento(String plan_mal_comportamiento) { this.plan_mal_comportamiento = plan_mal_comportamiento; }

        public boolean isConscienteMaltrato() { return consciente_maltrato; }
        public void setConscienteMaltrato(boolean consciente_maltrato) { this.consciente_maltrato = consciente_maltrato; }
    }
}