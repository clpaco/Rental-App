package com.rental.controller;

import com.rental.model.Equipo;
import com.rental.model.enums.EstadoEquipo;
import com.rental.service.EquipoService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EquipoFormController {

    @FXML
    private TextField txtCodigo;
    @FXML
    private TextField txtNombre;
    @FXML
    private ComboBox<String> comboCategoria;
    @FXML
    private ComboBox<EstadoEquipo> comboEstado;
    @FXML
    private TextField txtValorCompra;
    @FXML
    private TextField txtTarifa;
    @FXML
    private TextField txtFotoUrl;
    @FXML
    private TextArea txtDescripcion;

    private Stage stage;
    private Equipo equipo;
    private final EquipoService equipoService = new EquipoService();
    private boolean isEditMode = false;

    @FXML
    public void initialize() {
        comboEstado.setItems(FXCollections.observableArrayList(EstadoEquipo.values()));
        // Default categories for now
        comboCategoria.setItems(FXCollections.observableArrayList("Cámara", "Lente", "Iluminación", "Audio", "Drone",
                "Estabilizador", "Otro"));
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
        if (equipo != null) {
            isEditMode = true;
            // Populate fields
            txtCodigo.setText(equipo.getCodigo());
            txtNombre.setText(equipo.getNombre());
            comboCategoria.setValue(equipo.getCategoria());
            comboEstado.setValue(equipo.getEstado());
            txtValorCompra.setText(String.valueOf(equipo.getValorCompra()));
            txtTarifa.setText(String.valueOf(equipo.getTarifaDiaria()));
            txtFotoUrl.setText(equipo.getFotoUrl());
            txtDescripcion.setText(equipo.getDescripcion());
        } else {
            // Defaults for new
            this.equipo = new Equipo();
            comboEstado.setValue(EstadoEquipo.DISPONIBLE);
        }
    }

    @FXML
    private void handleGuardar() {
        if (!validate())
            return;

        // Populate object
        equipo.setCodigo(txtCodigo.getText());
        equipo.setNombre(txtNombre.getText());
        equipo.setCategoria(comboCategoria.getValue());
        equipo.setEstado(comboEstado.getValue());
        equipo.setDescripcion(txtDescripcion.getText());
        equipo.setFotoUrl(txtFotoUrl.getText());

        try {
            equipo.setValorCompra(Double.parseDouble(txtValorCompra.getText()));
            equipo.setTarifaDiaria(Double.parseDouble(txtTarifa.getText()));
        } catch (NumberFormatException e) {
            // Already checked in validate, but safety
            return;
        }

        if (isEditMode) {
            equipoService.update(equipo.getId(), equipo);
        } else {
            equipoService.create(equipo);
        }

        stage.close();
    }

    @FXML
    private void handleCancelar() {
        stage.close();
    }

    private boolean validate() {
        if (txtCodigo.getText().isEmpty() || txtNombre.getText().isEmpty() || comboCategoria.getValue() == null) {
            // Simple validation visualization could be red borders, but standard alert is
            // fine
            System.out.println("Validation failed: Required fields missing");
            return false;
        }
        try {
            Double.parseDouble(txtValorCompra.getText());
            Double.parseDouble(txtTarifa.getText());
        } catch (NumberFormatException e) {
            System.out.println("Validation failed: Numeric fields invalid");
            return false;
        }
        return true;
    }
}
