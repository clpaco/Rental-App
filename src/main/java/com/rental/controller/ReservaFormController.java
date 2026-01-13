package com.rental.controller;

import com.rental.model.Equipo;
import com.rental.model.Reserva;
import com.rental.model.Usuario;
import com.rental.model.enums.EstadoReserva;
import com.rental.service.EquipoService;
import com.rental.service.ReservaService;
import com.rental.service.UsuarioService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.util.ArrayList;
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
    private ListView<Equipo> listAvailable;
    @FXML
    private ListView<Equipo> listSelected;

    private Stage stage;
    private final ReservaService reservaService = new ReservaService();
    private final EquipoService equipoService = new EquipoService();
    private final UsuarioService usuarioService = new UsuarioService();

    private Reserva editingReserva; // For Edit Mode

    @FXML
    public void initialize() {
        comboCliente.setItems(FXCollections.observableArrayList(usuarioService.listarClientes()));
        dateInicio.setValue(LocalDate.now());
        dateFin.setValue(LocalDate.now().plusDays(1));

        listAvailable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listSelected.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    // --- Edit Mode Support ---
    public void setReserva(Reserva reserva) {
        this.editingReserva = reserva;

        // 1. Select Client
        for (Usuario u : comboCliente.getItems()) {
            if (u.getId().equals(reserva.getClienteId())) {
                comboCliente.setValue(u);
                break;
            }
        }

        // 2. Set Dates
        dateInicio.setValue(reserva.getFechaInicio());
        dateFin.setValue(reserva.getFechaFin());

        // 3. Load Selected Items directly
        List<Equipo> selectedItems = new ArrayList<>();
        for (ObjectId eqId : reserva.getEquipoIds()) {
            Equipo eq = equipoService.findById(eqId);
            if (eq != null)
                selectedItems.add(eq);
        }
        listSelected.setItems(FXCollections.observableArrayList(selectedItems));

        // 4. Ideally, also trigger availability check to populate left side,
        // preventing duplicate selection of already selected items.
        handleCheckAvailability();
    }

    @FXML
    private void handleCheckAvailability() {
        if (dateInicio.getValue() == null || dateFin.getValue() == null) {
            showAlert("Seleccione fechas primero.", Alert.AlertType.WARNING);
            return;
        }

        if (dateFin.getValue().isBefore(dateInicio.getValue())) {
            showAlert("La fecha fin no puede ser anterior a la inicio.", Alert.AlertType.WARNING);
            return;
        }

        // Get all equipment
        List<Equipo> all = equipoService.findAll();

        // Get currently selected IDs to avoid showing them in available
        List<ObjectId> currentSelectedIds = new ArrayList<>();
        if (listSelected.getItems() != null) {
            currentSelectedIds = listSelected.getItems().stream().map(Equipo::getId).collect(Collectors.toList());
        }

        List<ObjectId> finalCurrentSelectedIds = currentSelectedIds;
        List<Equipo> available = all.stream()
                .filter(e -> !finalCurrentSelectedIds.contains(e.getId())) // Remove already selected
                .filter(e -> {
                    // Special case for Edit: If collision is with THIS reservation, it's allowed.
                    // But isDisponible implementation creates collision if reservation exists.
                    // We need 'isDisponibleExcluding(eqId, start, end, excludedReservaId)'
                    // For now, simpler: isDisponible logic checks ALL reservations.
                    // Ideally pass editingReserva.getId() to exclude from check.
                    if (editingReserva != null) {
                        return reservaService.isDisponibleExcluding(e.getId(), dateInicio.getValue(),
                                dateFin.getValue(), editingReserva.getId());
                    }
                    return reservaService.isDisponible(e.getId(), dateInicio.getValue(), dateFin.getValue());
                })
                .collect(Collectors.toList());

        listAvailable.setItems(FXCollections.observableArrayList(available));
    }

    @FXML
    private void handleVerOcupacion() {
        Equipo selected = listAvailable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            // Try selected list
            selected = listSelected.getSelectionModel().getSelectedItem();
        }

        if (selected == null) {
            showAlert("Seleccione un equipo para ver su calendario.", Alert.AlertType.WARNING);
            return;
        }

        // Fetch future reservations for this item
        List<Reserva> reservas = reservaService.getFuturasPorEquipo(selected.getId());

        StringBuilder sb = new StringBuilder("Reservas Futuras para: " + selected.getNombre() + "\n\n");
        if (reservas.isEmpty()) {
            sb.append("Este equipo está totalmente libre en el futuro.");
        } else {
            for (Reserva r : reservas) {
                sb.append("📅 ").append(r.getFechaInicio()).append(" -> ").append(r.getFechaFin())
                        .append(" (").append(r.getEstado()).append(")\n");
            }
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Disponibilidad: " + selected.getNombre());
        alert.setHeaderText("Calendario de Ocupación");
        alert.setContentText(sb.toString());
        alert.showAndWait();
    }

    @FXML
    private void handleAgregar() {
        List<Equipo> items = listAvailable.getSelectionModel().getSelectedItems();
        if (items.isEmpty())
            return;

        // Move to Selected
        ObservableList<Equipo> selectedList = listSelected.getItems();
        if (selectedList == null)
            selectedList = FXCollections.observableArrayList();

        selectedList.addAll(items);
        listSelected.setItems(selectedList); // Refresh

        // Remove from Available
        listAvailable.getItems().removeAll(items);
    }

    @FXML
    private void handleQuitar() {
        List<Equipo> items = listSelected.getSelectionModel().getSelectedItems();
        if (items.isEmpty())
            return;

        // Move back to Available (logic implies re-checking availability, or just
        // adding back blindly)
        // Safer to just add back to listAvailable
        ObservableList<Equipo> availList = listAvailable.getItems();
        if (availList == null)
            availList = FXCollections.observableArrayList();

        availList.addAll(items);

        // Remove from Selected
        listSelected.getItems().removeAll(items);
    }

    @FXML
    private void handleGuardar() {
        if (comboCliente.getValue() == null) {
            showAlert("Seleccione un cliente.", Alert.AlertType.WARNING);
            return;
        }

        if (listSelected.getItems() == null || listSelected.getItems().isEmpty()) {
            showAlert("Seleccione al menos un equipo.", Alert.AlertType.WARNING);
            return;
        }

        Reserva r = (editingReserva != null) ? editingReserva : new Reserva();

        r.asignarCliente(comboCliente.getValue());
        r.setFechaInicio(dateInicio.getValue());
        r.setFechaFin(dateFin.getValue());
        r.setEquipoIds(listSelected.getItems().stream().map(Equipo::getId).collect(Collectors.toList()));

        if (editingReserva == null) {
            r.setEstado(EstadoReserva.CONFIRMADA);
            reservaService.create(r);
        } else {
            reservaService.update(r.getId(), r);
        }

        stage.close();
    }

    @FXML
    private void handleCancelar() {
        stage.close();
    }

    private void showAlert(String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setContentText(msg);
        alert.show();
    }
}
