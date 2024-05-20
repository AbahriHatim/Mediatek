package com.example.mediatek.Controller;

import com.example.mediatek.Dao.DAOException;
import com.example.mediatek.Dao.impProduit;
import com.example.mediatek.Produit;
import com.example.mediatek.ProduitFacture;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

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


    @FXML
    private void handleSubmitButtonClick() {
        try {
            for (ProduitFacture pf : addedProducts) {
                boolean produitExistsInFacture = produitDAO.checkProduitExistsInFacture(pf.getFacture_id(), pf.getProduit_id());
                if (!produitExistsInFacture) {
                    produitDAO.addProduitsFacture(pf.getFacture_id(), pf.getProduit_id(), pf.getQuantite());
                } else {
                    showAlert(Alert.AlertType.ERROR, "Product Already Exists", "Product with ID " + pf.getProduit_id() + " already exists in facture " + pf.getFacture_id());
                }
            }

            // Fermer la fenêtre de sélection de produits après soumission
            closeWindow();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "SQL Error", e.getMessage());
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
