package com.example.mediatek.Controller;

import com.example.mediatek.Client;
import com.example.mediatek.Dao.DAOException;
import com.example.mediatek.Dao.impClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ClientController implements Initializable {
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
    private TextField searchIdField;

    @FXML
    private TextField searchNameField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField telephoneField;

    @FXML
    private Button addButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize the table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("client_id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        telephoneColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));

        // Load the client data
        loadClientData();

        // Set up the selection listener for the table
        clientTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nameField.setText(newSelection.getNom());
                addressField.setText(newSelection.getAdresse());
                emailField.setText(newSelection.getEmail());
                telephoneField.setText(String.valueOf(newSelection.getTelephone()));

                // Hide the Add button and show the Edit and Delete buttons when a client is selected
                editButton.setVisible(true);
                deleteButton.setVisible(true);
                addButton.setVisible(false);
            } else {
                // Reset to default state when no client is selected
                resetFields();
                addButton.setVisible(true);
                editButton.setVisible(false);
                deleteButton.setVisible(false);
            }
        });
    }
@FXML
    private void loadClientData() {
        try {
            ObservableList<Client> clientList = FXCollections.observableArrayList(clientDao.lister());
            clientTableView.setItems(clientList);
        } catch (DAOException e) {
            showAlert("Error", "Unable to load clients: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    protected void onAddClientButtonClick() {
        String name = nameField.getText();
        String address = addressField.getText();
        String email = emailField.getText();
        Double telephone;
        try {
            telephone = Double.valueOf(telephoneField.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Téléphone invalide", "Veuillez entrer un numéro de téléphone valide (un nombre).");
            return;
        }
        Client newClient = new Client(0, name, address, email, telephone);

        try {
            clientDao.ajouter(newClient);
            loadClientData();  // Reload data
            resetFields();
            editButton.setVisible(false);
            deleteButton.setVisible(false);
            addButton.setVisible(true);
        } catch (DAOException e) {
            showAlert("Error", "Unable to add client: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    protected void onEditClientButtonClick() {
        Client selectedClient = clientTableView.getSelectionModel().getSelectedItem();

        if (selectedClient != null) {
            selectedClient.setNom(nameField.getText());
            selectedClient.setAdresse(addressField.getText());
            selectedClient.setEmail(emailField.getText());
            selectedClient.setTelephone(Double.parseDouble(telephoneField.getText()));

            try {
                clientDao.edite(selectedClient);
                loadClientData();  // Reload data
                resetFields();
                addButton.setVisible(true);
                editButton.setVisible(false);
                deleteButton.setVisible(false);
            } catch (DAOException e) {
                showAlert("Error", "Unable to edit client: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Warning", "No client selected for editing.", Alert.AlertType.WARNING);
        }
    }
    @FXML
    private void handleSearchButtonClick() {
        try {
            int clientId = Integer.parseInt(searchIdField.getText());
            String clientName = searchNameField.getText();
            ObservableList<Client> clients = FXCollections.observableArrayList(clientDao.Recherche(clientId, clientName));
            clientTableView.setItems(clients);
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid client ID format.", Alert.AlertType.ERROR);
        } catch (DAOException e) {
            showAlert("Error", "Failed to search clients: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    @FXML
    protected void onDeleteClientButtonClick() {
        Client selectedClient = clientTableView.getSelectionModel().getSelectedItem();

        if (selectedClient != null) {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirm Deletion");
            confirmationAlert.setHeaderText("Are you sure you want to delete this client?");
            confirmationAlert.setContentText("This action cannot be undone.");

            ButtonType yesButton = new ButtonType("Yes");
            ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
            confirmationAlert.getButtonTypes().setAll(yesButton, noButton);

            Optional<ButtonType> result = confirmationAlert.showAndWait();
            if (result.isPresent() && result.get() == yesButton) {
                int clientIdToDelete = selectedClient.getClient_id();

                try {
                    clientDao.supprimer(clientIdToDelete);
                    loadClientData();  // Reload data
                    resetFields();
                    addButton.setVisible(true);
                    editButton.setVisible(false);
                    deleteButton.setVisible(false);
                } catch (DAOException e) {
                    showAlert("Error", "Unable to delete client: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            } else {
                showAlert("Info", "Client deletion canceled.", Alert.AlertType.INFORMATION);
            }
        } else {
            showAlert("Warning", "No client selected for deletion.", Alert.AlertType.WARNING);
        }
    }

    private void resetFields() {
        nameField.setText("");
        addressField.setText("");
        emailField.setText("");
        telephoneField.setText("");
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
