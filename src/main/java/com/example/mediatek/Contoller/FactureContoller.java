package com.example.mediatek.Contoller;

import com.example.mediatek.Facture;
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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.Integer.parseInt;

public class FactureContoller {
    private ImpFacture factureDao = new ImpFacture(); // Instantiate the DAO

    @FXML
    private TableView<Client> clientTableView;
    @FXML
    private TableView<Produit> produitTableView;
    @FXML
    private TableView<Facture> factureTableView;
    @FXML
    private TableView<ProduitFacture> produitFactureTableView;


    @FXML
    private TableColumn<Facture, Integer> idFactureColumn;
    @FXML
    private TableColumn<Facture, Integer> ClientIdColumn;
    @FXML
    private TableColumn<Facture, Date> dateFactureColumn;

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
    private TextField ClientIdField;

    @FXML
    private TextField FactureidField;



    @FXML
    public void initialize() {
        initializeClientTable();
        initializeProduitTable();
        initializeFactureTable();



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
    private void initializeFactureTable() {
        idFactureColumn.setCellValueFactory(new PropertyValueFactory<>("facture_id"));
        ClientIdColumn.setCellValueFactory(new PropertyValueFactory<>("client_id"));
        dateFactureColumn.setCellValueFactory(new PropertyValueFactory<>("fac_date"));
        // Assurez-vous que les noms des propriétés correspondent à ceux de votre classe Facture


        onLoadFacturesButtonClick();

        // Ajoutez un écouteur pour sélectionner une facture dans la table
        factureTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // Mettez à jour les champs de saisie avec les informations de la facture sélectionnée
                FactureidField.setText(String.valueOf(newSelection.getFacture_id()));


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
    protected void onLoadFacturesButtonClick() {
        ObservableList<Facture> factureList = FXCollections.observableArrayList(factureDao.listerFacture());
        factureTableView.setItems(factureList);
    }


@FXML
protected void onAddFactureButtonClick() {
    // Récupérer l'ID du client à partir de son nom
    String clientName = nameClientField.getText();
    int clientId = factureDao.getClientIdByName(clientName);


    try {
        int factureId = factureDao.createFacture(clientId, new Date());



        onLoadClientsButtonClickClient();
        onLoadProduitsButtonClick();
        onLoadFacturesButtonClick();
    } catch (SQLException e) {
        e.printStackTrace();
        // Gérer l'exception
    }
}
    @FXML
    protected void onAddProduitButtonClick() {
        // Récupération des données saisies
        int factureId = Integer.parseInt(FactureidField.getText());
        String produitName = nameProduitField.getText();
        int quantite = Integer.parseInt(quantiteField.getText());

        // Récupération du produit depuis la base de données
        Produit produit = factureDao.getProduitByName(produitName);

        try {
            // Ajout du produit facturé à la table Produits_Facture
            factureDao.addProduitsFacture(factureId, produit.getProduit_id(), quantite);
            onLoadClientsButtonClickClient();
            onLoadProduitsButtonClick();
            onLoadFacturesButtonClick();
        } catch (SQLException e) {
            e.printStackTrace();

        }
    }



}
