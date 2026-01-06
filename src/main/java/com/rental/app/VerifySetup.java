package com.rental.app;

import com.rental.db.DataSeeder;
import com.rental.db.DatabaseService;

public class VerifySetup {
    public static void main(String[] args) {
        System.out.println("=== VERIFICACION DE DATOS ===");
        try {
            // Check DB Connection
            System.out.println("1. Conectando a MongoDB...");
            DatabaseService.getInstance().getDatabase().listCollectionNames().first();
            System.out.println("   [OK] Conexión exitosa.");

            // Run Seeder
            System.out.println("2. Ejecutando DataSeeder...");
            DataSeeder.seed();
            System.out.println("   [OK] DataSeeder finalizado sin errores críticos.");

        } catch (Exception e) {
            System.err.println("!!! ERROR DETECTADO !!!");
            e.printStackTrace();
        }
        System.out.println("=============================");
    }
}
