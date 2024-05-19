package com.example.mediatek.Controller;

import com.example.mediatek.Client;
import com.example.mediatek.Dao.impView;
import com.example.mediatek.View;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class ViewController {
    @FXML
    private TableView<Client> clientTableView;
    @FXML
    private TableView<View> viewTableView;

    @FXML
    private TableColumn<Client, Integer> idClientColumn;
    @FXML
    private TableColumn<Client, String> nameClientColumn;
    @FXML
    private TableColumn<Client, String> addressColumn;
    @FXML
    private TableColumn<Client, String> emailColumn;
    @FXML
    private TableColumn<Client, String> telephoneColumn;

    @FXML
    private TableColumn<View, Integer> codeClientColumn;
    @FXML
    private TableColumn<View, String> nomClientColumn;
    @FXML
    private TableColumn<View, Double> chiffreAffairesColumn;
    @FXML
    private TableColumn<View, String> categorieClientColumn;

    @FXML
    private TextField nameClientField;

    private impView dao;

    @FXML
    public void initialize() {
        dao = new impView();

        initializeClientTable();
        initializeViewTable();

        loadClients();
    }

    private void initializeClientTable() {
        idClientColumn.setCellValueFactory(new PropertyValueFactory<>("client_id"));
        nameClientColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        telephoneColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));

        clientTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                loadView(newSelection.getClient_id());
            }
        });
    }

    private void initializeViewTable() {
        codeClientColumn.setCellValueFactory(new PropertyValueFactory<>("codeClient"));
        nomClientColumn.setCellValueFactory(new PropertyValueFactory<>("nomClient"));
        chiffreAffairesColumn.setCellValueFactory(new PropertyValueFactory<>("chiffreDAffaires"));
        categorieClientColumn.setCellValueFactory(new PropertyValueFactory<>("categorieClient"));
    }


    private void loadClients() {
        List<Client> clients = dao.listerClient();
        clientTableView.getItems().setAll(clients);
    }

    private void loadView(int clientId) {
        List<View> views = dao.listerView(clientId);
        viewTableView.getItems().setAll(views);
    }
}
