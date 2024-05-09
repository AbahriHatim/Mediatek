package com.example.mediatek.Dao;

import com.example.mediatek.Client;
import com.example.mediatek.DataBaseConnection;

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
    public List<Client> lister() {
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

    @Override
    public void ajouter(Client client) {
        try {
            // Check if the connection is not null
            if (connection != null) {
                PreparedStatement seqStmt = connection.prepareStatement("SELECT CLIENT_SEQ.NEXTVAL FROM DUAL");
                ResultSet rs = seqStmt.executeQuery();

                int nextId = 0; // Initialiser à 0

                if (rs.next()) {
                    nextId = rs.getInt(1); // Obtenir la valeur de la séquence
                }

                // Insérer un nouveau client avec l'identifiant obtenu
                String insertQuery = "INSERT INTO CLIENTS (CLIENT_ID, NOM, ADRESSE, EMAIL, TELEPHONE) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);

                preparedStatement.setInt(1, nextId); // Utiliser l'identifiant généré par la séquence
                preparedStatement.setString(2, client.getNom());
                preparedStatement.setString(3, client.getAdresse());
                preparedStatement.setString(4, client.getEmail());
                preparedStatement.setString(5, client.getTelephone());

                preparedStatement.executeUpdate();
            } else {
                System.out.println("Database connection is null.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void edite(Client client) {
        try {
            // Check if the connection is not null
            if (connection != null) {
                // Use the connection to prepare and execute SQL statements
                PreparedStatement preparedStatement = connection.prepareStatement("UPDATE CLIENTS SET nom=?, adresse=?, email=?, telephone=? WHERE client_id=?");
                preparedStatement.setInt(5, client.getClient_id());

                preparedStatement.setString(1, client.getNom());
                preparedStatement.setString(2, client.getAdresse());
                preparedStatement.setString(3, client.getEmail());
                preparedStatement.setString(4, client.getTelephone());
                preparedStatement.executeUpdate();
            } else {
                System.out.println("Database connection is null.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void supprimer(int clientId) {
        try {
            // Check if the connection is not null
            if (connection != null) {
                // Use the connection to prepare and execute SQL statements
                PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM CLIENTS WHERE client_id=?");
                preparedStatement.setInt(1, clientId);
                preparedStatement.executeUpdate();
            } else {
                System.out.println("Database connection is null.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
