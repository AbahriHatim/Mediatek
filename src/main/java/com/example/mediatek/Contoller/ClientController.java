package com.example.mediatek.Contoller;

import com.example.mediatek.Client;
import com.example.mediatek.Dao.impClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.Properties;

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
    protected void onLoadClientsButtonClick() {
        // Set up columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("client_id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        telephoneColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));

        // Populate table
        clientTableView.setItems(FXCollections.observableArrayList(clientDao.lister()));
    }


    // Method to add a new client
    @FXML
    private TextField nameField;

    @FXML
    private TextField addressField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField telephoneField;


    @FXML

    protected void onAddClientButtonClick() {
        String name = nameField.getText();
        String address = addressField.getText();
        String email = emailField.getText();
        String telephone = telephoneField.getText();

        Client newClient = new Client(0, name, address, email, telephone);

        clientDao.ajouter(newClient);

        onLoadClientsButtonClick();
    }


    // Method to edit an existing client
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

            // Call the DAO method to update the client in the database
            clientDao.edite(selectedClient);

            // Reload client data after editing
            onLoadClientsButtonClick();
        } else {
            // Display an error message or handle the case where no client is selected
        }
    }


    // Method to delete an existing client
    @FXML
    protected void onDeleteClientButtonClick() {
        // Assuming you have a selected client somehow, let's call it selectedClient
        // Here you would call the DAO method to delete it from the database
        // clientDao.supprimer(selectedClient);
        Client selectedClient = clientTableView.getSelectionModel().getSelectedItem();
        // Assuming you have access to the client's ID
        int clientIdToDelete = selectedClient.getClient_id(); // Assuming client_id is the correct attribute

        // Call the DAO method to delete the client
        clientDao.supprimer(clientIdToDelete);

        onLoadClientsButtonClick(); // Reload client data after deleting
    }

}
