package com.rental.model;

import org.bson.types.ObjectId;
import java.time.LocalDate;

public class Garantia {
    private ObjectId id;
    private ObjectId equipoId;
    private String nombreEquipoSnapshot; // To show in list without join

    private String proveedor;
    private String numeroSerie;
    private LocalDate fechaCompra;
    private LocalDate fechaFin; // Expiration

    private String cobertura; // Description of what is covered
    private String contactoSoporte; // Phone/Email

    public Garantia() {
    }

    public Garantia(ObjectId equipoId, LocalDate compra, LocalDate fin) {
        this.equipoId = equipoId;
        this.fechaCompra = compra;
        this.fechaFin = fin;
    }

    public boolean isActiva() {
        if (fechaFin == null)
            return false;
        return LocalDate.now().isBefore(fechaFin) || LocalDate.now().isEqual(fechaFin);
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

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public String getNumeroSerie() {
        return numeroSerie;
    }

    public void setNumeroSerie(String numeroSerie) {
        this.numeroSerie = numeroSerie;
    }

    public LocalDate getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(LocalDate fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public void asignarFechaInicio(LocalDate date) {
        this.fechaCompra = date;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getCobertura() {
        return cobertura;
    }

    public void setCobertura(String cobertura) {
        this.cobertura = cobertura;
    }

    public String getContactoSoporte() {
        return contactoSoporte;
    }

    public void setContactoSoporte(String contactoSoporte) {
        this.contactoSoporte = contactoSoporte;
    }
}
