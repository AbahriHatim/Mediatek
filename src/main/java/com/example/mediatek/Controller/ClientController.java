package com.example.mediatek.Controller;

import com.example.mediatek.Client;
import com.example.mediatek.Dao.DAOException;
import com.example.mediatek.Dao.impClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class ClientController {
    @FXML
    private Label welcomeText;

    private impClient clientDao = new impClient(); // Instantiate your DAO

    @FXML
    private TableView<Client> clientTableView;

    @FXML
    private TableColumn<Client, String> nameColumn;
    @FXML
    private TableColumn<Client, Integer> idColumn;
    @FXML
    private TableColumn<Client, String> addressColumn;
    @FXML
    private TableColumn<Client, String> emailColumn;
    @FXML
    private TableColumn<Client, String> telephoneColumn;

    @FXML
    private TextField nameField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField telephoneField;

    @FXML
    protected void onLoadClientsButtonClick() {
        // Set up columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("client_id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        telephoneColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));

        try {
            // Populate table
            ObservableList<Client> clientList = FXCollections.observableArrayList(clientDao.lister());
            clientTableView.setItems(clientList);

            // Handle selection change
            clientTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    // Populate text fields with selected Client object's data
                    nameField.setText(newSelection.getNom());
                    addressField.setText(newSelection.getAdresse());
                    emailField.setText(String.valueOf(newSelection.getEmail()));
                    telephoneField.setText(String.valueOf(newSelection.getTelephone()));
                }
            });
        } catch (DAOException e) {
            // Handle DAOException (e.g., log the error, show an alert to the user)
            e.printStackTrace();
        }
    }

    @FXML
    protected void onAddClientButtonClick() {
        String name = nameField.getText();
        String address = addressField.getText();
        String email = emailField.getText();
        String telephone = telephoneField.getText();

        Client newClient = new Client(0, name, address, email, telephone);

        try {
            clientDao.ajouter(newClient);
            onLoadClientsButtonClick();
        } catch (DAOException e) {
            // Handle DAOException (e.g., log the error, show an alert to the user)
            e.printStackTrace();
        }
    }

    @FXML
    protected void onEditClientButtonClick() {
        // Get the selected client from the TableView
        Client selectedClient = clientTableView.getSelectionModel().getSelectedItem();

        // Check if a client is selected
        if (selectedClient != null) {
            // Assuming you have input fields for editing, update the selected client's properties
            selectedClient.setNom(nameField.getText());
            selectedClient.setAdresse(addressField.getText());
            selectedClient.setEmail(emailField.getText());
            selectedClient.setTelephone(telephoneField.getText());

            try {
                clientDao.edite(selectedClient);
                onLoadClientsButtonClick();
            } catch (DAOException e) {
                // Handle DAOException (e.g., log the error, show an alert to the user)
                e.printStackTrace();
            }
        } else {
            // Display an error message or handle the case where no client is selected
            System.out.println("No client selected for editing.");
        }
    }

    @FXML
    protected void onDeleteClientButtonClick() {
        Client selectedClient = clientTableView.getSelectionModel().getSelectedItem();

        if (selectedClient != null) {
            int clientIdToDelete = selectedClient.getClient_id();

            try {
                clientDao.supprimer(clientIdToDelete);
                onLoadClientsButtonClick();
            } catch (DAOException e) {
                // Handle DAOException (e.g., log the error, show an alert to the user)
                e.printStackTrace();
            }
        } else {
            // Display an error message or handle the case where no client is selected
            System.out.println("No client selected for deletion.");
        }
    }
}
