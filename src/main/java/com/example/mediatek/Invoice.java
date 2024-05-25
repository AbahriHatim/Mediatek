package com.example.mediatek;


public class Invoice {
    private Long id;
    private Long idFacture;
    private byte[] pdfFile;
    private String pdfFileName;

    // Constructors
    public Invoice() {}
    public Invoice( Long idFacture, byte[] pdfFile, String pdfFileName) {
        this.idFacture = idFacture;
        this.pdfFile = pdfFile;
        this.pdfFileName = pdfFileName;
    }
    public Invoice(Long id, Long idFacture, byte[] pdfFile, String pdfFileName) {
        this.id = id;
        this.idFacture = idFacture;
        this.pdfFile = pdfFile;
        this.pdfFileName = pdfFileName;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdFacture() {
        return idFacture;
    }

    public void setIdFacture(Long idFacture) {
        this.idFacture = idFacture;
    }

    public byte[] getPdfFile() {
        return pdfFile;
    }

    public void setPdfFile(byte[] pdfFile) {
        this.pdfFile = pdfFile;
    }

    public String getPdfFileName() {
        return pdfFileName;
    }

    public void setPdfFileName(String pdfFileName) {
        this.pdfFileName = pdfFileName;
    }
}
