package com.rental.controller;

import com.rental.app.Main;
import com.rental.model.Usuario;
import com.rental.model.enums.TipoUsuario;
import com.rental.service.UsuarioService;
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

public class UsuariosController {

    @FXML
    private TextField searchField;
    @FXML
    private TableView<Usuario> usuariosTable;
    @FXML
    private TableColumn<Usuario, String> colNombre;
    @FXML
    private TableColumn<Usuario, String> colEmail;
    @FXML
    private TableColumn<Usuario, String> colTelefono;
    @FXML
    private TableColumn<Usuario, TipoUsuario> colRol;
    @FXML
    private TableColumn<Usuario, String> colNif;

    private final UsuarioService usuarioService = new UsuarioService();
    private ObservableList<Usuario> masterData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        colEmail.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        colTelefono.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTelefono()));
        colRol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getRol()));
        colNif.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNif()));

        loadData();
    }

    private void loadData() {
        masterData.clear();
        masterData.addAll(usuarioService.findAll());
        usuariosTable.setItems(masterData);
    }

    @FXML
    private void handleBuscar() {
        String query = searchField.getText().toLowerCase();
        if (query.isEmpty()) {
            usuariosTable.setItems(masterData);
        } else {
            ObservableList<Usuario> filtered = masterData
                    .filtered(u -> (u.getNombre() != null && u.getNombre().toLowerCase().contains(query)) ||
                            (u.getEmail() != null && u.getEmail().toLowerCase().contains(query)) ||
                            (u.getNif() != null && u.getNif().toLowerCase().contains(query)));
            usuariosTable.setItems(filtered);
        }
    }

    @FXML
    private void handleNuevoUsuario() {
        showForm(null);
    }

    @FXML
    private void handleEditar() {
        Usuario selected = usuariosTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showForm(selected);
        } else {
            showAlert("Seleccione un usuario para editar.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleEliminar() {
        Usuario selected = usuariosTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar eliminación");
            alert.setHeaderText("¿Seguro que desea eliminar a: " + selected.getNombre() + "?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                usuarioService.delete(selected.getId());
                loadData();
            }
        } else {
            showAlert("Seleccione un usuario para eliminar.", Alert.AlertType.WARNING);
        }
    }

    private void showForm(Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/fxml/usuario_form.fxml"));
            Stage stage = new Stage();
            stage.setTitle(usuario == null ? "Nuevo Usuario" : "Editar Usuario");
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.setScene(new Scene(loader.load()));

            UsuarioFormController controller = loader.getController();
            controller.setUsuario(usuario);
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
