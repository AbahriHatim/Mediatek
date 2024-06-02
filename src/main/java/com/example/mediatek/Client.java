package com.example.mediatek;

public class Client {
    private int client_id;
    private String nom;
    private String adresse;
    private String email;
    private Double telephone;
    private double chiffreDAffaires;
    private String categorie;

    public Client(int client_id, String nom, String adresse, String email, Double telephone) {
        this.client_id = client_id;
        this.nom = nom;
        this.adresse = adresse;
        this.email = email;
        this.telephone = telephone;
    }
    public Client(int client_id, String nom, double chiffreDAffaires, String categorie) {
        this.client_id = client_id;
        this.nom = nom;
        this.chiffreDAffaires = chiffreDAffaires;
        this.categorie = categorie;
    }
    // Getters and setters
    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }
    public double getChiffreDAffaires() {
        return chiffreDAffaires;
    }
    public String getAdresse() {
        return adresse;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Double getTelephone() {
        return telephone;
    }

    public void setTelephone(Double telephone) {
        this.telephone = telephone;
    }

    @Override
    public String toString() {
        return "Client [client_id=" + client_id + ", nom=" + nom + ", adresse=" + adresse + ", email=" + email
                + ", telephone=" + telephone + "]";
    }
}
