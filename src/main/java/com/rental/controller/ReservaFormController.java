package com.rental.controller;

import com.rental.model.Equipo;
import com.rental.model.Reserva;
import com.rental.model.Usuario;
import com.rental.model.enums.EstadoReserva;
import com.rental.service.EquipoService;
import com.rental.service.ReservaService;
import com.rental.service.UsuarioService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ReservaFormController {

    @FXML
    private ComboBox<Usuario> comboCliente;
    @FXML
    private DatePicker dateInicio;
    @FXML
    private DatePicker dateFin;
    @FXML
    private ListView<Equipo> listEquipos;

    private Stage stage;
    private final ReservaService reservaService = new ReservaService();
    private final EquipoService equipoService = new EquipoService();
    private final UsuarioService usuarioService = new UsuarioService();

    @FXML
    public void initialize() {
        comboCliente.setItems(FXCollections.observableArrayList(usuarioService.listarClientes()));
        dateInicio.setValue(LocalDate.now());
        dateFin.setValue(LocalDate.now().plusDays(1));

        listEquipos.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleCheckAvailability() {
        if (dateInicio.getValue() == null || dateFin.getValue() == null) {
            return;
        }

        if (dateFin.getValue().isBefore(dateInicio.getValue())) {
            // warn order
            return;
        }

        // Get all equipment
        List<Equipo> all = equipoService.findAll();
        // Filter those available
        List<Equipo> available = all.stream()
                .filter(e -> reservaService.isDisponible(e.getId(), dateInicio.getValue(), dateFin.getValue()))
                .collect(Collectors.toList());

        listEquipos.setItems(FXCollections.observableArrayList(available));
    }

    @FXML
    private void handleGuardar() {
        if (comboCliente.getValue() == null)
            return;
        List<Equipo> selected = listEquipos.getSelectionModel().getSelectedItems();
        if (selected == null || selected.isEmpty())
            return;

        Reserva r = new Reserva(
                comboCliente.getValue().getId(),
                dateInicio.getValue(),
                dateFin.getValue());
        r.setNombreClienteSnapshot(comboCliente.getValue().getNombre());
        r.setEquipoIds(selected.stream().map(Equipo::getId).collect(Collectors.toList()));
        r.setEstado(EstadoReserva.CONFIRMADA);

        reservaService.create(r);

        stage.close();
    }

    @FXML
    private void handleCancelar() {
        stage.close();
    }
}
