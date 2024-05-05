package com.example.mediatek.Dao;

import com.example.mediatek.Client;
import com.example.mediatek.DataBaseConnection;
import com.example.mediatek.Produit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class impProduit implements iProduit{
    private Connection connection;

    @Override
        public List<Produit> lister() {
            List<Produit> produits = new ArrayList<>();
            Connection connection = null;
            PreparedStatement statement = null;
            ResultSet resultSet = null;

            try {
                connection = DataBaseConnection.getConnection();
                String sql = "SELECT * FROM produits";
                statement = connection.prepareStatement(sql);
                resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    int produit_id = resultSet.getInt("produit_id");
                    String nom = resultSet.getString("nom");
                    String description = resultSet.getString("description");
                    Double prix_unitaire = resultSet.getDouble("prix_unitaire");
                    Integer quantite_en_stock = resultSet.getInt("quantite_en_stock");
                    Produit produit = new Produit(produit_id, nom,description,prix_unitaire,quantite_en_stock);
                    produits.add(produit);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (resultSet != null) resultSet.close();
                    if (statement != null) statement.close();
                    if (connection != null) connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return produits;
        }

        @Override
        public void ajouter(Produit produit) {
            Connection connection = null;
            PreparedStatement statement = null;

            try {
                connection = DataBaseConnection.getConnection();
                String sql = "INSERT INTO produits (nom, description, prix_unitaire, quantite_en_stock) VALUES (?, ?, ?, ?)";
                statement = connection.prepareStatement(sql);
                statement.setString(1, produit.getNom());
                statement.setString(2, produit.getDescription());
                statement.setDouble(3, produit.getPrix_unitaire());
                statement.setInt(4, produit.getQuantite_en_stock());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (statement != null) statement.close();
                    if (connection != null) connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }


    public void edite(Produit produit) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DataBaseConnection.getConnection();
            String sql = "UPDATE produits SET nom=?, description=?, prix_unitaire=?, quantite_en_stock=? WHERE produit_id=?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, produit.getNom());
            statement.setString(2, produit.getDescription());
            statement.setDouble(3, produit.getPrix_unitaire());
            statement.setInt(4, produit.getQuantite_en_stock());
            statement.setInt(5, produit.getProduit_id());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void supprimer(int produitId) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {connection = DataBaseConnection.getConnection();
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM produits WHERE produit_id=?");
                preparedStatement.setInt(1, produitId);
                preparedStatement.executeUpdate();
            } else {
                System.out.println("Database connection is null.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    }


