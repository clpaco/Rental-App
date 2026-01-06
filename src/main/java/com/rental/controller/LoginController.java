package com.rental.controller;

import com.rental.app.Main;
import com.rental.service.OdooSyncService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField txtUser;

    @FXML
    private PasswordField txtPass;

    @FXML
    private Label lblStatus;

    @FXML
    private Button btnLogin;

    private final OdooSyncService odooService = OdooSyncService.getInstance();

    @FXML
    public void initialize() {
        // Optional: Focus user field
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String user = txtUser.getText();
        String pass = txtPass.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            lblStatus.setText("Por favor, ingrese usuario y contraseña.");
            return;
        }

        btnLogin.setDisable(true);
        lblStatus.setText("Conectando con Odoo...");

        new Thread(() -> {
            boolean success = odooService.login(user, pass);

            javafx.application.Platform.runLater(() -> {
                btnLogin.setDisable(false);
                if (success) {
                    try {
                        // Capture stage before switching root (safest) or just get it from the scene
                        Stage stage = (Stage) btnLogin.getScene().getWindow();
                        Main.setRoot("main_layout");
                        stage.setMaximized(true);
                    } catch (IOException e) {
                        lblStatus.setText("Error cargando la aplicación: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    lblStatus.setText("Login fallido. Verifique credenciales o conexión a Odoo.");
                }
            });
        }).start();
    }
}
