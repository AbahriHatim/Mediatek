package com.example.mediatek.Dao;

import com.example.mediatek.Produit;

import java.util.List;

public interface iProduit {
    void ajouter(Produit produit);
    void supprimer(int produitId);
    void edite(Produit produit);
    List<Produit> lister();
}
