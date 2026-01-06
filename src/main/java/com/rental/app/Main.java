package com.rental.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.rental.db.DatabaseService;

import java.io.IOException;

public class Main extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        // Initialize Database and Seed Data
        try {
            DatabaseService.getInstance().getDatabase().listCollectionNames().first();
            System.out.println("Conexion exitosa a MongoDB.");

            // Seed Data
            System.out.println(">>> INTENTANDO SEMBRAR DATOS...");
            try {
                com.rental.db.DataSeeder.seed();
                System.out.println(">>> DATA SEEDER FINALIZADO CORRECTAMENTE.");
            } catch (Throwable t) {
                System.err.println(">>> ERROR CRITICO EN DATA SEEDER:");
                t.printStackTrace();
            }

        } catch (Exception e) {
            System.err.println("Error conectando a MongoDB: " + e.getMessage());
        }

        // Load Login Screen first
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/fxml/login.fxml"));
            Parent root = fxmlLoader.load();
            scene = new Scene(root, 400, 500); // Smaller size for login

            // Add stylesheet
            scene.getStylesheets().add(Main.class.getResource("/css/style.css").toExternalForm());

            stage.setTitle("Rental Equipment Manager - Odoo Integrated");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading main layout: " + e.getMessage());
        }
    }

    public static void setRoot(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/fxml/" + fxml + ".fxml"));
        scene.setRoot(fxmlLoader.load());
    }

    public static void main(String[] args) {
        launch();
    }
}
