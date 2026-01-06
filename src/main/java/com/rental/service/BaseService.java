package com.rental.service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.rental.db.DatabaseService;
import org.bson.types.ObjectId;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseService<T> {

    protected final MongoCollection<T> collection;

    public BaseService(String collectionName, Class<T> clazz) {
        MongoDatabase database = DatabaseService.getInstance().getDatabase();
        this.collection = database.getCollection(collectionName, clazz);
    }

    public void create(T entity) {
        collection.insertOne(entity);
    }

    public T findById(ObjectId id) {
        return collection.find(Filters.eq("_id", id)).first();
    }

    public List<T> findAll() {
        return collection.find().into(new ArrayList<>());
    }

    public void update(ObjectId id, T entity) {
        // This is a naive full replace. Ideally use Updates.set for specific fields or
        // replaceOne
        collection.replaceOne(Filters.eq("_id", id), entity);
    }

    public void delete(ObjectId id) {
        collection.deleteOne(Filters.eq("_id", id));
    }

    public List<T> findByFilter(Bson filter) {
        return collection.find(filter).into(new ArrayList<>());
    }
}
