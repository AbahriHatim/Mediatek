package com.example.mediatek;

import java.time.LocalDate;

public class DailyData {
    private LocalDate date;
    private int nombreFactures;
    private double montantTotal;

    public DailyData(LocalDate date, int nombreFactures, double montantTotal) {
        this.date = date;
        this.nombreFactures = nombreFactures;
        this.montantTotal = montantTotal;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getNombreFactures() {
        return nombreFactures;
    }

    public double getMontantTotal() {
        return montantTotal;
    }
    @Override
    public String toString() {
        return "DailyData{" +
                "date="  + date +
                ", nombreFactures=" + nombreFactures +
                ", montantTotal=" +  montantTotal +
                '}';
    }
}
