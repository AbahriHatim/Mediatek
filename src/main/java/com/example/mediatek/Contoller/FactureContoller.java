package com.example.mediatek.Contoller;

import com.example.mediatek.ProduitFacture;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import com.example.mediatek.Client;
import com.example.mediatek.Dao.ImpFacture;
import com.example.mediatek.Produit;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Date;
import java.util.List;

public class FactureContoller {
    private ImpFacture factureDao = new ImpFacture(); // Instantiate the DAO

    @FXML
    private TableView<Client> clientTableView;
    @FXML
    private TableView<Produit> produitTableView;

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
    private TableColumn<Produit, Integer> idProduitColumn;
    @FXML
    private TableColumn<Produit, String> nameProduitColumn;
    @FXML
    private TableColumn<Produit, String> descriptionColumn;
    @FXML
    private TableColumn<Produit, Double> prixUnitaireColumn;
    @FXML
    private TableColumn<Produit, Integer> quantiteEnStockColumn;

    @FXML
    private TextField nameClientField;
    @FXML
    private TextField nameProduitField;
    @FXML
    private TextField quantiteField;

    @FXML
    public void initialize() {
        initializeClientTable();
        initializeProduitTable();
    }

    private void initializeClientTable() {
        idClientColumn.setCellValueFactory(new PropertyValueFactory<>("client_id"));
        nameClientColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        telephoneColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        onLoadClientsButtonClickClient();
        clientTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nameClientField.setText(newSelection.getNom());

            }
        });
    }

    private void initializeProduitTable() {
        idProduitColumn.setCellValueFactory(new PropertyValueFactory<>("produit_id"));
        nameProduitColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        prixUnitaireColumn.setCellValueFactory(new PropertyValueFactory<>("prix_unitaire"));
        quantiteEnStockColumn.setCellValueFactory(new PropertyValueFactory<>("quantite_en_stock"));
        onLoadProduitsButtonClick();
        produitTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nameProduitField.setText(newSelection.getNom());

            }
        });
    }

    @FXML
    protected void onLoadClientsButtonClickClient() {
        ObservableList<Client> clientList = FXCollections.observableArrayList(factureDao.listerClient());
        clientTableView.setItems(clientList);
    }

    @FXML
    protected void onLoadProduitsButtonClick() {
        ObservableList<Produit> produitList = FXCollections.observableArrayList(factureDao.listerProduit());
        produitTableView.setItems(produitList);
    }

    @FXML
    protected void onAddToInvoiceButtonClick() {
        String clientName = nameClientField.getText();
        String produitName = nameProduitField.getText();
        int quantite = Integer.parseInt(quantiteField.getText());

        // Fetch the client ID based on the client name
        int clientId = factureDao.getClientIdByName(clientName);

        // Fetch the product based on the product name
        Produit produit = factureDao.getProduitByName(produitName);

        // Create ProduitFacture object with the product and quantity
        ProduitFacture produitFacture = new ProduitFacture(0, produit.getProduit_id(), quantite);

        // Create a list with the produitFacture object
        List<ProduitFacture> produitsFacture = List.of(produitFacture);

        // Create the invoice
        factureDao.createInvoice(clientId, new Date(), produitsFacture);

        // Refresh the tables
        onLoadClientsButtonClickClient();
        onLoadProduitsButtonClick();
    }

}
