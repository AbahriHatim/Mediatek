package com.example.mediatek;

import java.util.Date;

public class StockAudit {
    private int audit_id;
    private Date date_audit;
    private int produit_id;
    private int stock_restant;

    public StockAudit(int audit_id, Date date_audit, int produit_id, int stock_restant) {
        this.audit_id = audit_id;
        this.date_audit = date_audit;
        this.produit_id = produit_id;
        this.stock_restant = stock_restant;
    }

    public int getAudit_id() {
        return audit_id;
    }

    public void setAudit_id(int audit_id) {
        this.audit_id = audit_id;
    }

    public Date getDate_audit() {
        return date_audit;
    }

    public void setDate_audit(Date date_audit) {
        this.date_audit = date_audit;
    }

    public int getProduit_id() {
        return produit_id;
    }

    public void setProduit_id(int produit_id) {
        this.produit_id = produit_id;
    }

    public int getStock_restant() {
        return stock_restant;
    }

    public void setStock_restant(int stock_restant) {
        this.stock_restant = stock_restant;
    }
}
