package com.example.mediatek.Contoller;

import com.example.mediatek.Client;
import com.example.mediatek.Dao.impClient;
import com.example.mediatek.Dao.impProduit;
import com.example.mediatek.Produit;
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

public class ProduitController {
    @FXML
    private Label welcomeText;

    private impProduit produitDao = new impProduit(); // Instantiate your DAO



    @FXML
    private TableView<Produit> clientTableView;



    @FXML
    private TableColumn<Produit, String> nameColumn;
    @FXML
    private TableColumn<Produit, Integer> idColumn;

    @FXML
    private TableColumn<Produit, String> descriptionColumn;

    @FXML
    private TableColumn<Produit, Double> prix_unitaireColumn;

    @FXML
    private TableColumn<Produit, Integer> quantite_en_stockColumn;

    @FXML
    protected void onLoadClientsButtonClick() {
        // Set up columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("produit_id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        prix_unitaireColumn.setCellValueFactory(new PropertyValueFactory<>("prix_unitaire"));
        quantite_en_stockColumn.setCellValueFactory(new PropertyValueFactory<>("quantite_en_stock"));

        // Populate table
        clientTableView.setItems(FXCollections.observableArrayList(produitDao.lister()));
    }


    // Method to add a new client
    @FXML
    private TextField nameField;

    @FXML
    private TextField descriptionField;

    @FXML
    private TextField prix_unitaireField;

    @FXML
    private TextField quantite_en_stockField;


    @FXML

    protected void onAddClientButtonClick() {
        String name = nameField.getText();
        String description = descriptionField.getText();
        Double prix_unitaire = Double.valueOf(prix_unitaireField.getText());
        Integer quantite_en_stock = Integer.valueOf(quantite_en_stockField.getText());

        Produit newProduit = new Produit(0, name, description, prix_unitaire, quantite_en_stock);

        produitDao.ajouter(newProduit);

        onLoadClientsButtonClick();
    }


    // Method to edit an existing client
    @FXML
    protected void onEditClientButtonClick() {
        // Get the selected client from the TableView
        Produit selectedClient = clientTableView.getSelectionModel().getSelectedItem();

        // Check if a client is selected
        if (selectedClient != null) {
            // Assuming you have input fields for editing, update the selected client's properties
            selectedClient.setNom(nameField.getText());
            selectedClient.setDescription(descriptionField.getText());
            selectedClient.setPrix_unitaire(Double.parseDouble(prix_unitaireField.getText()));
            selectedClient.setQuantite_en_stock(Integer.parseInt(quantite_en_stockField.getText()));

            // Call the DAO method to update the client in the database
            produitDao.edite(selectedClient);

            // Reload client data after editing
            onLoadClientsButtonClick();
        }
    }


    // Method to delete an existing client
    @FXML
    protected void onDeleteClientButtonClick() {
        // Assuming you have a selected client somehow, let's call it selectedClient
        // Here you would call the DAO method to delete it from the database
        // clientDao.supprimer(selectedClient);
        Produit selectedClient = clientTableView.getSelectionModel().getSelectedItem();
        // Assuming you have access to the client's ID
        int productdToDelete = selectedClient.getProduit_id(); // Assuming client_id is the correct attribute

        // Call the DAO method to delete the client
        produitDao.supprimer(productdToDelete);

        onLoadClientsButtonClick(); // Reload client data after deleting
    }

}
