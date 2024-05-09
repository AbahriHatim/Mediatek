package com.example.mediatek;

public class ProduitFacture {
    private int facture_id;
    private int produit_id;
    private int quantite;

    public ProduitFacture(int facture_id, int produit_id, int quantite) {
        this.facture_id = facture_id;
        this.produit_id = produit_id;
        this.quantite = quantite;
    }


    public int getFacture_id() {
        return facture_id;
    }

    public void setFacture_id(int facture_id) {
        this.facture_id = facture_id;
    }

    public int getProduit_id() {
        return produit_id;
    }

    public void setProduit_id(int produit_id) {
        this.produit_id = produit_id;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    @Override
    public String toString() {
        return "ProduitFacture [facture_id=" + facture_id + ", produit_id=" + produit_id + ", quantite=" + quantite
                + "]";
    }
}
