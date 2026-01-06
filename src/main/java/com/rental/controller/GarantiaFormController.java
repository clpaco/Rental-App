package com.rental.controller;

import com.rental.model.Equipo;
import com.rental.model.Garantia;
import com.rental.service.EquipoService;
import com.rental.service.GarantiaService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class GarantiaFormController {

    @FXML
    private ComboBox<Equipo> comboEquipo;
    @FXML
    private TextField txtProveedor;
    @FXML
    private TextField txtSerie;
    @FXML
    private DatePicker dateCompra;
    @FXML
    private DatePicker dateFin;
    @FXML
    private TextField txtContacto;
    @FXML
    private TextArea txtCobertura;

    private Stage stage;
    private Garantia garantia;
    private final GarantiaService garantiaService = new GarantiaService();
    private final EquipoService equipoService = new EquipoService();

    @FXML
    public void initialize() {
        comboEquipo.setItems(FXCollections.observableArrayList(equipoService.findAll()));
        dateCompra.setValue(LocalDate.now());
        dateFin.setValue(LocalDate.now().plusYears(1));
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setGarantia(Garantia garantia) {
        this.garantia = garantia;
        if (garantia != null) {
            // Find equipment in combo
            comboEquipo.getItems().stream()
                    .filter(e -> e.getId().equals(garantia.getEquipoId()))
                    .findFirst()
                    .ifPresent(e -> comboEquipo.setValue(e));

            txtProveedor.setText(garantia.getProveedor());
            txtSerie.setText(garantia.getNumeroSerie());
            dateCompra.setValue(garantia.getFechaCompra());
            dateFin.setValue(garantia.getFechaFin());
            txtContacto.setText(garantia.getContactoSoporte());
            txtCobertura.setText(garantia.getCobertura());
        } else {
            this.garantia = new Garantia();
        }
    }

    @FXML
    private void handleGuardar() {
        if (comboEquipo.getValue() == null)
            return;

        garantia.setEquipoId(comboEquipo.getValue().getId());
        garantia.setNombreEquipoSnapshot(comboEquipo.getValue().getNombre());
        garantia.setProveedor(txtProveedor.getText());
        garantia.setNumeroSerie(txtSerie.getText());
        garantia.setFechaCompra(dateCompra.getValue());
        garantia.setFechaFin(dateFin.getValue());
        garantia.setContactoSoporte(txtContacto.getText());
        garantia.setCobertura(txtCobertura.getText());

        if (garantia.getId() != null) {
            garantiaService.update(garantia.getId(), garantia);
        } else {
            garantiaService.create(garantia);
        }

        stage.close();
    }

    @FXML
    private void handleCancelar() {
        stage.close();
    }
}
