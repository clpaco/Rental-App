package com.rental.model;

import com.rental.model.enums.EstadoReserva;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Reserva {
    private ObjectId id;
    private ObjectId facturaId; // Optional link to invoice
    private ObjectId clienteId;
    private String nombreClienteSnapshot;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private EstadoReserva estado;

    private List<ObjectId> equipoIds = new ArrayList<>();

    private String notas;

    public Reserva() {
        this.estado = EstadoReserva.CONFIRMADA;
    }

    public Reserva(ObjectId clienteId, LocalDate inicio, LocalDate fin) {
        this();
        this.clienteId = clienteId;
        this.fechaInicio = inicio;
        this.fechaFin = fin;
    }

    // Getters and Setters
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public ObjectId getFacturaId() {
        return facturaId;
    }

    public void setFacturaId(ObjectId facturaId) {
        this.facturaId = facturaId;
    }

    public ObjectId getClienteId() {
        return clienteId;
    }

    public void setClienteId(ObjectId clienteId) {
        this.clienteId = clienteId;
    }

    public void asignarCliente(Usuario cliente) {
        if (cliente != null) {
            this.clienteId = cliente.getId();
            this.nombreClienteSnapshot = cliente.getNombre();
        }
    }

    public String getNombreClienteSnapshot() {
        return nombreClienteSnapshot;
    }

    public void setNombreClienteSnapshot(String nombreClienteSnapshot) {
        this.nombreClienteSnapshot = nombreClienteSnapshot;
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

    public EstadoReserva getEstado() {
        return estado;
    }

    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }

    public List<ObjectId> getEquipoIds() {
        return equipoIds;
    }

    public void setEquipoIds(List<ObjectId> equipoIds) {
        this.equipoIds = equipoIds;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    // Helpers
    public void agregarEquipo(Equipo equipo) {
        if (equipo != null && equipo.getId() != null) {
            this.equipoIds.add(equipo.getId());
        }
    }

    private double total;

    public void setTotal(double total) {
        this.total = total;
    }

    public double getTotal() {
        return total;
    }
}
