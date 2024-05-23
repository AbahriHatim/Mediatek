package com.example.mediatek.Dao;

import com.example.mediatek.Client;

import java.util.List;

public interface iClient {
    List<Client> lister() throws DAOException;
    void ajouter(Client client) throws DAOException;
    void edite(Client client) throws DAOException;
    void supprimer(int clientId) throws DAOException;
    List<Client> Recherche(int clientId,String clientNom) throws  DAOException;
}
