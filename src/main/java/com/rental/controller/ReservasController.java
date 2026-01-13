package com.rental.controller;

import com.rental.app.Main;
import com.rental.model.Reserva;
import com.rental.model.enums.EstadoReserva;
import com.rental.service.ReservaService;
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

public class ReservasController {

    @FXML
    private DatePicker filterStart;
    @FXML
    private DatePicker filterEnd;
    @FXML
    private TableView<Reserva> reservasTable;
    @FXML
    private TableColumn<Reserva, String> colCliente;
    @FXML
    private TableColumn<Reserva, LocalDate> colInicio;
    @FXML
    private TableColumn<Reserva, LocalDate> colFin;
    @FXML
    private TableColumn<Reserva, EstadoReserva> colEstado;
    @FXML
    private TableColumn<Reserva, String> colEquipos;

    private final ReservaService reservaService = new ReservaService();
    private ObservableList<Reserva> masterData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Defaults: This Month
        filterStart.setValue(LocalDate.now().withDayOfMonth(1));
        filterEnd.setValue(LocalDate.now().plusMonths(1).withDayOfMonth(1).minusDays(1));

        colCliente.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNombreClienteSnapshot()));
        colInicio.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getFechaInicio()));
        colFin.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getFechaFin()));
        colEstado.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getEstado()));
        colEquipos.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getEquipoIds().size() + " Equipos"));

        loadData();
    }

    private void loadData() {
        masterData.clear();
        masterData.addAll(reservaService.listarPorFecha(filterStart.getValue(), filterEnd.getValue()));
        reservasTable.setItems(masterData);
    }

    @FXML
    private void handleFiltrar() {
        loadData();
    }

    @FXML
    private void handleNuevaReserva() {
        showForm(null);
    }

    @FXML
    private void handleEditarReserva() {
        Reserva selected = reservasTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Seleccione una reserva para editar.", Alert.AlertType.WARNING);
            return;
        }

        if (selected.getEstado() == EstadoReserva.CANCELADA) {
            showAlert("No se pueden editar reservas canceladas.", Alert.AlertType.WARNING);
            return;
        }

        showForm(selected);
    }

    @FXML
    private void handleCancelarReserva() {
        Reserva selected = reservasTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Cancelar Reserva");
            alert.setHeaderText("¿Seguro que desea cancelar la reserva?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                selected.setEstado(EstadoReserva.CANCELADA);
                reservaService.update(selected.getId(), selected);
                loadData();
            }
        } else {
            showAlert("Seleccione una reserva.", Alert.AlertType.WARNING);
        }
    }

    private void showForm(Reserva reservaToEdit) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/fxml/reserva_form.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Nueva Reserva");
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.setScene(new Scene(loader.load()));

            ReservaFormController controller = loader.getController();
            controller.setStage(stage);

            if (reservaToEdit != null) {
                stage.setTitle("Editar Reserva");
                controller.setReserva(reservaToEdit);
            } else {
                stage.setTitle("Nueva Reserva");
            }

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
