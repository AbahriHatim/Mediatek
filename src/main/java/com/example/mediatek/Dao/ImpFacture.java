package com.example.mediatek.Dao;

import com.example.mediatek.DataBaseConnection;
import com.example.mediatek.Produit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.ResultSet;
import java.util.List;

public class ImpFacture {

    public void createInvoice(int clientId, Date invoiceDate, List<Produit> produits) {
        Connection connection = null;
        PreparedStatement invoiceStatement = null;
        PreparedStatement itemStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DataBaseConnection.getConnection();
            connection.setAutoCommit(false); // Start transaction

            // Insert the invoice
            String insertInvoiceSQL = "INSERT INTO Factures (facture_id, client_id, fac_date) VALUES (?, ?, ?)";
            invoiceStatement = connection.prepareStatement(insertInvoiceSQL);
            invoiceStatement.setInt(1, clientId);
            invoiceStatement.setDate(2, invoiceDate);
            invoiceStatement.executeUpdate();

            // Get the generated invoice ID
            int invoiceId = getLatestInvoiceId(connection);

            // Insert invoice items
            String insertItemSQL = "INSERT INTO Produits_Facture (facture_id, produit_id, quantite) VALUES (?, ?, ?)";
            itemStatement = connection.prepareStatement(insertItemSQL);
            for (Produit item : produits) {
                itemStatement.setInt(1, invoiceId);
                itemStatement.setInt(2, item.getProduit_id());
                itemStatement.setInt(3, item.getQuantite_en_stock());
                itemStatement.addBatch();
            }
            itemStatement.executeBatch();

            connection.commit(); // Commit transaction
        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback(); // Rollback if an error occurs
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (itemStatement != null) itemStatement.close();
                if (invoiceStatement != null) invoiceStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to get the latest invoice ID
    private int getLatestInvoiceId(Connection connection) throws SQLException {
        String query = "SELECT MAX(facture_id) FROM Factures";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        }
        return 0; // Return 0 if no invoice ID found
    }
}
