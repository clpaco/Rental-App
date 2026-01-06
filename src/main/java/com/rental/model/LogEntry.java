package com.rental.model;

import org.bson.types.ObjectId;
import java.time.LocalDateTime;

public class LogEntry {
    private ObjectId id;
    private LocalDateTime timestamp;
    private String action; // CREATE, UPDATE, DELETE
    private String entityType; // Usuario, Equipo...
    private String entityId;
    private String details;
    private String user; // "System" or logged user if auth is implemented

    public LogEntry() {
        this.timestamp = LocalDateTime.now();
        this.user = "System"; // Default for now
    }

    public LogEntry(String action, String entityType, String entityId, String details) {
        this();
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.details = details;
    }

    // Getters and Setters
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
