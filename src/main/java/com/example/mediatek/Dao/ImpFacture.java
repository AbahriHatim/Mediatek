package com.example.mediatek.Dao;

import com.example.mediatek.Client;
import com.example.mediatek.DataBaseConnection;
import com.example.mediatek.Produit;
import com.example.mediatek.ProduitFacture;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ImpFacture {
    private Connection connection;

    public ImpFacture() {
        // Initialize the database connection
        connection = DataBaseConnection.getConnection();
    }

    public List<Client> listerClient() {
        List<Client> clients = new ArrayList<>();
        try {
            // Check if the connection is not null
            if (connection != null) {
                // Use the connection to execute SQL query to fetch clients
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM CLIENTS");

                // Iterate through the result set and create Client objects
                while (resultSet.next()) {
                    int id = resultSet.getInt("client_id");
                    String name = resultSet.getString("nom");
                    String address = resultSet.getString("adresse");
                    String email = resultSet.getString("email");
                    String telephone = resultSet.getString("telephone");
                    clients.add(new Client(id, name, address, email, telephone));
                }
            } else {
                System.out.println("Database connection is null.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clients;
    }

    public List<Produit> listerProduit() {
        List<Produit> produits = new ArrayList<>();
        try {
            // Check if the connection is not null
            if (connection != null) {
                // Use the connection to execute SQL query to fetch produits
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM PRODUITS");

                // Iterate through the result set and create Produit objects
                while (resultSet.next()) {
                    int produit_id = resultSet.getInt("produit_id");
                    String nom = resultSet.getString("nom");
                    String description = resultSet.getString("description");
                    Double prix_unitaire = resultSet.getDouble("prix_unitaire");
                    Integer quantite_en_stock = resultSet.getInt("quantite_en_stock");
                    Produit produit = new Produit(produit_id, nom, description, prix_unitaire, quantite_en_stock);
                    produits.add(produit);
                }
            } else {
                System.out.println("Database connection is null.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produits;
    }

    public int getClientIdByName(String clientName) {
        int clientId = -1;
        try {
            if (connection != null) {
                String query = "SELECT client_id FROM CLIENTS WHERE nom = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, clientName);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    clientId = resultSet.getInt("client_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clientId;
    }

    public Produit getProduitByName(String produitName) {
        Produit produit = null;
        try {
            if (connection != null) {
                String query = "SELECT * FROM PRODUITS WHERE nom = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, produitName);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    int produit_id = resultSet.getInt("produit_id");
                    String nom = resultSet.getString("nom");
                    String description = resultSet.getString("description");
                    Double prix_unitaire = resultSet.getDouble("prix_unitaire");
                    Integer quantite_en_stock = resultSet.getInt("quantite_en_stock");
                    produit = new Produit(produit_id, nom, description, prix_unitaire, quantite_en_stock);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produit;
    }

    public void createInvoice(int clientId, Date invoiceDate, List<ProduitFacture> produitsFacture) {
        PreparedStatement invoiceStatement = null;
        PreparedStatement itemStatement = null;
        ResultSet resultSet = null;

        try {
            connection.setAutoCommit(false);

            Statement seqStatement = connection.createStatement();
            ResultSet seqResultSet = seqStatement.executeQuery("SELECT facture_seq.nextVal FROM dual");
            int invoiceId = 0;
            if (seqResultSet.next()) {
                invoiceId = seqResultSet.getInt(1);
            }

            String insertInvoiceSQL = "INSERT INTO Factures (FACTURE_ID, CLIENT_ID, DATE1) VALUES (?, ?, ?)";
            invoiceStatement = connection.prepareStatement(insertInvoiceSQL);
            invoiceStatement.setInt(1, invoiceId);
            invoiceStatement.setInt(2, clientId);
            java.sql.Date sqlInvoiceDate = new java.sql.Date(invoiceDate.getTime());
            invoiceStatement.setDate(3, sqlInvoiceDate);
            invoiceStatement.executeUpdate();

            String insertItemSQL = "INSERT INTO Produits_Facture (FACTURE_ID, produit_id, quantite) VALUES (?, ?, ?)";
            itemStatement = connection.prepareStatement(insertItemSQL);

            for (ProduitFacture produitFacture : produitsFacture) {
                itemStatement.setInt(1, invoiceId);
                itemStatement.setInt(2, produitFacture.getProduit_id());
                itemStatement.setInt(3, produitFacture.getQuantite());
                itemStatement.addBatch();
            }
            itemStatement.executeBatch();


            connection.commit();
        } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

