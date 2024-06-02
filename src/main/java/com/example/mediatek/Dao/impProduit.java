package com.example.mediatek.Dao;

import com.example.mediatek.Client;
import com.example.mediatek.DataBaseConnection;
import com.example.mediatek.Produit;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.*;
import oracle.jdbc.OracleTypes;
public class impProduit implements iProduit {
    private Connection connection;
    public impProduit() {
        connection = DataBaseConnection.getConnection();
    }
    private static final Logger logger = Logger.getLogger(impProduit.class.getName());


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

                // Delete from Produits_Facture if the product is associated with any facture
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

                    // Delete from facture_pdf
                    String deleteFromFacturePdfSql = "DELETE FROM facture_pdfs WHERE id_facture = ?";
                    PreparedStatement deleteFromFacturePdfStatement = connection.prepareStatement(deleteFromFacturePdfSql);
                    deleteFromFacturePdfStatement.setInt(1, orphanedFactureId);
                    deleteFromFacturePdfStatement.executeUpdate();

                    // Delete from Factures
                    String deleteOrphanedFactureSql = "DELETE FROM Factures WHERE facture_id = ?";
                    PreparedStatement deleteOrphanedStatement = connection.prepareStatement(deleteOrphanedFactureSql);
                    deleteOrphanedStatement.setInt(1, orphanedFactureId);
                    deleteOrphanedStatement.executeUpdate();
                }

                // Delete from Produits
                String deleteFromProduitsSql = "DELETE FROM PRODUITS WHERE produit_id = ?";
                statement = connection.prepareStatement(deleteFromProduitsSql);
                statement.setInt(1, produitId);
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected == 0) {
                    throw new DAOException("Product not found with id: " + produitId);
                }

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


        // Méthode pour vérifier si un produit existe déjà dans une facture
        public boolean checkProduitExistsInFacture(int factureId, int produitId) throws SQLException {
            String query = "SELECT COUNT(*) FROM Produits_Facture WHERE facture_id = ? AND produit_id = ?";
            try (Connection   connection = DataBaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, factureId);
                statement.setInt(2, produitId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt(1) > 0;
                    }
                }
            }
            return false;
        }

        // Méthode pour ajouter un produit à une facture
        public void addProduitsFacture(int factureId, int produitId, int quantite) throws SQLException {
            String query = "INSERT INTO Produits_Facture (facture_id, produit_id, quantite) VALUES (?, ?, ?)";
            try (Connection   connection = DataBaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, factureId);
                statement.setInt(2, produitId);
                statement.setInt(3, quantite);
                statement.executeUpdate();
            }
        }

    @Override
    public List<Produit> Recherche(Integer productId, String produitNom) throws DAOException {
        List<Produit> filteredProduit = new ArrayList<>();
        String query = "SELECT * FROM PRODUITS WHERE 1=1";
        List<Object> params = new ArrayList<>();

        if (productId != null) {
            query += " AND produit_id = ?";
            params.add(productId);
        }

        if (produitNom != null && !produitNom.isEmpty()) {
            query += " AND LOWER(NOM) LIKE ?";
            params.add("%" + produitNom.toLowerCase() + "%");
        }

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            // Set parameters for the prepared statement
            for (int i = 0; i < params.size(); i++) {
                statement.setObject(i + 1, params.get(i));
            }

            // Execute the query
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int produit_id = resultSet.getInt("produit_id");
                    String nom = resultSet.getString("nom");
                    String description = resultSet.getString("description");
                    Double prix_unitaire = resultSet.getDouble("prix_unitaire");
                    Integer quantite_en_stock = resultSet.getInt("quantite_en_stock");
                    Produit produit = new Produit(produit_id, nom, description, prix_unitaire, quantite_en_stock);
                    filteredProduit.add(produit);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error searching products", e);
        }

        return filteredProduit;
    }

    public Produit getProduitById(int produitId) throws DAOException {
        Produit produit = null;
        String sql = "SELECT nom, prix_unitaire FROM PRODUITS WHERE produit_id = ?";
        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, produitId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String nom = resultSet.getString("nom");
                    double prix_unitaire = resultSet.getDouble("prix_unitaire");
                    produit = new Produit(produitId, nom, "", prix_unitaire, 0); // Vous pouvez ajuster le constructeur de Produit
                } else {
                    throw new DAOException("Produit non trouvé pour l'ID : " + produitId);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la récupération du produit avec l'ID : " + produitId, e);
        }
        return produit;
    }
    public static double getDiscountedPrice(int productId, double discountPercentage) throws SQLException {
        double discountedPrice = 0.0;
        String sql = "{? = call get_discounted_price(?, ?)}";
        try (Connection connection = DataBaseConnection.getConnection();
             CallableStatement statement = connection.prepareCall(sql)) {
            statement.registerOutParameter(1, OracleTypes.NUMBER);
            statement.setInt(2, productId);
            statement.setDouble(3, discountPercentage);
            statement.execute();
            discountedPrice = statement.getDouble(1);
        }
        return discountedPrice;
    }


}






