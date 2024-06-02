package com.example.mediatek.Controller;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class DashboardController {

    @FXML
    private AnchorPane factureView;  // Référence à la vue facture

    @FXML
    private VBox ClientView;  // Référence à la vue client

    @FXML
    private VBox produitView;// Référence à la vue produit

    @FXML
    private GridPane dashboardView;  // Modifier le type en GridPane


    // Méthode appelée lors du clic sur le label "Dashboard"
    @FXML
    private void handleDashboardClick() {
        // Logic pour afficher la vue du tableau de bord
        hideAllViews();
        if (dashboardView != null) dashboardView.setVisible(true);
    }




    // Méthode appelée lors du clic sur le label "Client"
    @FXML
    private void handleClientClick() {
        // Logic pour afficher la vue Client
        hideAllViews();
        if (ClientView != null) ClientView.setVisible(true);
    }


    // Méthode appelée lors du clic sur le label "Produit"
    @FXML
    private void handleProduitClick() {
        // Logic pour afficher la vue Produit
        hideAllViews();
        if (produitView != null) produitView.setVisible(true);
    }

    // Méthode appelée lors du clic sur le label "Facture"
    @FXML
    private void handleFactureClick() {
        // Logic pour afficher la vue Facture
        hideAllViews();
        if (factureView != null)   factureView.setVisible(true);
    }

    private void hideAllViews() {
        if (factureView != null) factureView.setVisible(false);
        if (ClientView != null) ClientView.setVisible(false);
        if (produitView != null) produitView.setVisible(false);
        if (dashboardView != null) dashboardView.setVisible(false);
    }
}
