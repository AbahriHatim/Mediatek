package com.example.mediatek.Dao;

import com.example.mediatek.DataBaseConnection;
import com.example.mediatek.Produit;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class impProduit implements iProduit {
    private Connection connection;

    @Override
    public List<Produit> lister() throws DAOException {
        List<Produit> produits = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DataBaseConnection.getConnection();
            String sql = "SELECT * FROM PRODUITS";
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int produit_id = resultSet.getInt("produit_id");
                String nom = resultSet.getString("nom");
                String description = resultSet.getString("description");
                Double prix_unitaire = resultSet.getDouble("prix_unitaire");
                Integer quantite_en_stock = resultSet.getInt("quantite_en_stock");
                Produit produit = new Produit(produit_id, nom, description, prix_unitaire, quantite_en_stock);
                produits.add(produit);
            }
        } catch (SQLException e) {
            throw new DAOException("Error listing products", e);
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                throw new DAOException("Error closing resources", e);
            }
        }
        return produits;
    }

    @Override
    public void ajouter(Produit produit) throws DAOException {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DataBaseConnection.getConnection();
            PreparedStatement seqStmt = connection.prepareStatement("SELECT PRODUIT_SEQ.NEXTVAL FROM DUAL");
            ResultSet rs = seqStmt.executeQuery();

            int nextId = 0; // Initialiser à 0

            if (rs.next()) {
                nextId = rs.getInt(1); // Obtenir la valeur de la séquence
            }
            String sql = "INSERT INTO PRODUITS (produit_id, nom, description, prix_unitaire, quantite_en_stock) VALUES (?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, nextId);
            statement.setString(2, produit.getNom());
            statement.setString(3, produit.getDescription());
            statement.setDouble(4, produit.getPrix_unitaire());
            statement.setInt(5, produit.getQuantite_en_stock());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error adding product", e);
        } finally {
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                throw new DAOException("Error closing resources", e);
            }
        }
    }

    @Override
    public void edite(Produit produit) throws DAOException {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DataBaseConnection.getConnection();
            String sql = "UPDATE PRODUITS SET nom=?, description=?, prix_unitaire=?, quantite_en_stock=? WHERE produit_id=?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, produit.getNom());
            statement.setString(2, produit.getDescription());
            statement.setDouble(3, produit.getPrix_unitaire());
            statement.setInt(4, produit.getQuantite_en_stock());
            statement.setInt(5, produit.getProduit_id());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error editing product", e);
        } finally {
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                throw new DAOException("Error closing resources", e);
            }
        }
    }

    @Override
    public void supprimer(int produitId) throws DAOException {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DataBaseConnection.getConnection();
            if (connection != null) {
                connection.setAutoCommit(false); // Start transaction

                // Delete from Produits_Facture
                String deleteFromProduitsFactureSql = "DELETE FROM Produits_Facture WHERE produit_id = ?";
                statement = connection.prepareStatement(deleteFromProduitsFactureSql);
                statement.setInt(1, produitId);
                statement.executeUpdate();

                // Find and delete orphaned Factures
                String findOrphanedFacturesSql = "SELECT facture_id FROM Factures f WHERE NOT EXISTS (SELECT 1 FROM Produits_Facture pf WHERE f.facture_id = pf.facture_id)";
                PreparedStatement findOrphanedStatement = connection.prepareStatement(findOrphanedFacturesSql);
                ResultSet orphanedFactures = findOrphanedStatement.executeQuery();

                while (orphanedFactures.next()) {
                    int orphanedFactureId = orphanedFactures.getInt("facture_id");
                    String deleteOrphanedFactureSql = "DELETE FROM Factures WHERE facture_id = ?";
                    PreparedStatement deleteOrphanedStatement = connection.prepareStatement(deleteOrphanedFactureSql);
                    deleteOrphanedStatement.setInt(1, orphanedFactureId);
                    deleteOrphanedStatement.executeUpdate();
                }

                // Delete from Produits
                String deleteFromProduitsSql = "DELETE FROM PRODUITS WHERE produit_id = ?";
                statement = connection.prepareStatement(deleteFromProduitsSql);
                statement.setInt(1, produitId);
                statement.executeUpdate();

                connection.commit(); // Commit transaction
            } else {
                throw new DAOException("Database connection is null.");
            }
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback(); // Rollback transaction on error
                } catch (SQLException ex) {
                    throw new DAOException("Error rolling back transaction", ex);
                }
            }
            throw new DAOException("Error deleting product", e);
        } finally {
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                throw new DAOException("Error closing resources", e);
            }
        }
    }
}
