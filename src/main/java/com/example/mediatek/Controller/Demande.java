package com.example.mediatek.Controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Demande {
    private final SimpleIntegerProperty produit_id;
    private final SimpleStringProperty produit_nom;
    private final SimpleStringProperty demande;

    public Demande(int produit_id, String produit_nom, String demande) {
        this.produit_id = new SimpleIntegerProperty(produit_id);
        this.produit_nom = new SimpleStringProperty(produit_nom);
        this.demande = new SimpleStringProperty(demande);
    }

    public int getProduit_id() {
        return produit_id.get();
    }

    public String getProduit_nom() {
        return produit_nom.get();
    }

    public String getDemande() {
        return demande.get();
    }
}
