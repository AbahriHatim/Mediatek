package com.example.mediatek.Controller;

import com.example.mediatek.StockAudit;
import com.example.mediatek.Dao.DAOException;
import com.example.mediatek.Dao.impStockAudit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class StockAuditController implements Initializable {
    @FXML
    private TableView<StockAudit> stockAuditTableView;

    @FXML
    private TableColumn<StockAudit, Integer> auditIdColumn;
    @FXML
    private TableColumn<StockAudit, String> dateAuditColumn;
    @FXML
    private TableColumn<StockAudit, Integer> produitIdColumn;
    @FXML
    private TableColumn<StockAudit, Integer> stockRestantColumn;

    private impStockAudit stockAuditDao = new impStockAudit(); // Instantiate your DAO

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize the table columns
        auditIdColumn.setCellValueFactory(new PropertyValueFactory<>("audit_id"));
        dateAuditColumn.setCellValueFactory(new PropertyValueFactory<>("date_audit"));
        produitIdColumn.setCellValueFactory(new PropertyValueFactory<>("produit_id"));
        stockRestantColumn.setCellValueFactory(new PropertyValueFactory<>("stock_restant"));

        // Load the stock audit data
        loadStockAuditData();
    }

    private void loadStockAuditData() {
        try {
            ObservableList<StockAudit> stockAuditList = FXCollections.observableArrayList(stockAuditDao.lister());
            stockAuditTableView.setItems(stockAuditList);
        } catch (DAOException e) {
            showAlert("Error", "Unable to load stock audit data: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // Helper method to show alerts
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
