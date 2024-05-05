package com.example.mediatek;

import java.sql.Date;

public class Facture {
    private int facture_id;
    private int client_id;
    private Date fac_date;

    public Facture(int facture_id, int client_id, Date fac_date) {
        this.facture_id = facture_id;
        this.client_id = client_id;
        this.fac_date = fac_date;
    }

    // Getters and setters
    public int getFacture_id() {
        return facture_id;
    }

    public void setFacture_id(int facture_id) {
        this.facture_id = facture_id;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public Date getFac_date() {
        return fac_date;
    }

    public void setFac_date(Date fac_date) {
        this.fac_date = fac_date;
    }

    @Override
    public String toString() {
        return "Facture [facture_id=" + facture_id + ", client_id=" + client_id + ", fac_date=" + fac_date + "]";
    }
}

