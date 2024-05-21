package com.example.mediatek.Controller;

import com.example.mediatek.Client;
import com.example.mediatek.Dao.DAOException;
import com.example.mediatek.Dao.ImpFacture;
import com.example.mediatek.Dao.impProduit;
import com.example.mediatek.PDFGenerator;
import com.example.mediatek.Produit;
import com.example.mediatek.ProduitFacture;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProductSelectionController {

    @FXML
    private TableView<Produit> productTableView;

    @FXML
    private TableColumn<Produit, Integer> idColumn;

    @FXML
    private TableColumn<Produit, String> nameColumn;

    @FXML
    private TableColumn<Produit, String> descriptionColumn;

    @FXML
    private TableColumn<Produit, Double> priceColumn;

    @FXML
    private TableColumn<Produit, Integer> quantityColumn;

    @FXML
    private TextField quantityField;

    @FXML
    private TableView<ProduitFacture> addedProductsTableView;

    @FXML
    private TableColumn<ProduitFacture, Integer> addedProductIdColumn;

    @FXML
    private TableColumn<ProduitFacture, String> addedProductNameColumn;

    @FXML
    private TableColumn<ProduitFacture, Integer> addedQuantityColumn;

    @FXML
    private TableColumn<ProduitFacture, Void> actionColumn;
    @FXML
    private Button addButton;

    @FXML
    private Button submitButton;

    private List<ProduitFacture> addedProducts = new ArrayList<>();

    private int factureId;

    private final impProduit produitDAO = new impProduit();

    public void initialize() {
        initializeProductTable();
        loadProducts();
        initializeAddedProductsTable();

        addButton.setDisable(true);

        productTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null && !quantityField.getText().isEmpty()) {
                addButton.setDisable(false);
            } else {
                addButton.setDisable(true);
            }
        });
    }

    public void setFactureId(int factureId) {
        this.factureId = factureId;
    }

    @FXML
    private void handleAddButtonClick() {
        Produit selectedProduct = productTableView.getSelectionModel().getSelectedItem();
        int quantityToAdd = Integer.parseInt(quantityField.getText());

        // Vérifier si la quantité à ajouter est valide
        if (quantityToAdd <= 0) {
            showAlert(Alert.AlertType.ERROR, "Quantité invalide", "Veuillez entrer une quantité valide supérieure à zéro.");
            return;
        }

        if (quantityToAdd > selectedProduct.getQuantite_en_stock()) {
            showAlert(Alert.AlertType.ERROR, "Quantité insuffisante", "La quantité en stock de ce produit est insuffisante pour satisfaire votre demande.");
            return;
        }

        for (ProduitFacture pf : addedProducts) {
            if (pf.getProduit_id() == selectedProduct.getProduit_id()) {
                showAlert(Alert.AlertType.ERROR, "Produit déjà ajouté", "Le produit " + selectedProduct.getNom() + " a déjà été ajouté.");
                return; // Sortir de la méthode pour éviter d'ajouter le produit une deuxième fois
            }
        }

        ProduitFacture produitFacture = new ProduitFacture(factureId, selectedProduct.getProduit_id(), quantityToAdd);
        addedProducts.add(produitFacture);
        updateAddedProductsTableView();
    }
    private Client selectedClient;

    public void setClient(Client client) {
        this.selectedClient = client;
    }
    // In FactureController class
    ImpFacture impFacture = new ImpFacture();
    @FXML
    private void handleSubmitButtonClick() {
        if (selectedClient == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Client information is missing.");
            return;
        }

        try {
            for (ProduitFacture pf : addedProducts) {
                produitDAO.addProduitsFacture(pf.getFacture_id(), pf.getProduit_id(), pf.getQuantite());
            }

            // Retrieve client information by client_id
            Client client = impFacture.getClientById(selectedClient.getClient_id());

            PDFGenerator pdfGenerator = new PDFGenerator();
            pdfGenerator.generateInvoicePDF(client, addedProducts);

            closeWindow();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }






    private void initializeProductTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("produit_id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("prix_unitaire"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantite_en_stock"));
    }

    private void loadProducts() {
        try {
            List<Produit> produits = produitDAO.lister().stream()
                    .filter(produit -> produit.getQuantite_en_stock() > 0) // Filtrer les produits avec quantité > 0
                    .collect(Collectors.toList());
            productTableView.getItems().setAll(produits);
        } catch (DAOException e) {
            e.printStackTrace();
        }
    }


    private void initializeAddedProductsTable() {
        addedProductIdColumn.setCellValueFactory(new PropertyValueFactory<>("produit_id"));

        addedQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantite"));

        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Supprimer");

            {
                deleteButton.setOnAction(event -> {
                    ProduitFacture produitFacture = getTableView().getItems().get(getIndex());
                    addedProducts.remove(produitFacture);
                    updateAddedProductsTableView();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });
    }

    private void updateAddedProductsTableView() {
        addedProductsTableView.getItems().clear();
        addedProductsTableView.getItems().addAll(addedProducts);
    }

    private void closeWindow() {
        Stage stage = (Stage) submitButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
