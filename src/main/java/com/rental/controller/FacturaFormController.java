package com.rental.controller;

import com.rental.model.Equipo;
import com.rental.model.Factura;
import com.rental.model.LineaFactura;
import com.rental.model.Usuario;
import com.rental.model.enums.EstadoFactura;
import com.rental.model.enums.EstadoEquipo;
import com.rental.service.EquipoService;
import com.rental.service.FacturaService;
import com.rental.service.UsuarioService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.DoubleStringConverter;

import java.util.stream.Collectors;

public class FacturaFormController {

    @FXML
    private TextField txtNumero;
    @FXML
    private DatePicker dateFecha;
    @FXML
    private ComboBox<Usuario> comboCliente;
    @FXML
    private ComboBox<EstadoFactura> comboEstado;

    @FXML
    private ComboBox<Equipo> comboEquipo;
    @FXML
    private TableView<LineaFactura> itemsTable;
    @FXML
    private TableColumn<LineaFactura, String> colItemConcepto;
    @FXML
    private TableColumn<LineaFactura, Double> colItemPrecio;
    @FXML
    private TableColumn<LineaFactura, Integer> colItemCantidad;
    @FXML
    private TableColumn<LineaFactura, Integer> colItemDias;
    @FXML
    private TableColumn<LineaFactura, Double> colItemTotal;

    @FXML
    private Label lblSubtotal;
    @FXML
    private Label lblIva;
    @FXML
    private Label lblTotal;

    private Stage stage;
    private Factura factura;
    private final FacturaService facturaService = new FacturaService();
    private final UsuarioService usuarioService = new UsuarioService();
    private final EquipoService equipoService = new EquipoService();

    private ObservableList<LineaFactura> lineas = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        comboEstado.setItems(FXCollections.observableArrayList(EstadoFactura.values()));
        comboCliente.setItems(FXCollections.observableArrayList(usuarioService.listarClientes()));

        // Only available equipment
        comboEquipo.setItems(FXCollections.observableArrayList(
                equipoService.findAll().stream()
                        .filter(e -> e.getEstado() == EstadoEquipo.DISPONIBLE)
                        .collect(Collectors.toList())));

        setupTable();
    }

    private void setupTable() {
        itemsTable.setItems(lineas);
        itemsTable.setEditable(true);

        colItemConcepto.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getConcepto()));

        colItemCantidad
                .setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getCantidad()).asObject());
        colItemCantidad.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colItemCantidad.setOnEditCommit(e -> {
            e.getRowValue().setCantidad(e.getNewValue());
            recalculateTotals();
            itemsTable.refresh();
        });

        colItemDias.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getDias()).asObject());
        colItemDias.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colItemDias.setOnEditCommit(e -> {
            e.getRowValue().setDias(e.getNewValue());
            recalculateTotals();
            itemsTable.refresh();
        });

        colItemPrecio.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getPrecioDia()).asObject());
        colItemPrecio.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        colItemPrecio.setOnEditCommit(e -> {
            e.getRowValue().setPrecioDia(e.getNewValue());
            recalculateTotals();
            itemsTable.refresh();
        });

        colItemTotal.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getTotalLinea()).asObject());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
        if (factura != null) {
            txtNumero.setText(factura.getNumeroFactura());
            dateFecha.setValue(factura.getFecha());
            // Find client in combo
            if (factura.getClienteId() != null) {
                // This is O(N), usually retrieve by ID, but for small list ok
                comboCliente.getItems().stream()
                        .filter(c -> c.getId().equals(factura.getClienteId()))
                        .findFirst()
                        .ifPresent(c -> comboCliente.setValue(c));
            }
            comboEstado.setValue(factura.getEstado());
            lineas.addAll(factura.getItems());
        } else {
            this.factura = new Factura();
            dateFecha.setValue(java.time.LocalDate.now());
            comboEstado.setValue(EstadoFactura.COTIZACION);
        }
        recalculateTotals();
    }

    @FXML
    private void handleAgregarLinea() {
        Equipo equipo = comboEquipo.getValue();
        if (equipo != null) {
            LineaFactura linea = new LineaFactura(
                    equipo.getId(),
                    equipo.getNombre(),
                    1,
                    equipo.getTarifaDiaria(),
                    1);
            lineas.add(linea);
            recalculateTotals();
        }
    }

    @FXML
    private void handleEliminarLinea() {
        LineaFactura selected = itemsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            lineas.remove(selected);
            recalculateTotals();
        }
    }

    private void recalculateTotals() {
        // Update factura object transiently to calculate
        factura.setItems(lineas);

        lblSubtotal.setText(String.format("%.2f €", factura.getSubtotal()));
        lblIva.setText(String.format("%.2f €", factura.getIva()));
        lblTotal.setText(String.format("%.2f €", factura.getTotal()));
    }

    @FXML
    private void handleGuardar() {
        if (comboCliente.getValue() == null) {
            // alert
            return;
        }

        factura.setFecha(dateFecha.getValue());
        factura.setClienteId(comboCliente.getValue().getId());
        factura.setNombreClienteSnapshot(comboCliente.getValue().getNombre());
        factura.setEstado(comboEstado.getValue());
        factura.setItems(lineas); // Triggers recalc inside model

        if (factura.getId() != null) {
            facturaService.update(factura.getId(), factura);
        } else {
            facturaService.create(factura);
        }

        stage.close();
    }

    @FXML
    private void handleCancelar() {
        stage.close();
    }
}
