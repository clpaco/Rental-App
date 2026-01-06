package com.rental.controller;

import com.rental.model.Usuario;
import com.rental.model.enums.TipoUsuario;
import com.rental.service.UsuarioService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UsuarioFormController {

    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtTelefono;
    @FXML
    private TextField txtNif;
    @FXML
    private TextField txtDireccion;
    @FXML
    private ComboBox<TipoUsuario> comboRol;
    @FXML
    private TextArea txtNotas;

    private Stage stage;
    private Usuario usuario;
    private final UsuarioService usuarioService = new UsuarioService();
    private boolean isEditMode = false;

    @FXML
    public void initialize() {
        comboRol.setItems(FXCollections.observableArrayList(TipoUsuario.values()));
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        if (usuario != null) {
            isEditMode = true;
            txtNombre.setText(usuario.getNombre());
            txtEmail.setText(usuario.getEmail());
            txtTelefono.setText(usuario.getTelefono());
            txtNif.setText(usuario.getNif());
            txtDireccion.setText(usuario.getDireccion());
            comboRol.setValue(usuario.getRol());
            txtNotas.setText(usuario.getNotas());
        } else {
            this.usuario = new Usuario();
            comboRol.setValue(TipoUsuario.CLIENTE); // Default
        }
    }

    @FXML
    private void handleGuardar() {
        if (!validate())
            return;

        usuario.setNombre(txtNombre.getText());
        usuario.setEmail(txtEmail.getText());
        usuario.setTelefono(txtTelefono.getText());
        usuario.setNif(txtNif.getText());
        usuario.setDireccion(txtDireccion.getText());
        usuario.setRol(comboRol.getValue());
        usuario.setNotas(txtNotas.getText());

        if (isEditMode) {
            usuarioService.update(usuario.getId(), usuario);
        } else {
            usuarioService.create(usuario);
        }

        stage.close();
    }

    @FXML
    private void handleCancelar() {
        stage.close();
    }

    private boolean validate() {
        if (txtNombre.getText().isEmpty() || comboRol.getValue() == null) {
            System.out.println("Validation failed: Required fields missing");
            return false;
        }
        return true;
    }
}
