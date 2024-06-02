package com.example.mediatek.Dao;

import com.example.mediatek.*;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ImpFacture {
    private Connection connection;
    public List<Facture> listerFacture() {
        List<Facture> factures = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DataBaseConnection.getConnection();
            String sql = "SELECT * FROM Factures";
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int facture_id = resultSet.getInt("facture_id");
                int client_id = resultSet.getInt("client_id");
                Date fac_date = resultSet.getDate("fac_date");
                Facture facture = new Facture(facture_id, client_id, (java.sql.Date) fac_date);
                factures.add(facture);
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
        return factures;
    }


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
                    Double telephone = resultSet.getDouble("telephone");
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
                ResultSet resultSet = statement.executeQuery("SELECT * FROM PRODUITS where quantite_en_stock>0");

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

public int createFacture(int clientId, Date invoiceDate) throws SQLException {
    PreparedStatement invoiceStatement = null;
    ResultSet seqResultSet = null;

    try {
        connection.setAutoCommit(false);

        Statement seqStatement = connection.createStatement();
        seqResultSet = seqStatement.executeQuery("SELECT facture_seq.nextVal FROM dual");
        int invoiceId = 0;
        if (seqResultSet.next()) {
            invoiceId = seqResultSet.getInt(1);
        }

        String insertInvoiceSQL = "INSERT INTO Factures (FACTURE_ID, CLIENT_ID, fac_date) VALUES (?, ?, ?)";
        invoiceStatement = connection.prepareStatement(insertInvoiceSQL);
        invoiceStatement.setInt(1, invoiceId);
        invoiceStatement.setInt(2, clientId);
        java.sql.Date sqlInvoiceDate = new java.sql.Date(invoiceDate.getTime());
        invoiceStatement.setDate(3, sqlInvoiceDate);
        invoiceStatement.executeUpdate();

        connection.commit();

        return invoiceId;
    } finally {
        if (seqResultSet != null) {
            seqResultSet.close();
        }
        if (invoiceStatement != null) {
            invoiceStatement.close();
        }
    }

}

    public void addProduitsFacture(int factureId, int produitId, int quantite) throws SQLException {
        PreparedStatement itemStatement = null;

        try {
            String insertItemSQL = "INSERT INTO Produits_Facture (FACTURE_ID, produit_id, quantite) VALUES (?, ?, ?)";
            itemStatement = connection.prepareStatement(insertItemSQL);

            // Définir les valeurs des paramètres
            itemStatement.setInt(1, factureId);
            itemStatement.setInt(2, produitId);
            itemStatement.setInt(3, quantite);
            connection.commit();
            // Ajouter l'instruction à la batch
            itemStatement.addBatch();

            // Exécuter la batch
            itemStatement.executeBatch();
        } finally {
            if (itemStatement != null) {
                itemStatement.close();
            }
        }
    }
    public void editeQuantity(int factureId, int produitId, int newQuantity) throws DAOException {
        PreparedStatement statement = null;
        try {
            String sql = "UPDATE Produits_Facture SET quantite = ? WHERE FACTURE_ID = ? AND produit_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, newQuantity);
            statement.setInt(2, factureId);
            statement.setInt(3, produitId);
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated == 0) {
                throw new DAOException("Failed to update quantity: Product not found in the invoice.");
            }
        } catch (SQLException e) {
            throw new DAOException("Error editing product quantity", e);
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                throw new DAOException("Error closing statement", e);
            }
        }
    }

    public boolean checkProduitExistsInFacture(int factureId, int produitId) throws SQLException {
        boolean exists = false;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            String sql = "SELECT COUNT(*) FROM Produits_Facture WHERE FACTURE_ID = ? AND produit_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, factureId);
            statement.setInt(2, produitId);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                exists = (count > 0);
            }
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
        }

        return exists;
    }
    // In ImpFacture class
    public Client getClientById(int clientId) {
        Client client = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            String sql = "SELECT * FROM CLIENTS WHERE client_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, clientId);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("nom");
                String address = resultSet.getString("adresse");
                String email = resultSet.getString("email");
                Double telephone = resultSet.getDouble("telephone");
                client = new Client(clientId, name, address, email, telephone);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return client;
    }
    public void savePDF(int factureId, String fileName, byte[] pdfData) throws SQLException {
        PreparedStatement seqStmt = connection.prepareStatement("SELECT seq_Invoices.NEXTVAL FROM DUAL");
        ResultSet rs = seqStmt.executeQuery();
        int nextId = 0; // Initialiser à 0

        if (rs.next()) {
            nextId = rs.getInt(1); // Obtenir la valeur de la séquence
        }
        String sql = "INSERT INTO Facture_PDFs (ID,id_facture, pdf_file_name, pdf_file_data) VALUES (?,?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, nextId);
            statement.setLong(2, factureId);
            statement.setString(3, fileName);
            statement.setBytes(4, pdfData);
            statement.executeUpdate();
        }
    }

    public List<Invoice> getAllInvoices() throws SQLException {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT id, id_facture, pdf_file_name, pdf_file_data FROM Facture_PDFs";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long idFacture = resultSet.getLong("id_facture");
                String pdfFileName = resultSet.getString("pdf_file_name");
                Blob pdfBlob = resultSet.getBlob("pdf_file_data");
                byte[] pdfData = pdfBlob.getBytes(1, (int) pdfBlob.length());
                invoices.add(new Invoice(id, idFacture, pdfData, pdfFileName));
            }
        }
        return invoices;
    }
    public File getFacturePDF(int factureId) throws SQLException, IOException {
        String query = "SELECT pdf_file_data, pdf_file_name FROM Facture_PDFs WHERE id_facture = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, factureId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Blob pdfBlob = rs.getBlob("pdf_file_data");
                String pdfFileName = rs.getString("pdf_file_name");

                InputStream inputStream = pdfBlob.getBinaryStream();
                File tempFile = File.createTempFile(pdfFileName, ".pdf");

                try (OutputStream outputStream = new FileOutputStream(tempFile)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }

                return tempFile;
            } else {
                return null;
            }
        }
    }

}

