package com.example.mediatek.Contoller;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class IndexController {


    @FXML
    private AnchorPane contentPane;

    // Afficher la vue Facture
    @FXML
    public void showFacture() {
        loadFXML("facture.fxml");
    }

    // Afficher la vue Client
    @FXML
    public void showClient() {
        loadFXML("client.fxml");
    }

    // Afficher la vue Produit
    @FXML
    public void showProduit() {
        loadFXML("produit.fxml");
    }


    private void loadFXML(String fxmlFile) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
            AnchorPane newPane = fxmlLoader.load();
            contentPane.getChildren().setAll(newPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

