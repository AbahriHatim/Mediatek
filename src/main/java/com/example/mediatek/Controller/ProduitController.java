package com.example.mediatek.Controller;

import com.example.mediatek.Client;
import com.example.mediatek.Dao.DAOException;
import com.example.mediatek.Dao.impProduit;
import com.example.mediatek.DataBaseConnection;
import com.example.mediatek.Produit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Optional;
import java.util.ResourceBundle;

public class ProduitController implements Initializable {
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
    private TextField searchField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField prix_unitaireField;
    @FXML
    private TextField quantite_en_stockField;
    @FXML
    private Label demandeLabel;

    @FXML
    private Button addButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button viewStockAuditButton;
    @FXML
    private Button viewAllDemandesButton;
    @FXML
    private void onViewStockAuditButtonClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/mediatek/stock_audit.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setTitle("Stock Audit");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Unable to load stock audit view: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize the table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("produit_id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        prix_unitaireColumn.setCellValueFactory(new PropertyValueFactory<>("prix_unitaire"));
        quantite_en_stockColumn.setCellValueFactory(new PropertyValueFactory<>("quantite_en_stock"));

        // Load the product data
        loadProductData();

        // Set up the selection listener for the table
        clientTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nameField.setText(newSelection.getNom());
                descriptionField.setText(newSelection.getDescription());
                prix_unitaireField.setText(String.valueOf(newSelection.getPrix_unitaire()));
                quantite_en_stockField.setText(String.valueOf(newSelection.getQuantite_en_stock()));

                // Hide the Add button and show the Edit and Delete buttons when a product is selected
                editButton.setVisible(true);
                deleteButton.setVisible(true);
                addButton.setVisible(false);
                viewStockAuditButton.setVisible(false);
                viewAllDemandesButton.setVisible(false);

                displayDemande(newSelection.getProduit_id());

            } else {
                // Reset to default state when no product is selected
                resetFields();
                addButton.setVisible(true);
                editButton.setVisible(false);
                deleteButton.setVisible(false);
                viewStockAuditButton.setVisible(true);
                viewAllDemandesButton.setVisible(true);



            }
        });
    }
    private void displayDemande(int productId) {
        Connection connection = null;
        CallableStatement callableStatement = null;
        try {
            connection = DataBaseConnection.getConnection();
            String sql = "{ ? = call FonctionDeterminerDemande(?) }";
            callableStatement = connection.prepareCall(sql);
            callableStatement.registerOutParameter(1, Types.VARCHAR);
            callableStatement.setInt(2, productId);
            callableStatement.execute();
            String demande = callableStatement.getString(1);
            demandeLabel.setText("Demande: " + demande);
        } catch (SQLException e) {
            showAlert("Error", "Unable to fetch demande: " + e.getMessage(), Alert.AlertType.ERROR);
        } finally {
            try {
                if (callableStatement != null) callableStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                showAlert("Error", "Error closing resources: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
    @FXML
    private void onViewAllDemandesButtonClick() {
        try {
            System.out.println("Loading FXML...");
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/mediatek/all_demandes.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setTitle("All Demandes");
            stage.setScene(scene);
            System.out.println("Showing stage...");
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Unable to load all demandes view: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }



    @FXML
    private void loadProductData() {
        try {
            ObservableList<Produit> produitList = FXCollections.observableArrayList(produitDao.lister());
            clientTableView.setItems(produitList);
        } catch (DAOException e) {
            showAlert("Error", "Unable to load products: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    @FXML
    private void handleSearchButtonClick() {
        try {
            String searchInput = searchField.getText();

            Integer productId = null;
            String productName = null;

            // Try parsing the input as an integer (for ID)
            try {
                productId = Integer.parseInt(searchInput);
            } catch (NumberFormatException e) {
                // If parsing fails, assume it's a product name
                productName = searchInput;
            }

            ObservableList<Produit> produits = FXCollections.observableArrayList(produitDao.Recherche(productId, productName));
            clientTableView.setItems(produits);
        } catch (DAOException e) {
            showAlert("Error", "Failed to search products: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }



    @FXML
    protected void onAddClientButtonClick() {
        String name = nameField.getText();
        String description = descriptionField.getText();

        Double prix_unitaire;
        Integer quantite_en_stock;
        try {
            prix_unitaire = Double.valueOf(prix_unitaireField.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Prix unitaire invalide", "Veuillez entrer un prix unitaire valide (un nombre).");
            return;
        }
        try {
            quantite_en_stock = Integer.valueOf(quantite_en_stockField.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Quantité en stock invalide", "Veuillez entrer une quantité valide (un nombre).");
            return;
        }
        Produit newProduit = new Produit(0, name, description, prix_unitaire, quantite_en_stock);

        try {
            produitDao.ajouter(newProduit);
            loadProductData();  // Reload data
            resetFields();
            editButton.setVisible(false);
            deleteButton.setVisible(false);
            addButton.setVisible(true);
        } catch (DAOException e) {
            showAlert("Error", "Unable to add product: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    protected void onEditClientButtonClick() {
        Produit selectedClient = clientTableView.getSelectionModel().getSelectedItem();

        if (selectedClient != null) {
            selectedClient.setNom(nameField.getText());
            selectedClient.setDescription(descriptionField.getText());

            try {
                selectedClient.setPrix_unitaire(Double.parseDouble(prix_unitaireField.getText()));
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Prix unitaire invalide", "Veuillez entrer un prix unitaire valide (un nombre).");
                return;
            }
            try {
            selectedClient.setQuantite_en_stock(Integer.parseInt(quantite_en_stockField.getText()));

            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Quantité en stock invalide", "Veuillez entrer une quantité valide (un nombre).");
                return;
            }
            try {
                produitDao.edite(selectedClient);
                loadProductData();  // Reload data
                resetFields();
                addButton.setVisible(true);
                editButton.setVisible(false);
                deleteButton.setVisible(false);
            } catch (DAOException e) {
                showAlert("Error", "Unable to edit product: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Warning", "No product selected for editing.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    protected void onDeleteClientButtonClick() {
        Produit selectedClient = clientTableView.getSelectionModel().getSelectedItem();

        if (selectedClient != null) {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirm Deletion");
            confirmationAlert.setHeaderText("Are you sure you want to delete this product?");
            confirmationAlert.setContentText("This action cannot be undone.");

            ButtonType yesButton = new ButtonType("Yes");
            ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
            confirmationAlert.getButtonTypes().setAll(yesButton, noButton);

            Optional<ButtonType> result = confirmationAlert.showAndWait();
            if (result.isPresent() && result.get() == yesButton) {
                int productIdToDelete = selectedClient.getProduit_id();

                try {
                    produitDao.supprimer(productIdToDelete);
                    loadProductData();  // Reload data
                    resetFields();
                    addButton.setVisible(true);
                    editButton.setVisible(false);
                    deleteButton.setVisible(false);
                } catch (DAOException e) {
                    showAlert("Error", "Unable to delete product: " + e.getMessage() + "\nCause: " + e.getCause(), Alert.AlertType.ERROR);
                }
            } else {
                showAlert("Info", "Product deletion canceled.", Alert.AlertType.INFORMATION);
            }
        } else {
            showAlert("Warning", "No product selected for deletion.", Alert.AlertType.WARNING);
        }
    }

    private void resetFields() {
        nameField.setText("");
        descriptionField.setText("");
        prix_unitaireField.setText("");
        quantite_en_stockField.setText("");
        demandeLabel.setText("Demande: ");
    }

    // Helper method to show alerts
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
