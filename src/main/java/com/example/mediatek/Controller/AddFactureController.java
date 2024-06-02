package com.example.mediatek.Controller;

import com.example.mediatek.Client;
import com.example.mediatek.Dao.DAOException;
import com.example.mediatek.Dao.ImpFacture;
import com.example.mediatek.Dao.impClient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class AddFactureController {
    private Client selectedClient;
    @FXML
    private TableView<Client> clientTableView;

    @FXML
    private TableColumn<Client, Integer> idColumn;

    @FXML
    private TableColumn<Client, String> nameClientColumn;

    @FXML
    private TableColumn<Client, String> addressColumn;

    @FXML
    private TableColumn<Client, String> emailColumn;

    @FXML
    private TableColumn<Client, String> telephoneColumn;
    private FactureContoller factureController; // Reference to FactureController

    public void setFactureController(FactureContoller factureController) {
        this.factureController = factureController;
    }

    @FXML
    private Button nextButton;
    impClient clientDAO = new impClient(); // Créez une instance de votre DAO
    private ImpFacture factureDao = new ImpFacture();


    public void initialize() {
        initializeClientTable();
        loadClients();
        nextButton.setDisable(true);

        clientTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nextButton.setDisable(false);
                selectedClient = newSelection; // Update selectedClient when a client is selected
                int selectedClientId = selectedClient.getClient_id();
                System.out.println("Selected client ID: " + selectedClientId);
            } else {
                nextButton.setDisable(true);
                selectedClient = null; // Reset selectedClient when no client is selected
            }
        });
    }

    private void initializeClientTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("client_id"));
        nameClientColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        telephoneColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));
    }

    private void loadClients() {
        try {
            if (clientDAO != null) {
                List<Client> clients = clientDAO.lister();
                clientTableView.getItems().setAll(clients);
            } else {
                System.err.println("Le DAO des clients n'a pas été initialisé.");
            }
        } catch (DAOException e) {
            e.printStackTrace(); // Gérer l'exception d'une manière appropriée dans votre application
        }
    }

    @FXML
    private void handleNextClick() {
        // Obtenez le client sélectionné
        Client selectedClient = clientTableView.getSelectionModel().getSelectedItem();
        if (selectedClient == null) {
            // Affichez un message d'erreur ou prenez d'autres mesures appropriées si aucun client n'est sélectionné
            return;
        }

        try {
            // Créez une nouvelle facture pour le client avec la date actuelle
            int factureId = factureDao.createFacture(selectedClient.getClient_id(), new Date());

            // Ouvrez la popup de sélection des produits
            openProductSelectionPopup(factureId);

        } catch (SQLException e) {
            e.printStackTrace();
            // Gérez l'exception d'une manière appropriée dans votre application
        }
    }

    private void openProductSelectionPopup(int factureId) {
        try {
            // Load the FXML file of the product selection window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/mediatek/product_selection_popup.fxml"));
            Scene scene = new Scene(loader.load());

            // Create a new stage for the product selection window
            Stage productStage = new Stage();
            productStage.setTitle("Product Selection");
            productStage.setScene(scene);
            productStage.initModality(Modality.APPLICATION_MODAL);

            // Get the controller of the product selection window
            ProductSelectionController controller = loader.getController();

            // Pass the facture ID and the selected client to the product selection controller
            controller.setFactureId(factureId);
            controller.setClient(selectedClient);

            // Show the product selection window
            productStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
