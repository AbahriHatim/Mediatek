package com.example.mediatek.Controller;

import com.example.mediatek.DailyData;
import com.example.mediatek.DashboardData;
import com.example.mediatek.DataFetcher;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardDataController {

    @FXML
    private Label  nombreFactures;
    @FXML
    private Label montantTotal;
    @FXML
    private Label produitsVendus;
    @FXML
    private Label nombreClients;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private BarChart<String, Number> salesChart;
    @FXML
    private BarChart<String, Number> revenueChart;

    @FXML
    protected void onSearchButtonClick() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (startDate == null || endDate == null) {
            return;
        }

        DashboardData data = DataFetcher.fetchData(startDate, endDate);
        System.out.println("Data fetched: " + data);

        List<DailyData> dailyData = DataFetcher.fetchDailyData(startDate, endDate);
        dailyData.forEach(d -> System.out.println("Daily data: " + d));

        nombreFactures.setText("Nombre de Factures: " + data.getNombreFactures());
        montantTotal.setText("Montant Total: " + data.getMontantTotal());
        produitsVendus.setText("Produits Vendus: " + data.getProduitsVendus());
        nombreClients.setText("Nombre de Clients: " + data.getNombreClients());

        updateBarCharts(dailyData);
    }

    private void updateBarCharts(List<DailyData> dailyData) {

        salesChart.getData().clear();
        revenueChart.getData().clear();

        XYChart.Series<String, Number> salesSeries = new XYChart.Series<>();
        salesSeries.setName("Nombre de Factures");

        XYChart.Series<String, Number> revenueSeries = new XYChart.Series<>();
        revenueSeries.setName("Montant Total");

        // Accumuler les données par jour
        Map<String, Integer> salesMap = new HashMap<>();
        Map<String, Double> revenueMap = new HashMap<>();

        for (DailyData data : dailyData) {
            String date = data.getDate().toString();
            salesMap.put(date, salesMap.getOrDefault(date, 0) + data.getNombreFactures());
            revenueMap.put(date, revenueMap.getOrDefault(date, 0.0) + data.getMontantTotal());
        }

        for (String date : salesMap.keySet()) {
            salesSeries.getData().add(new XYChart.Data<>(date, salesMap.get(date)));
            revenueSeries.getData().add(new XYChart.Data<>(date, revenueMap.get(date)));
        }

        salesChart.getData().add(salesSeries);
        revenueChart.getData().add(revenueSeries);
    }
}
