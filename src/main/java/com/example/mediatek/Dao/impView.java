package com.example.mediatek.Dao;

import com.example.mediatek.Client;
import com.example.mediatek.DataBaseConnection;
import com.example.mediatek.View;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class impView {
    private Connection connection;

    public impView() {
        connection = DataBaseConnection.getConnection();
    }

    public List<Client> listerClient() {
        List<Client> clients = new ArrayList<>();
        try {
            if (connection != null) {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM CLIENTS");

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

    public List<View> listerView(int code) {
        List<View> views = new ArrayList<>();
        try {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM V_CHIFFRE_AFFAIRE WHERE code_client = ?");
                preparedStatement.setInt(1, code);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    int code_client = resultSet.getInt("CODE_CLIENT");
                    String nom_client = resultSet.getString("NOM_CLIENT");
                    double chiffreAffaires = resultSet.getDouble("CHIFFRE_D_AFFAIRES");
                    String categorie_client = resultSet.getString("CATEGORIE_CLIENT");
                    views.add(new View(code_client, nom_client, chiffreAffaires, categorie_client));
                }
                resultSet.close();
                preparedStatement.close();
            } else {
                System.out.println("Database connection is null.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return views;
    }
}
