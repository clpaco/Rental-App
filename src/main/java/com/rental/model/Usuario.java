package com.rental.model;

import com.rental.model.enums.TipoUsuario;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

public class Usuario {
    private ObjectId id;
    private String nombre;
    private String email;
    private String telefono;
    private String nif;
    private String direccion;
    private String notas;

    // Internal fields
    private TipoUsuario rol;
    private String username;
    private String password; // In a real app, this should be hashed
    private LocalDateTime fechaRegistro;

    public Usuario() {
        this.fechaRegistro = LocalDateTime.now();
    }

    public Usuario(String nombre, String email, TipoUsuario rol) {
        this();
        this.nombre = nombre;
        this.email = email;
        this.rol = rol;
    }

    // Getters and Setters
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public TipoUsuario getRol() {
        return rol;
    }

    public void setRol(TipoUsuario rol) {
        this.rol = rol;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @Override
    public String toString() {
        return nombre + " (" + email + ")";
    }
}
