package com.rental.controller;

import com.rental.app.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MainLayoutController {

    private static final Logger logger = LoggerFactory.getLogger(MainLayoutController.class);
    private static MainLayoutController instance;

    @FXML
    private BorderPane mainContainer;

    @FXML
    public void initialize() {
        instance = this;
        // Load default view (Dashboard)
        showDashboard();
    }

    public static void navigateTo(String fxml) {
        if (instance != null) {
            instance.setView(fxml);
        } else {
            logger.error("MainLayoutController instance is null");
        }
    }

    public void setView(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/fxml/" + fxml + ".fxml"));
            Parent view = loader.load();
            mainContainer.setCenter(view);
        } catch (IOException e) {
            logger.error("Error cargando vista: " + fxml, e);
        }
    }

    @FXML
    public void showDashboard() {
        setView("dashboard");
    }

    @FXML
    public void showEquipos() {
        setView("equipos");
    }

    @FXML
    public void showUsuarios() {
        setView("usuarios");
    }

    @FXML
    public void showReservas() {
        setView("reservas_calendario");
    }

    @FXML
    public void showFacturas() {
        setView("facturas");
    }

    @FXML
    public void showGarantias() {
        setView("garantias");
    }

    @FXML
    public void showMantenimientos() {
        setView("mantenimientos");
    }

    @FXML
    public void showLogs() {
        setView("logs");
    }
}
