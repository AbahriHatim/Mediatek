package com.example.mediatek.Dao;

import com.example.mediatek.Client;
import com.example.mediatek.DataBaseConnection;
import com.example.mediatek.Dao.DAOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class impClient implements iClient {
    private Connection connection;

    public impClient() {
        // Initialize the database connection
        connection = DataBaseConnection.getConnection();
    }

    @Override
    public List<Client> lister() throws DAOException {
        List<Client> clients = new ArrayList<>();
        String query = "SELECT * FROM Clients";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("client_id");
                String name = resultSet.getString("nom");
                String address = resultSet.getString("adresse");
                String email = resultSet.getString("email");
                Double telephone = resultSet.getDouble("telephone");
                clients.add(new Client(id, name, address, email, telephone));
            }
        } catch (SQLException e) {
            throw new DAOException("Error listing clients", e);
        }
        return clients;
    }

    @Override
    public void ajouter(Client client) throws DAOException {
        String seqQuery = "SELECT client_seq.NEXTVAL FROM DUAL";
        String insertQuery = "INSERT INTO Clients (client_id, nom, adresse, email, telephone) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement seqStmt = connection.prepareStatement(seqQuery);
             ResultSet rs = seqStmt.executeQuery();
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

            if (rs.next()) {
                int nextId = rs.getInt(1);
                preparedStatement.setInt(1, nextId);
                preparedStatement.setString(2, client.getNom());
                preparedStatement.setString(3, client.getAdresse());
                preparedStatement.setString(4, client.getEmail());
                preparedStatement.setDouble(5, client.getTelephone());
                preparedStatement.executeUpdate();
            } else {
                throw new DAOException("Failed to retrieve next client ID");
            }
        } catch (SQLException e) {
            throw new DAOException("Error inserting client", e);
        }
    }

    @Override
    public void edite(Client client) throws DAOException {
        String updateQuery = "UPDATE Clients SET nom = ?, adresse = ?, email = ?, telephone = ? WHERE client_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, client.getNom());
            preparedStatement.setString(2, client.getAdresse());
            preparedStatement.setString(3, client.getEmail());
            preparedStatement.setDouble(4, client.getTelephone());
            preparedStatement.setInt(5, client.getClient_id());
            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated == 0) {
                throw new DAOException("No client found with ID: " + client.getClient_id());
            }
        } catch (SQLException e) {
            throw new DAOException("Error updating client", e);
        }
    }

    public void supprimer(int client_id) throws DAOException {
        PreparedStatement stmtSelectFactures = null;
        PreparedStatement stmtDeleteProduitsFacture = null;
        PreparedStatement stmtDeleteFacturePdf = null;
        PreparedStatement stmtDeleteFactures = null;
        PreparedStatement stmtDeleteClient = null;

        try {
            connection.setAutoCommit(false);

            String selectFacturesSQL = "SELECT facture_id FROM Factures WHERE client_id = ?";
            stmtSelectFactures = connection.prepareStatement(selectFacturesSQL);
            stmtSelectFactures.setInt(1, client_id);
            ResultSet rsFactures = stmtSelectFactures.executeQuery();

            String deleteProduitsFactureSQL = "DELETE FROM Produits_Facture WHERE facture_id = ?";
            stmtDeleteProduitsFacture = connection.prepareStatement(deleteProduitsFactureSQL);

            String deleteFacturePdfSQL = "DELETE FROM facture_pdfs WHERE id_facture = ?";
            stmtDeleteFacturePdf = connection.prepareStatement(deleteFacturePdfSQL);

            while (rsFactures.next()) {
                int facture_id = rsFactures.getInt("facture_id");

                // Delete related records in Produits_Facture
                stmtDeleteProduitsFacture.setInt(1, facture_id);
                stmtDeleteProduitsFacture.executeUpdate();

                // Delete related records in facture_pdf
                stmtDeleteFacturePdf.setInt(1, facture_id);
                stmtDeleteFacturePdf.executeUpdate();
            }

            // Delete related records in Factures
            String deleteFacturesSQL = "DELETE FROM Factures WHERE client_id = ?";
            stmtDeleteFactures = connection.prepareStatement(deleteFacturesSQL);
            stmtDeleteFactures.setInt(1, client_id);
            stmtDeleteFactures.executeUpdate();

            // Delete the client
            String deleteClientSQL = "DELETE FROM Clients WHERE client_id = ?";
            stmtDeleteClient = connection.prepareStatement(deleteClientSQL);
            stmtDeleteClient.setInt(1, client_id);
            stmtDeleteClient.executeUpdate();

            // Commit transaction
            connection.commit();
        } catch (SQLException e) {
            // Rollback transaction on error
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    throw new DAOException("Error rolling back transaction", ex);
                }
            }
            throw new DAOException("Error deleting client", e);
        } finally {
            // Close all statements and result sets
            try {
                if (stmtSelectFactures != null) stmtSelectFactures.close();
                if (stmtDeleteProduitsFacture != null) stmtDeleteProduitsFacture.close();
                if (stmtDeleteFacturePdf != null) stmtDeleteFacturePdf.close();
                if (stmtDeleteFactures != null) stmtDeleteFactures.close();
                if (stmtDeleteClient != null) stmtDeleteClient.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<Client> Recherche(Integer clientId, String clientNom) throws DAOException {
        List<Client> filteredClients = new ArrayList<>();
        String query = "SELECT * FROM Clients WHERE 1=1";
        List<Object> params = new ArrayList<>();

        if (clientId != null) {
            query += " AND client_id = ?";
            params.add(clientId);
        }

        if (clientNom != null && !clientNom.isEmpty()) {
            query += " AND LOWER(nom) LIKE ?";
            params.add("%" + clientNom.toLowerCase() + "%");
        }

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            // Set parameters for the prepared statement
            for (int i = 0; i < params.size(); i++) {
                statement.setObject(i + 1, params.get(i));
            }

            // Execute the query
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("client_id");
                    String name = resultSet.getString("nom");
                    String address = resultSet.getString("adresse");
                    String email = resultSet.getString("email");
                    Double telephone = resultSet.getDouble("telephone");
                    filteredClients.add(new Client(id, name, address, email, telephone));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error searching clients", e);
        }

        return filteredClients;
    }
    public List<Client> getClientDetailsFromView() throws DAOException {
        String query = "SELECT * FROM v_chiffre_affaire";
        List<Client> clients = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("code_client");
                String name = resultSet.getString("nom_client");
                double chiffreDAffaires = resultSet.getDouble("chiffre_d_affaires");
                String categorie = resultSet.getString("categorie_client");

                Client client = new Client(id, name, chiffreDAffaires, categorie);
                clients.add(client);
            }
        } catch (SQLException e) {
            throw new DAOException("Error retrieving client details from view", e);
        }
        return clients;
    }

}
