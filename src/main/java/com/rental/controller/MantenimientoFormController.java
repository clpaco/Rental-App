package com.rental.controller;

import com.rental.model.Equipo;
import com.rental.model.Mantenimiento;
import com.rental.model.enums.EstadoMantenimiento;
import com.rental.model.enums.TipoMantenimiento;
import com.rental.service.EquipoService;
import com.rental.service.MantenimientoService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class MantenimientoFormController {

    @FXML
    private ComboBox<Equipo> comboEquipo;
    @FXML
    private ComboBox<TipoMantenimiento> comboTipo;
    @FXML
    private ComboBox<EstadoMantenimiento> comboEstado;
    @FXML
    private DatePicker dateInicio;
    @FXML
    private DatePicker dateFin;
    @FXML
    private TextField txtCoste;
    @FXML
    private TextArea txtDescripcion;

    private Stage stage;
    private Mantenimiento mantenimiento;
    private final MantenimientoService service = new MantenimientoService();
    private final EquipoService equipoService = new EquipoService();

    @FXML
    public void initialize() {
        comboEquipo.setItems(FXCollections.observableArrayList(equipoService.findAll()));
        comboTipo.setItems(FXCollections.observableArrayList(TipoMantenimiento.values()));
        comboEstado.setItems(FXCollections.observableArrayList(EstadoMantenimiento.values()));

        dateInicio.setValue(LocalDate.now());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setMantenimiento(Mantenimiento mantenimiento) {
        this.mantenimiento = mantenimiento;
        if (mantenimiento != null) {
            comboEquipo.getItems().stream()
                    .filter(e -> e.getId().equals(mantenimiento.getEquipoId()))
                    .findFirst()
                    .ifPresent(e -> comboEquipo.setValue(e));

            comboTipo.setValue(mantenimiento.getTipo());
            comboEstado.setValue(mantenimiento.getEstado());
            dateInicio.setValue(mantenimiento.getFechaInicio());
            dateFin.setValue(mantenimiento.getFechaFin());
            txtCoste.setText(String.valueOf(mantenimiento.getCoste()));
            txtDescripcion.setText(mantenimiento.getDescripcion());
        } else {
            this.mantenimiento = new Mantenimiento();
            comboEstado.setValue(EstadoMantenimiento.PENDIENTE);
        }
    }

    @FXML
    private void handleGuardar() {
        if (comboEquipo.getValue() == null)
            return;

        mantenimiento.setEquipoId(comboEquipo.getValue().getId());
        mantenimiento.setNombreEquipoSnapshot(comboEquipo.getValue().getNombre());
        mantenimiento.setTipo(comboTipo.getValue());
        mantenimiento.setEstado(comboEstado.getValue());
        mantenimiento.setFechaInicio(dateInicio.getValue());
        mantenimiento.setFechaFin(dateFin.getValue());
        mantenimiento.setDescripcion(txtDescripcion.getText());

        try {
            mantenimiento.setCoste(Double.parseDouble(txtCoste.getText()));
        } catch (Exception e) {
            mantenimiento.setCoste(0.0);
        }

        if (mantenimiento.getId() != null) {
            service.update(mantenimiento.getId(), mantenimiento);
        } else {
            service.create(mantenimiento);
        }

        stage.close();
    }

    @FXML
    private void handleCancelar() {
        stage.close();
    }
}
