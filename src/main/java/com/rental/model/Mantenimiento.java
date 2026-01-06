package com.rental.model;

import com.rental.model.enums.EstadoMantenimiento;
import com.rental.model.enums.TipoMantenimiento;
import org.bson.types.ObjectId;

import java.time.LocalDate;

public class Mantenimiento {
    private ObjectId id;
    private ObjectId equipoId;
    private String nombreEquipoSnapshot;

    private TipoMantenimiento tipo;
    private EstadoMantenimiento estado;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private double coste;
    private String descripcion;
    private String realizadoPor; // Technician name or external provider

    public Mantenimiento() {
        this.estado = EstadoMantenimiento.PENDIENTE;
        this.tipo = TipoMantenimiento.PREVENTIVO;
    }

    public Mantenimiento(ObjectId equipoId, LocalDate inicio) {
        this();
        this.equipoId = equipoId;
        this.fechaInicio = inicio;
    }

    // Getters and Setters
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public ObjectId getEquipoId() {
        return equipoId;
    }

    public void setEquipoId(ObjectId equipoId) {
        this.equipoId = equipoId;
    }

    public void asignarEquipo(Equipo equipo) {
        if (equipo != null) {
            this.equipoId = equipo.getId();
            this.nombreEquipoSnapshot = equipo.getNombre();
        }
    }

    public String getNombreEquipoSnapshot() {
        return nombreEquipoSnapshot;
    }

    public void setNombreEquipoSnapshot(String nombreEquipoSnapshot) {
        this.nombreEquipoSnapshot = nombreEquipoSnapshot;
    }

    public TipoMantenimiento getTipo() {
        return tipo;
    }

    public void setTipo(TipoMantenimiento tipo) {
        this.tipo = tipo;
    }

    public EstadoMantenimiento getEstado() {
        return estado;
    }

    public void setEstado(EstadoMantenimiento estado) {
        this.estado = estado;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public double getCoste() {
        return coste;
    }

    public void setCoste(double coste) {
        this.coste = coste;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getRealizadoPor() {
        return realizadoPor;
    }

    public void setRealizadoPor(String realizadoPor) {
        this.realizadoPor = realizadoPor;
    }

    // Helpers
    public void asignarNotas(String n) {
        this.descripcion = n;
    }

    public void asignarCosto(double c) {
        this.coste = c;
    }

    public void asignarFechaIngreso(LocalDate d) {
        this.fechaInicio = d;
    }

    public void asignarFechaFinalizacion(LocalDate d) {
        this.fechaFin = d;
    }
}
