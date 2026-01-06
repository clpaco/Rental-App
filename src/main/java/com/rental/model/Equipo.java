package com.rental.model;

import com.rental.model.enums.EstadoEquipo;
import org.bson.types.ObjectId;

public class Equipo {
    private ObjectId id;
    private String codigo; // Unique alphanumeric code (e.g., CAM-001)
    private String nombre;
    private String categoria; // e.g., Camera, Lens, Lighting
    private EstadoEquipo estado;
    private double valorCompra;
    private double tarifaDiaria;
    private String descripcion;
    private String fotoUrl;

    public Equipo() {
    }

    public Equipo(String codigo, String nombre, String categoria, double tarifaDiaria) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.categoria = categoria;
        this.tarifaDiaria = tarifaDiaria;
        this.estado = EstadoEquipo.DISPONIBLE;
    }

    // Getters and Setters
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public EstadoEquipo getEstado() {
        return estado;
    }

    public void setEstado(EstadoEquipo estado) {
        this.estado = estado;
    }

    public double getValorCompra() {
        return valorCompra;
    }

    public void setValorCompra(double valorCompra) {
        this.valorCompra = valorCompra;
    }

    public double getTarifaDiaria() {
        return tarifaDiaria;
    }

    public void setTarifaDiaria(double tarifaDiaria) {
        this.tarifaDiaria = tarifaDiaria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre;
    }
}
