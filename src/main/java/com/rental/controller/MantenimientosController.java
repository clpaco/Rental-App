package com.rental.controller;

import com.rental.app.Main;
import com.rental.model.Mantenimiento;
import com.rental.model.enums.EstadoMantenimiento;
import com.rental.model.enums.TipoMantenimiento;
import com.rental.service.MantenimientoService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

public class MantenimientosController {

    @FXML
    private TableView<Mantenimiento> table;
    @FXML
    private TableColumn<Mantenimiento, String> colEquipo;
    @FXML
    private TableColumn<Mantenimiento, TipoMantenimiento> colTipo;
    @FXML
    private TableColumn<Mantenimiento, EstadoMantenimiento> colEstado;
    @FXML
    private TableColumn<Mantenimiento, LocalDate> colInicio;
    @FXML
    private TableColumn<Mantenimiento, LocalDate> colFin;
    @FXML
    private TableColumn<Mantenimiento, Double> colCoste;

    private final MantenimientoService service = new MantenimientoService();
    private ObservableList<Mantenimiento> masterData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colEquipo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNombreEquipoSnapshot()));
        colTipo.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getTipo()));
        colEstado.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getEstado()));
        colInicio.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getFechaInicio()));
        colFin.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getFechaFin()));
        colCoste.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getCoste()));

        loadData();
    }

    private void loadData() {
        masterData.clear();
        masterData.addAll(service.findAll());
        table.setItems(masterData);
    }

    @FXML
    private void handleRecargar() {
        loadData();
    }

    @FXML
    private void handleNuevo() {
        showForm(null);
    }

    @FXML
    private void handleEditar() {
        Mantenimiento selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showForm(selected);
        } else {
            showAlert("Seleccione un registro.", Alert.AlertType.WARNING);
        }
    }

    private void showForm(Mantenimiento item) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/fxml/mantenimiento_form.fxml"));
            Stage stage = new Stage();
            stage.setTitle(item == null ? "Registrar Mantenimiento" : "Editar Mantenimiento");
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.setScene(new Scene(loader.load()));

            MantenimientoFormController controller = loader.getController();
            controller.setMantenimiento(item);
            controller.setStage(stage);

            stage.showAndWait();

            loadData();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error al abrir formulario: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setContentText(msg);
        alert.show();
    }
}
