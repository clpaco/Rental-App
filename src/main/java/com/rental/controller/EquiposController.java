package com.rental.controller;

import com.rental.app.Main;
import com.rental.model.Equipo;
import com.rental.model.enums.EstadoEquipo;
import com.rental.service.EquipoService;
import javafx.beans.property.SimpleDoubleProperty;
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
import java.util.Optional;

public class EquiposController {

    @FXML
    private TextField searchField;
    @FXML
    private TableView<Equipo> equiposTable;
    @FXML
    private TableColumn<Equipo, String> colCodigo;
    @FXML
    private TableColumn<Equipo, String> colNombre;
    @FXML
    private TableColumn<Equipo, String> colCategoria;
    @FXML
    private TableColumn<Equipo, EstadoEquipo> colEstado;
    @FXML
    private TableColumn<Equipo, Double> colTarifa;

    private final EquipoService equipoService = new EquipoService();
    private ObservableList<Equipo> masterData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Setup columns
        colCodigo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCodigo()));
        colNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        colCategoria.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategoria()));
        colEstado.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getEstado()));
        colTarifa.setCellValueFactory(
                cellData -> new SimpleDoubleProperty(cellData.getValue().getTarifaDiaria()).asObject());

        loadData();
    }

    private void loadData() {
        masterData.clear();
        masterData.addAll(equipoService.findAll());
        equiposTable.setItems(masterData);
    }

    @FXML
    private void handleBuscar() {
        String query = searchField.getText().toLowerCase();
        if (query.isEmpty()) {
            equiposTable.setItems(masterData);
        } else {
            ObservableList<Equipo> filtered = masterData
                    .filtered(e -> (e.getNombre() != null && e.getNombre().toLowerCase().contains(query)) ||
                            (e.getCodigo() != null && e.getCodigo().toLowerCase().contains(query)));
            equiposTable.setItems(filtered);
        }
    }

    @FXML
    private void handleNuevoEquipo() {
        showForm(null);
    }

    @FXML
    private void handleEditar() {
        Equipo selected = equiposTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showForm(selected);
        } else {
            showAlert("Seleccione un equipo para editar.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleEliminar() {
        Equipo selected = equiposTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar eliminación");
            alert.setHeaderText("¿Seguro que desea eliminar: " + selected.getNombre() + "?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                equipoService.delete(selected.getId());
                loadData();
            }
        } else {
            showAlert("Seleccione un equipo para eliminar.", Alert.AlertType.WARNING);
        }
    }

    private void showForm(Equipo equipo) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/fxml/equipo_form.fxml"));
            Stage stage = new Stage();
            stage.setTitle(equipo == null ? "Nuevo Equipo" : "Editar Equipo");
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.setScene(new Scene(loader.load()));

            EquipoFormController controller = loader.getController();
            controller.setEquipo(equipo); // Pass data if editing
            controller.setStage(stage);

            stage.showAndWait(); // Wait for close

            loadData(); // Refresh table

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
