package com.rental.model;

import com.rental.model.enums.EstadoFactura;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Factura {
    private ObjectId id;
    private String numeroFactura;
    private ObjectId clienteId;
    private String nombreClienteSnapshot; // To show without joining
    private LocalDate fecha;
    private List<LineaFactura> items = new ArrayList<>();

    // Financials
    private double subtotal;
    private double iva; // Amount
    private double tasaIva = 0.21; // 21% default
    private double depositoSeguridad;
    private double total;

    private EstadoFactura estado;
    private String notas;

    public Factura() {
        this.fecha = LocalDate.now();
        this.estado = EstadoFactura.COTIZACION;
    }

    public void recalcular() {
        this.subtotal = items.stream().mapToDouble(LineaFactura::getTotalLinea).sum();
        this.iva = this.subtotal * this.tasaIva;
        this.total = this.subtotal + this.iva;
        // Deposit is usually separate or part of payments, here handled as a field
    }

    public void agregarItem(LineaFactura item) {
        this.items.add(item);
        recalcular();
    }

    // Getters and Setters
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
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

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void asignarFechaEmision(LocalDate date) {
        this.fecha = date;
    }

    public List<LineaFactura> getItems() {
        return items;
    }

    public void setItems(List<LineaFactura> items) {
        this.items = items;
        recalcular();
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getIva() {
        return iva;
    }

    public void setIva(double iva) {
        this.iva = iva;
    }

    public double getTasaIva() {
        return tasaIva;
    }

    public void setTasaIva(double tasaIva) {
        this.tasaIva = tasaIva;
    }

    public double getDepositoSeguridad() {
        return depositoSeguridad;
    }

    public void setDepositoSeguridad(double depositoSeguridad) {
        this.depositoSeguridad = depositoSeguridad;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public EstadoFactura getEstado() {
        return estado;
    }

    public void setEstado(EstadoFactura estado) {
        this.estado = estado;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }
}
