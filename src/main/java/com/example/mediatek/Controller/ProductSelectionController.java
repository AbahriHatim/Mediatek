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
import javafx.util.Callback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProductSelectionController {

    @FXML
    private TableView<Produit> productTableView;

    @FXML
    private TableColumn<Produit, Integer> idColumn;

    @FXML
    private TableColumn<Produit, String> nameColumn;
    @FXML
    private TableColumn<Produit, String> editeColumn;
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
    private TableColumn<ProduitFacture,String> addeddescColumn;

    @FXML
    private TableColumn<ProduitFacture, Double> addedProductPriceColumn;


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
    private Produit selectedProduct; // Add this field to store the selected product

    @FXML
    private void handleProductSelection() {
        selectedProduct = productTableView.getSelectionModel().getSelectedItem();
        if (selectedProduct != null && !quantityField.getText().isEmpty()) {
            addButton.setDisable(false);
        } else {
            addButton.setDisable(true);
        }
    }

    @FXML
    private void handleQuantitySelection() {
        String quantityText = quantityField.getText();
        if (!quantityText.isEmpty()) {
            try {
                int quantity = Integer.parseInt(quantityText);
                if (quantity > 0) {
                    addButton.setDisable(selectedProduct == null);
                } else {
                    addButton.setDisable(true);
                    showAlert(Alert.AlertType.ERROR, "Quantité invalide", "Veuillez entrer une quantité valide supérieure à zéro.");
                }
            } catch (NumberFormatException e) {
                addButton.setDisable(true);
                showAlert(Alert.AlertType.ERROR, "Quantité invalide", "Veuillez entrer une quantité valide (un nombre entier).");
            }
        } else {
            addButton.setDisable(true);
        }
    }

    @FXML

    private void handleAddButtonClick() {
        Produit selectedProduct = productTableView.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            showAlert(Alert.AlertType.ERROR, "Produit non sélectionné", "Veuillez sélectionner un produit.");
            return;
        }

        int quantityToAdd;
        try {
            quantityToAdd = Integer.parseInt(quantityField.getText());
        } catch (NumberFormatException e) {
            // Show alert if the entered value is not a valid integer
            showAlert(Alert.AlertType.ERROR, "Quantité invalide", "Veuillez entrer une quantité valide (un nombre).");
            return;
        }
        if (quantityToAdd <= 0) {
            showAlert(Alert.AlertType.ERROR, "Quantité invalide", "Veuillez entrer une quantité valide supérieure à zéro.");
            return;
        }

        if (quantityToAdd > selectedProduct.getQuantite_en_stock()) {
            showAlert(Alert.AlertType.ERROR, "Quantité insuffisante", "La quantité en stock de ce produit est insuffisante pour satisfaire votre demande.");
            return;
        }

        // Check if the product is already added
        Optional<ProduitFacture> existingProduct = addedProducts.stream()
                .filter(pf -> pf.getProduit_id() == selectedProduct.getProduit_id())
                .findFirst();

        if (existingProduct.isPresent()) {
            showAlert(Alert.AlertType.ERROR, "Produit déjà ajouté", "Le produit " + selectedProduct.getNom() + " a déjà été ajouté.");
            return;
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
            String logoPath = "C:/Users/surface/Downloads/logo.png";

            // Retrieve client information by client_id
            Client client = impFacture.getClientById(selectedClient.getClient_id());

            PDFGenerator pdfGenerator = new PDFGenerator();
            byte[] pdfData = pdfGenerator.generateInvoicePDF(client, addedProducts);

            String fileName = client.getNom() + "_Invoice.pdf";

            // Save the PDF to the database
            impFacture.savePDF(factureId, fileName, pdfData);

            // Download the PDF
            downloadPDF(pdfData, fileName);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Invoice generated and downloaded successfully.");
            closeWindow();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    private void downloadPDF(byte[] pdfData, String fileName) {
        try {
            File file = new File(System.getProperty("user.home") + "/Downloads/" + fileName);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(pdfData);
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle error
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
                    .filter(produit -> produit.getQuantite_en_stock() > 0)
                    .collect(Collectors.toList());
            productTableView.getItems().setAll(produits);
        } catch (DAOException e) {
            e.printStackTrace();
        }
    }


    private void initializeAddedProductsTable() {
        addedProductIdColumn.setCellValueFactory(new PropertyValueFactory<>("produit_id"));
        addedQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantite"));


        // Edit column
        TableColumn<ProduitFacture, Void> editColumn = new TableColumn<>("Edit");
        editColumn.setPrefWidth(80);
        Callback<TableColumn<ProduitFacture, Void>, TableCell<ProduitFacture, Void>> editCellFactory = new Callback<>() {
            @Override
            public TableCell<ProduitFacture, Void> call(final TableColumn<ProduitFacture, Void> param) {
                final TableCell<ProduitFacture, Void> cell = new TableCell<>() {
                    private final Button editButton = new Button("Edit");

                    {
                        editButton.setOnAction(event -> {
                            ProduitFacture produitFacture = getTableView().getItems().get(getIndex());
                            openEditQuantityDialog(produitFacture);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(editButton);
                        }
                    }
                };
                return cell;
            }
        };
        editColumn.setCellFactory(editCellFactory);

        TableColumn<ProduitFacture, Void> actionColumn = new TableColumn<>("Action");
        actionColumn.setPrefWidth(200);
        Callback<TableColumn<ProduitFacture, Void>, TableCell<ProduitFacture, Void>> actionCellFactory = new Callback<>() {
            @Override
            public TableCell<ProduitFacture, Void> call(final TableColumn<ProduitFacture, Void> param) {
                final TableCell<ProduitFacture, Void> cell = new TableCell<>() {
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
                };
                return cell;
            }
        };
        actionColumn.setCellFactory(actionCellFactory);

        addedProductsTableView.getColumns().addAll(editColumn, actionColumn);
    }





    private void openEditQuantityDialog(ProduitFacture produitFacture) {
        TextInputDialog dialog = new TextInputDialog(String.valueOf(produitFacture.getQuantite()));
        dialog.setTitle("Edit Quantity");
        dialog.setHeaderText("Edit Quantity of Product");
        dialog.setContentText("New Quantity:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(quantity -> {
            try {
                int newQuantity = Integer.parseInt(quantity);
                if (newQuantity <= 0) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Quantity", "Please enter a valid quantity greater than zero.");
                    return;
                }
                produitFacture.setQuantite(newQuantity);
                updateAddedProductsTableView();
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Quantity", "Please enter a valid quantity (an integer number).");
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