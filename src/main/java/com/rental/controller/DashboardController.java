package com.rental.controller;

import com.rental.model.enums.EstadoEquipo;
import com.rental.service.DashboardService;
import com.rental.db.DatabaseService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

public class DashboardController {

    @FXML
    private Label statusLabel;
    @FXML
    private Label lblActiveRentals;
    @FXML
    private Label lblTotalClients;

    @FXML
    private PieChart pieChartStatus;
    @FXML
    private BarChart<String, Number> barChartRevenue;

    private final DashboardService dashboardService = new DashboardService();

    @FXML
    private javafx.scene.control.ComboBox<Integer> comboYear;

    @FXML
    public void initialize() {
        // Check DB connection
        if (DatabaseService.getInstance().getDatabase() != null) {
            statusLabel.setText("Conectado a MongoDB: rental_db");
        } else {
            statusLabel.setText("Error de conexión a MongoDB");
        }

        // Setup Combo
        comboYear.getItems().addAll(2025, 2026);
        comboYear.setValue(LocalDate.now().getYear());
        comboYear.setOnAction(e -> loadCharts());

        refreshDashboard();
    }

    @FXML
    public void refreshDashboard() {
        loadStats();
        loadCharts();
    }

    private void loadStats() {
        Platform.runLater(() -> {
            lblActiveRentals.setText(String.valueOf(dashboardService.countActiveRentals()));
            lblTotalClients.setText(String.valueOf(dashboardService.countClients()));
        });
    }

    private void loadCharts() {
        Platform.runLater(() -> {
            // PIE CHART
            pieChartStatus.getData().clear();
            Map<EstadoEquipo, Integer> equiposStats = dashboardService.getEquiposPorEstado();
            for (Map.Entry<EstadoEquipo, Integer> entry : equiposStats.entrySet()) {
                pieChartStatus.getData().add(new PieChart.Data(entry.getKey().name(), entry.getValue()));
            }

            // BAR CHART
            barChartRevenue.getData().clear();
            int year = comboYear.getValue() != null ? comboYear.getValue() : LocalDate.now().getYear();

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Ingresos " + year);

            Map<String, Double> revenueStats = dashboardService.getIngresosPorMes(year);

            for (java.time.Month m : java.time.Month.values()) {
                String mName = m.name();
                Double val = revenueStats.getOrDefault(mName, 0.0);
                series.getData().add(new XYChart.Data<>(mName.substring(0, 3), val));
            }

            barChartRevenue.getData().add(series);
        });
    }

    // Navigation methods now delegate to MainLayoutController to keep the sidebar
    @FXML
    public void goToEquipos() throws IOException {
        MainLayoutController.navigateTo("equipos");
    }

    @FXML
    public void goToUsuarios() throws IOException {
        MainLayoutController.navigateTo("usuarios");
    }

    @FXML
    public void goToFacturas() throws IOException {
        MainLayoutController.navigateTo("facturas");
    }

    @FXML
    public void goToReservas() throws IOException {
        MainLayoutController.navigateTo("reservas_calendario");
    }

    @FXML
    public void goToGarantias() throws IOException {
        MainLayoutController.navigateTo("garantias");
    }

    @FXML
    public void goToMantenimientos() throws IOException {
        MainLayoutController.navigateTo("mantenimientos");
    }

    @FXML
    public void goToLogs() throws IOException {
        MainLayoutController.navigateTo("logs");
    }

    @FXML
    private void handleSyncOdoo() {
        com.rental.service.OdooSyncService odoo = new com.rental.service.OdooSyncService();
        statusLabel.setText("Sincronizando con Odoo...");

        new Thread(() -> {
            try {
                Thread.sleep(1500);
                Platform.runLater(() -> {
                    com.rental.model.Equipo mock = new com.rental.model.Equipo();
                    mock.setCodigo("SYNC-TEST");
                    mock.setNombre("Mock Product");
                    mock.setCategoria("Test");
                    mock.setValorCompra(0.0);
                    mock.setTarifaDiaria(0.0);
                    mock.setEstado(EstadoEquipo.DISPONIBLE);

                    odoo.syncProduct(mock);
                    statusLabel.setText("Sincronización Odoo Completada: " + LocalDate.now());
                });
            } catch (InterruptedException e) {
            }
        }).start();
    }
}
