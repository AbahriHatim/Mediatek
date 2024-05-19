package com.example.mediatek;

public class View {

    private int codeClient;
    private String nomClient;
    private double chiffreDAffaires;
    private String categorieClient;

    public View(int codeClient, String nomClient, double chiffreDAffaires, String categorieClient) {
        this.codeClient = codeClient;
        this.nomClient = nomClient;
        this.chiffreDAffaires = chiffreDAffaires;
        this.categorieClient = categorieClient;
    }

    // Getter methods
    public int getCodeClient() {
        return codeClient;
    }

    public String getNomClient() {
        return nomClient;
    }

    public double getChiffreDAffaires() {
        return chiffreDAffaires;
    }

    public String getCategorieClient() {
        return categorieClient;
    }

    // Setter methods
    public void setCodeClient(int codeClient) {
        this.codeClient = codeClient;
    }

    public void setNomClient(String nomClient) {
        this.nomClient = nomClient;
    }

    public void setChiffreDAffaires(double chiffreDAffaires) {
        this.chiffreDAffaires = chiffreDAffaires;
    }

    public void setCategorieClient(String categorieClient) {
        this.categorieClient = categorieClient;
    }
}
