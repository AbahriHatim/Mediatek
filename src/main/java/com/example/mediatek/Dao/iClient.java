package com.example.mediatek.Dao;

import com.example.mediatek.Client;

import java.util.List;

public interface iClient {
    List<Client> lister();
    void ajouter(Client client);
    void edite(Client client);
    void supprimer(int clientid);

}
