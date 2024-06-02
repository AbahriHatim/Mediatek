package com.example.mediatek;

public class Produit {
    private int produit_id;
    private String nom;
    private String description;
    private double prix_unitaire;
    private int quantite_en_stock;
    private double promo_price;

    public Produit(int produit_id, String nom, String description, double prix_unitaire, int quantite_en_stock, double promo_price) {
        this.produit_id = produit_id;
        this.nom = nom;
        this.description = description;
        this.prix_unitaire = prix_unitaire;
        this.quantite_en_stock = quantite_en_stock;
        this.promo_price = promo_price;
    }
    public Produit(int produit_id, String nom, String description, double prix_unitaire, int quantite_en_stock) {
        this.produit_id = produit_id;
        this.nom = nom;
        this.description = description;
        this.prix_unitaire = prix_unitaire;
        this.quantite_en_stock = quantite_en_stock;
    }

    // Getters and setters
    public int getProduit_id() {
        return produit_id;
    }

    public void setProduit_id(int produit_id) {
        this.produit_id = produit_id;
    }

    public double getPromo_price() {
        return promo_price;
    }

    public void setPromo_price(double promo_price) {
        this.promo_price = promo_price;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrix_unitaire() {
        return prix_unitaire;
    }

    public void setPrix_unitaire(double prix_unitaire) {
        this.prix_unitaire = prix_unitaire;
    }

    public int getQuantite_en_stock() {
        return quantite_en_stock;
    }

    public void setQuantite_en_stock(int quantite_en_stock) {
        this.quantite_en_stock = quantite_en_stock;
    }

    @Override
    public String toString() {
        return "Produit [produit_id=" + produit_id + ", nom=" + nom + ", description=" + description + ", prix_unitaire="
                + prix_unitaire + ", quantite_en_stock=" + quantite_en_stock + "]";
    }
}

