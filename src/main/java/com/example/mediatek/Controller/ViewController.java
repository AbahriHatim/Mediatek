package com.example.mediatek.Controller;

import com.example.mediatek.Client;
import com.example.mediatek.Dao.DAOException;
import com.example.mediatek.Dao.impClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class ViewController implements Initializable {

    @FXML
    private TableView<Client> categorizedClientTableView;

    @FXML
    private TableColumn<Client, Integer> idColumn;
    @FXML
    private TableColumn<Client, String> nameColumn;
    @FXML
    private TableColumn<Client, Double> chiffreAffairesColumn;
    @FXML
    private TableColumn<Client, String> categorieColumn;

    private impClient clientDao = new impClient(); // Instantiate your DAO

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize the table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("client_id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        chiffreAffairesColumn.setCellValueFactory(new PropertyValueFactory<>("chiffreDAffaires"));
        categorieColumn.setCellValueFactory(new PropertyValueFactory<>("categorie"));

        // Load the categorized client data
        try {
            loadCategorizedData();
        } catch (DAOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void loadCategorizedData() throws DAOException {
        try {
            ObservableList<Client> categorizedClientList = FXCollections.observableArrayList(clientDao.getClientDetailsFromView());
            categorizedClientTableView.setItems(categorizedClientList);
        } catch (DAOException e) {
            throw new DAOException("Error retrieving client details from view", e);
        }
    }
}
