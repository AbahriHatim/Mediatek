package com.example.mediatek.Controller;

import com.example.mediatek.Dao.DAOException;
import com.example.mediatek.Dao.impProduit;
import com.example.mediatek.DataBaseConnection;
import com.example.mediatek.Produit;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;



    public class AllDemandesController implements Initializable {
        @FXML
        private TableView<Demande> demandesTableView;
        @FXML
        private TableColumn<Demande, Integer> produitIdColumn;
        @FXML
        private TableColumn<Demande, String> produitNameColumn;
        @FXML
        private TableColumn<Demande, String> demandeColumn;

        @Override
        public void initialize(URL url, ResourceBundle resourceBundle) {
            produitIdColumn.setCellValueFactory(new PropertyValueFactory<>("produit_id"));
            produitNameColumn.setCellValueFactory(new PropertyValueFactory<>("produit_nom"));
            demandeColumn.setCellValueFactory(new PropertyValueFactory<>("demande"));

            loadDemandesData();
        }

        private void loadDemandesData() {
            Connection connection = null;
            Statement statement = null;
            ObservableList<Demande> demandesList = FXCollections.observableArrayList();
            try {
                connection = DataBaseConnection.getConnection();
                String sql = "SELECT produit_id, nom FROM Produits";
                statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql);
                while (resultSet.next()) {
                    int produitId = resultSet.getInt("produit_id");
                    String produitNom = resultSet.getString("nom");

                    System.out.println("Produit ID: " + produitId + ", Nom: " + produitNom);

                    String demande = getDemandeForProduct(produitId);

                    System.out.println("Demande for Produit ID " + produitId + ": " + demande);

                    Demande demandeObj = new Demande(produitId, produitNom, demande);
                    demandesList.add(demandeObj);
                }
                demandesTableView.setItems(demandesList);
                System.out.println("Demandes list size: " + demandesList.size());
            } catch (SQLException e) {
                showAlert("Error", "Unable to load demandes data: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            } finally {
                try {
                    if (statement != null) statement.close();
                    if (connection != null) connection.close();
                } catch (SQLException e) {
                    showAlert("Error", "Error closing resources: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        }

        private String getDemandeForProduct(int produitId) {
            Connection connection = null;
            CallableStatement callableStatement = null;
            String demande = "";
            try {
                connection = DataBaseConnection.getConnection();
                String sql = "{ ? = call FonctionDeterminerDemande(?) }";
                callableStatement = connection.prepareCall(sql);
                callableStatement.registerOutParameter(1, Types.VARCHAR);
                callableStatement.setInt(2, produitId);
                callableStatement.execute();
                demande = callableStatement.getString(1);

                System.out.println("Demande for Produit ID " + produitId + ": " + demande);
            } catch (SQLException e) {
                showAlert("Error", "Unable to fetch demande: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            } finally {
                try {
                    if (callableStatement != null) callableStatement.close();
                    if (connection != null) connection.close();
                } catch (SQLException e) {
                    showAlert("Error", "Error closing resources: " + e.getMessage(), Alert.AlertType.ERROR);
                    e.printStackTrace();
                }
            }
            return demande;
        }

        private void showAlert(String title, String message, Alert.AlertType alertType) {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }
    }

