package com.rental.model;

import org.bson.types.ObjectId;

public class LineaFactura {
    private ObjectId equipoId;
    private String concepto; // Snapshot of Equipment Name or custom text
    private int cantidad;
    private double precioDia;
    private int dias;
    private double totalLinea;

    public LineaFactura() {
    }

    public LineaFactura(ObjectId equipoId, String concepto, int cantidad, double precioDia, int dias) {
        this.equipoId = equipoId;
        this.concepto = concepto;
        this.cantidad = cantidad;
        this.precioDia = precioDia;
        this.dias = dias;
        this.calcularTotal();
    }

    public void calcularTotal() {
        this.totalLinea = this.cantidad * this.precioDia * this.dias;
    }

    // Getters and Setters
    public ObjectId getEquipoId() {
        return equipoId;
    }

    public void setEquipoId(ObjectId equipoId) {
        this.equipoId = equipoId;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        calcularTotal();
    }

    public double getPrecioDia() {
        return precioDia;
    }

    public void setPrecioDia(double precioDia) {
        this.precioDia = precioDia;
        calcularTotal();
    }

    public int getDias() {
        return dias;
    }

    public void setDias(int dias) {
        this.dias = dias;
        calcularTotal();
    }

    public double getTotalLinea() {
        return totalLinea;
    }

    public void setTotalLinea(double totalLinea) {
        this.totalLinea = totalLinea;
    }
}
