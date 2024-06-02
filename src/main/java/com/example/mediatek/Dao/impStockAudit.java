package com.example.mediatek.Dao;

import com.example.mediatek.DataBaseConnection;
import com.example.mediatek.StockAudit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class impStockAudit {
    public List<StockAudit> lister() throws DAOException {
        List<StockAudit> stockAudits = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DataBaseConnection.getConnection();
            String sql = "SELECT * FROM Stock_Audit";
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                StockAudit stockAudit = new StockAudit(
                        resultSet.getInt("audit_id"),
                        resultSet.getDate("date_audit"),
                        resultSet.getInt("produit_id"),
                        resultSet.getInt("stock_restant")
                );
                stockAudits.add(stockAudit);
            }
        } catch (SQLException e) {
            throw new DAOException("Error listing stock audits", e);
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                throw new DAOException("Error closing resources", e);
            }
        }
        return stockAudits;
    }
}
