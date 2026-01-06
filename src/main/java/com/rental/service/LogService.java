package com.rental.service;

import com.rental.model.LogEntry;
import com.mongodb.client.model.Sorts;
import java.util.ArrayList;
import java.util.List;

public class LogService extends BaseService<LogEntry> {

    private static final LogService INSTANCE = new LogService();

    public LogService() {
        super("logs", LogEntry.class);
    }

    public static LogService getInstance() {
        return INSTANCE;
    }

    public void log(String action, String entityType, String entityId, String details) {
        LogEntry entry = new LogEntry(action, entityType, entityId, details);
        create(entry);
    }

    @Override
    public List<LogEntry> findAll() {
        // Find all sorting by timestamp desc
        if (collection == null)
            return new ArrayList<>(); // Fallback if db not connected
        return collection.find().sort(Sorts.descending("timestamp")).into(new ArrayList<>());
    }
}
