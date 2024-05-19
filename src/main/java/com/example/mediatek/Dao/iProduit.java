package com.example.mediatek.Dao;

import com.example.mediatek.Produit;

import java.util.List;

public interface iProduit {
    void ajouter(Produit produit) throws DAOException;;
    void supprimer(int produitId) throws DAOException;;
    void edite(Produit produit) throws DAOException;;
    List<Produit> lister() throws DAOException;;
}
