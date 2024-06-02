package com.example.mediatek.Controller;

import com.example.mediatek.Dao.DAOException;
import com.example.mediatek.Facture;
import com.example.mediatek.ProduitFacture;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import com.example.mediatek.Client;
import com.example.mediatek.Dao.ImpFacture;
import com.example.mediatek.Produit;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
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
    private TableColumn<Facture, Void> actionsColumn;

    private void initializeActionsColumn() {
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button btnView = new Button("View");
            private final Button btnDownload = new Button("Download");

            {
                btnView.setOnAction(event -> {
                    Facture facture = getTableView().getItems().get(getIndex());
                    handleViewFacture(facture);
                });

                btnDownload.setOnAction(event -> {
                    Facture facture = getTableView().getItems().get(getIndex());
                    handleDownloadFacture(facture);
                });

                HBox hbox = new HBox(btnView, btnDownload);
                hbox.setSpacing(10);
                setGraphic(hbox);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(btnView, btnDownload);
                    hbox.setSpacing(10);
                    setGraphic(hbox);
                }
            }
        });
    }

    private void handleViewFacture(Facture facture) {
        try {
            File pdfFile = factureDao.getFacturePDF(facture.getFacture_id());

            if (pdfFile != null && pdfFile.exists()) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(pdfFile);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "PDF viewer is not supported on your system.");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "The requested PDF file does not exist.");
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while trying to view the PDF.");
        }
    }

    private void handleDownloadFacture(Facture facture) {
        try {
            File pdfFile = factureDao.getFacturePDF(facture.getFacture_id());

            if (pdfFile != null && pdfFile.exists()) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save Facture PDF");
                fileChooser.setInitialFileName("facture_" + facture.getFacture_id() + ".pdf");
                File saveFile = fileChooser.showSaveDialog(factureTableView.getScene().getWindow());

                if (saveFile != null) {
                    Files.copy(pdfFile.toPath(), saveFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    showAlert(Alert.AlertType.INFORMATION, "Success", "The PDF has been downloaded successfully.");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "The requested PDF file does not exist.");
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while trying to download the PDF.");
        }
    }


    @FXML
    public void initialize() {
        /*initializeClientTable();
        initializeProduitTable();*/
        initializeFactureTable();
        initializeActionsColumn();




    }

    private void initializeClientTable() {
        idClientColumn.setCellValueFactory(new PropertyValueFactory<>("client_id"));
        nameClientColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        telephoneColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        loadClients();

        clientTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // Store or use the selected client information as needed
            }
        });
    }

    private void loadClients() {
        ObservableList<Client> clientList = FXCollections.observableArrayList(factureDao.listerClient());
        clientTableView.setItems(clientList);
    }

    private void initializeFactureTable() {
        idFactureColumn.setCellValueFactory(new PropertyValueFactory<>("facture_id"));
        ClientIdColumn.setCellValueFactory(new PropertyValueFactory<>("client_id"));
        dateFactureColumn.setCellValueFactory(new PropertyValueFactory<>("fac_date"));
        // Assurez-vous que les noms des propriétés correspondent à ceux de votre classe Facture


        onLoadFacturesButtonClick();


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
    public void loadFactures() {
        List<Facture> factures = factureDao.listerFacture();
        factureTableView.getItems().setAll(factures);
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
            if (produit != null) {
                if (quantite <= produit.getQuantite_en_stock()) {
                    boolean produitExistsInFacture = factureDao.checkProduitExistsInFacture(factureId, produit.getProduit_id());
                    if (!produitExistsInFacture) {
                        factureDao.addProduitsFacture(factureId, produit.getProduit_id(), quantite);
                        onLoadClientsButtonClickClient();
                        onLoadProduitsButtonClick();
                        onLoadFacturesButtonClick();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Product Already Exists", "Product with ID " + produit.getProduit_id() + " already exists in facture " + factureId);
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Quantity Exceeded", "Quantity selected is greater than the quantity in stock for product " + produit.getNom());
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Product Not Found", "Product with name " + produitName + " does not exist.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "SQL Error", e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void handleAddFactureClick() {
        // Create a new stage for the popup
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);

        try {
            // Load the FXML for the popup
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/mediatek/add_facture_popup.fxml"));
            AnchorPane popupContent = loader.load();

            // Set up the stage
            popupStage.setScene(new Scene(popupContent));
            popupStage.setTitle("Ajouter Facture");
            popupStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
