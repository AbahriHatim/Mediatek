package com.example.mediatek.Controller;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class DashboardController {

    @FXML
    private AnchorPane factureView;  // Référence à la vue facture

    @FXML
    private VBox ClientView;  // Référence à la vue client

    @FXML
    private VBox produitView;  // Référence à la vue produit

    // Méthode appelée lors du clic sur le label "Dashboard"
    @FXML
    private void handleDashboardClick() {
        // Logic pour afficher la vue Dashboard
        hideAllViews();
    }

    // Méthode appelée lors du clic sur le label "Client"
    @FXML
    private void handleClientClick() {
        // Logic pour afficher la vue Client
        hideAllViews();
        ClientView.setVisible(true);
    }

    // Méthode appelée lors du clic sur le label "Produit"
    @FXML
    private void handleProduitClick() {
        // Logic pour afficher la vue Produit
        hideAllViews();
        produitView.setVisible(true);
    }

    // Méthode appelée lors du clic sur le label "Facture"
    @FXML
    private void handleFactureClick() {
        // Masquer toutes les vues sauf la vue facture
        factureView.setVisible(true);
        ClientView.setVisible(false); // Assurez-vous que les autres vues sont masquées
        produitView.setVisible(false); // Assurez-vous que les autres vues sont masquées
    }


    private void hideAllViews() {
        if (factureView != null) factureView.setVisible(false);
        if (ClientView != null) ClientView.setVisible(false);
        if (produitView != null) produitView.setVisible(false);
    }
}
