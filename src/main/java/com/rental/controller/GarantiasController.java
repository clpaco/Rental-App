package com.rental.controller;

import com.rental.app.Main;
import com.rental.model.Garantia;
import com.rental.service.GarantiaService;
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
import java.util.Optional;

public class GarantiasController {

    @FXML
    private TextField searchField;
    @FXML
    private TableView<Garantia> garantiasTable;
    @FXML
    private TableColumn<Garantia, String> colEquipo;
    @FXML
    private TableColumn<Garantia, String> colProveedor;
    @FXML
    private TableColumn<Garantia, LocalDate> colFin;
    @FXML
    private TableColumn<Garantia, String> colEstado;
    @FXML
    private TableColumn<Garantia, String> colContacto;

    private final GarantiaService garantiaService = new GarantiaService();
    private ObservableList<Garantia> masterData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colEquipo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNombreEquipoSnapshot()));
        colProveedor.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getProveedor()));
        colFin.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getFechaFin()));
        colContacto.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getContactoSoporte()));

        colEstado.setCellValueFactory(cell -> {
            boolean active = cell.getValue().isActiva();
            return new SimpleStringProperty(active ? "ACTIVA" : "VENCIDA");
        });

        // Simple Color coding
        colEstado.setCellFactory(column -> new TableCell<Garantia, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if ("ACTIVA".equals(item)) {
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    }
                }
            }
        });

        loadData();
    }

    private void loadData() {
        masterData.clear();
        masterData.addAll(garantiaService.findAll());
        garantiasTable.setItems(masterData);
    }

    @FXML
    private void handleBuscar() {
        String query = searchField.getText().toLowerCase();
        if (query.isEmpty()) {
            garantiasTable.setItems(masterData);
        } else {
            ObservableList<Garantia> filtered = masterData.filtered(g -> (g.getNombreEquipoSnapshot() != null
                    && g.getNombreEquipoSnapshot().toLowerCase().contains(query)) ||
                    (g.getProveedor() != null && g.getProveedor().toLowerCase().contains(query)));
            garantiasTable.setItems(filtered);
        }
    }

    @FXML
    private void handleNuevaGarantia() {
        showForm(null);
    }

    @FXML
    private void handleEditar() {
        Garantia selected = garantiasTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showForm(selected);
        } else {
            showAlert("Seleccione una garantía.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleEliminar() {
        Garantia selected = garantiasTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar eliminación");
            alert.setHeaderText("¿Seguro que desea eliminar esta garantía?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                garantiaService.delete(selected.getId());
                loadData();
            }
        } else {
            showAlert("Seleccione una garantía.", Alert.AlertType.WARNING);
        }
    }

    private void showForm(Garantia garantia) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/fxml/garantia_form.fxml"));
            Stage stage = new Stage();
            stage.setTitle(garantia == null ? "Registrar Garantía" : "Editar Garantía");
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.setScene(new Scene(loader.load()));

            GarantiaFormController controller = loader.getController();
            controller.setGarantia(garantia);
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
