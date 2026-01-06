package com.rental.controller;

import com.rental.app.Main;
import com.rental.model.Factura;
import com.rental.model.enums.EstadoFactura;
import com.rental.service.FacturaService;
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

public class FacturasController {

    @FXML
    private TextField searchField;
    @FXML
    private TableView<Factura> facturasTable;
    @FXML
    private TableColumn<Factura, String> colNumero;
    @FXML
    private TableColumn<Factura, String> colFecha;
    @FXML
    private TableColumn<Factura, String> colCliente;
    @FXML
    private TableColumn<Factura, EstadoFactura> colEstado;
    @FXML
    private TableColumn<Factura, String> colTotal; // String to format currency

    private final FacturaService facturaService = new FacturaService();
    private ObservableList<Factura> masterData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colNumero.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNumeroFactura()));
        colFecha.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFecha().toString()));
        colCliente.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getNombreClienteSnapshot()));
        colEstado.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getEstado()));
        colEstado.setCellFactory(column -> new TableCell<Factura, EstadoFactura>() {
            @Override
            protected void updateItem(EstadoFactura item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label label = new Label(item.toString());
                    label.getStyleClass().add("status-pill");
                    switch (item) {
                        case PAGADA:
                            label.getStyleClass().add("status-pagada");
                            break;
                        case PENDIENTE:
                            label.getStyleClass().add("status-pendiente");
                            break;
                        case ANULADA:
                        case CANCELADA:
                            label.getStyleClass().add("status-anulada");
                            break;
                        default:
                            label.getStyleClass().add("status-cotizacion");
                    }
                    setGraphic(label);
                    setText(null);
                }
            }
        });
        colTotal.setCellValueFactory(
                cellData -> new SimpleStringProperty(String.format("%.2f €", cellData.getValue().getTotal())));

        loadData();
    }

    private void loadData() {
        masterData.clear();
        masterData.addAll(facturaService.findAll());
        facturasTable.setItems(masterData);
    }

    @FXML
    private void handleBuscar() {
        String query = searchField.getText().toLowerCase();
        if (query.isEmpty()) {
            facturasTable.setItems(masterData);
        } else {
            ObservableList<Factura> filtered = masterData.filtered(
                    f -> (f.getNumeroFactura() != null && f.getNumeroFactura().toLowerCase().contains(query)) ||
                            (f.getNombreClienteSnapshot() != null
                                    && f.getNombreClienteSnapshot().toLowerCase().contains(query)));
            facturasTable.setItems(filtered);
        }
    }

    @FXML
    private void handleNuevaFactura() {
        showForm(null);
    }

    @FXML
    private void handleVerDetalle() {
        Factura selected = facturasTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showForm(selected);
        } else {
            showAlert("Seleccione una factura.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleExportarPdf() {
        Factura selected = facturasTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Seleccione una factura para exportar.", Alert.AlertType.WARNING);
            return;
        }

        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Guardar Factura PDF");
        fileChooser.setInitialFileName(
                "Factura_" + (selected.getNumeroFactura() != null ? selected.getNumeroFactura() : "Borrador") + ".pdf");
        fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

        java.io.File file = fileChooser.showSaveDialog(facturasTable.getScene().getWindow());

        if (file != null) {
            try {
                new com.rental.service.PdfExportService().exportarFactura(selected, file);
                showAlert("Factura exportada correctamente.", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error al exportar PDF: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void showForm(Factura factura) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/fxml/factura_form.fxml"));
            Stage stage = new Stage();
            stage.setTitle(factura == null ? "Nueva Factura" : "Editar Factura");
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.setScene(new Scene(loader.load()));

            FacturaFormController controller = loader.getController();
            controller.setFactura(factura);
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
