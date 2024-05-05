package com.example.mediatek;

public class Client {
    private int client_id;
    private String nom;
    private String adresse;
    private String email;
    private String telephone;

    public Client(int client_id, String nom, String adresse, String email, String telephone) {
        this.client_id = client_id;
        this.nom = nom;
        this.adresse = adresse;
        this.email = email;
        this.telephone = telephone;
    }

    // Getters and setters
    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAdresse() {
        return adresse;
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

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    @Override
    public String toString() {
        return "Client [client_id=" + client_id + ", nom=" + nom + ", adresse=" + adresse + ", email=" + email
                + ", telephone=" + telephone + "]";
    }
}
