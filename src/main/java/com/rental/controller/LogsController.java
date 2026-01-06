package com.rental.controller;

import com.rental.model.LogEntry;
import com.rental.service.LogService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.format.DateTimeFormatter;

public class LogsController {

    @FXML
    private TableView<LogEntry> logsTable;
    @FXML
    private TableColumn<LogEntry, String> colTimestamp;
    @FXML
    private TableColumn<LogEntry, String> colAction;
    @FXML
    private TableColumn<LogEntry, String> colType;
    @FXML
    private TableColumn<LogEntry, String> colId;
    @FXML
    private TableColumn<LogEntry, String> colDetails;
    @FXML
    private TableColumn<LogEntry, String> colUser;

    private final LogService logService = LogService.getInstance();
    private static final DateTimeFormatter D_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    public void initialize() {
        colTimestamp
                .setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTimestamp().format(D_FORMAT)));
        colAction.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getAction()));
        colType.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEntityType()));
        colId.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEntityId()));
        colDetails.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDetails()));
        colUser.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getUser()));

        loadLogs();
    }

    @FXML
    private void handleRecargar() {
        loadLogs();
    }

    private void loadLogs() {
        logsTable.setItems(FXCollections.observableArrayList(logService.findAll()));
    }
}
