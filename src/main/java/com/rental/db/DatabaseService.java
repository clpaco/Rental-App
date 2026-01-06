package com.rental.db;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class DatabaseService {

    private static DatabaseService instance;
    private final MongoClient mongoClient;
    private final MongoDatabase database;

    private DatabaseService() {
        // Fallback or Environment Variable if needed manually
        String connectionString = "mongodb://request_admin:request_password@localhost:27018";
        String dbName = "rental_db";

        // Configure CodecRegistry for POJOs
        CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
        CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);

        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .codecRegistry(codecRegistry)
                .build();

        this.mongoClient = MongoClients.create(clientSettings);
        this.database = mongoClient.getDatabase(dbName);
    }

    public static synchronized DatabaseService getInstance() {
        if (instance == null) {
            instance = new DatabaseService();
        }
        return instance;
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public MongoClient getClient() {
        return mongoClient;
    }
}
