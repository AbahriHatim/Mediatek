package com.example.mediatek;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DataFetcher {

    public static DashboardData fetchData(LocalDate startDate, LocalDate endDate) {
        DashboardData data = new DashboardData();
        String query = "SELECT COUNT(f.facture_id) AS nombre_factures, "
                + "SUM(pf.quantite * p.prix_unitaire) AS montant_total, "
                + "SUM(pf.quantite) AS produits_vendus, "
                + "COUNT(DISTINCT f.client_id) AS nombre_clients "
                + "FROM Factures f "
                + "JOIN Produits_Facture pf ON f.facture_id = pf.facture_id "
                + "JOIN Produits p ON pf.produit_id = p.produit_id "
                + "WHERE f.fac_date BETWEEN ? AND ?";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setDate(1, java.sql.Date.valueOf(startDate));
            pstmt.setDate(2, java.sql.Date.valueOf(endDate));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                data.setNombreFactures(rs.getInt("nombre_factures"));
                data.setMontantTotal(rs.getDouble("montant_total"));
                data.setProduitsVendus(rs.getInt("produits_vendus"));
                data.setNombreClients(rs.getInt("nombre_clients"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }

    public static List<DailyData> fetchDailyData(LocalDate startDate, LocalDate endDate) {
        List<DailyData> dailyDataList = new ArrayList<>();
        String query = "SELECT fac_date, COUNT(f.facture_id) AS nombre_factures, "
                + "SUM(pf.quantite * p.prix_unitaire) AS montant_total "
                + "FROM Factures f "
                + "JOIN Produits_Facture pf ON f.facture_id = pf.facture_id "
                + "JOIN Produits p ON pf.produit_id = p.produit_id "
                + "WHERE f.fac_date BETWEEN ? AND ? "
                + "GROUP BY fac_date "
                + "ORDER BY fac_date";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setDate(1, java.sql.Date.valueOf(startDate));
            pstmt.setDate(2, java.sql.Date.valueOf(endDate));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                LocalDate date = rs.getDate("fac_date").toLocalDate();
                int nombreFactures = rs.getInt("nombre_factures");
                double montantTotal = rs.getDouble("montant_total");
                dailyDataList.add(new DailyData(date, nombreFactures, montantTotal));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dailyDataList;
    }




}
