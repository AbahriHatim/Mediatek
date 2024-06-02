package com.example.mediatek;

public class DashboardData {
    private int nombreFactures;
    private double montantTotal;
    private int produitsVendus;
    private int nombreClients;

    public int getNombreFactures() {
        return nombreFactures;
    }

    public void setNombreFactures(int nombreFactures) {
        this.nombreFactures = nombreFactures;
    }

    public double getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(double montantTotal) {
        this.montantTotal = montantTotal;
    }

    public int getProduitsVendus() {
        return produitsVendus;
    }

    public void setProduitsVendus(int produitsVendus) {
        this.produitsVendus = produitsVendus;
    }

    public int getNombreClients() {
        return nombreClients;
    }

    public void setNombreClients(int nombreClients) {
        this.nombreClients = nombreClients;
    }
    @Override
    public String toString() {
        return "DashboardData{" +
                "nombreFactures="   + nombreFactures +
                ", montantTotal=" + montantTotal +
                ", produitsVendus=" + produitsVendus +
                ", nombreClients=" + nombreClients +
                '}';
    }
}
