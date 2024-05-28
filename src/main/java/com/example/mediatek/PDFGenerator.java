package com.example.mediatek;

import com.example.mediatek.Client;
import com.example.mediatek.Dao.DAOException;
import com.example.mediatek.Dao.impProduit;
import com.example.mediatek.Produit;
import com.example.mediatek.ProduitFacture;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.io.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PDFGenerator {

    private final impProduit produitDAO = new impProduit();

    public byte[] generateInvoicePDF(Client client, List<ProduitFacture> produits) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Draw olive green rectangle for header
                contentStream.setNonStrokingColor(85 / 255f, 107 / 255f, 47 / 255f); // Olive green color
                contentStream.addRect(0, page.getMediaBox().getHeight() - 100, page.getMediaBox().getWidth(), 100);
                contentStream.fill();

                // Set up fonts and margins
                float margin = 50;
                float yStart = page.getMediaBox().getHeight() - margin - 50;
                float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
                float rowHeight = 20;
                float cellMargin = 5;

                // Draw invoice title and metadata
                contentStream.setNonStrokingColor(1, 1, 1); // White color for text
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
                contentStream.newLineAtOffset(margin, yStart + 60);
                contentStream.showText("FACTURE");
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(page.getMediaBox().getWidth() - margin - 100, yStart + 60);

                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Date : " + new SimpleDateFormat("dd/MM/yy").format(new Date()));
                contentStream.endText();

                yStart -= rowHeight * 3;

                // Draw client information
                yStart = drawClientInfo(contentStream, margin, yStart, rowHeight, client);

                // Draw table header
                yStart -= rowHeight * 2;
                drawTableHeader(contentStream, margin, yStart, tableWidth, rowHeight, cellMargin);

                // Draw product rows
                yStart -= rowHeight;
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                double totalAmount = 0;
                for (ProduitFacture produitFacture : produits) {
                    Produit produit = produitDAO.getProduitById(produitFacture.getProduit_id()); // Assuming you have this method in impProduit

                    if (produit != null) {
                        yStart -= rowHeight;
                        drawRow(contentStream, margin, yStart, tableWidth, rowHeight, cellMargin, false,
                                produit.getNom(),
                                formatCurrency(produit.getPrix_unitaire()),
                                String.valueOf(produitFacture.getQuantite()),
                                formatCurrency(produit.getPrix_unitaire() * produitFacture.getQuantite()));

                        // Calculating total amount
                        totalAmount += produit.getPrix_unitaire() * produitFacture.getQuantite();
                    }
                }

                // Draw total amount
                yStart -= 2 * rowHeight;
                drawTotal(contentStream, margin, yStart, tableWidth, rowHeight, totalAmount);

                // Draw footer
                yStart -= rowHeight * 3;
                drawFooter(contentStream, margin, yStart, page.getMediaBox().getWidth(), rowHeight);
            } catch (DAOException e) {
                throw new RuntimeException(e);
            }

            document.save(outputStream);
        }

        return outputStream.toByteArray();
    }

    private float drawClientInfo(PDPageContentStream contentStream, float margin, float yStart, float rowHeight, Client client) throws IOException {
        contentStream.setNonStrokingColor(85 / 255f, 107 / 255f, 47 / 255f); // Olive green color
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
        contentStream.newLineAtOffset(margin, yStart);
        contentStream.showText("Client");
        contentStream.endText();

        yStart -= rowHeight;
        contentStream.setNonStrokingColor(0, 0, 0); // Black color
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 12);
        contentStream.newLineAtOffset(margin, yStart);
        contentStream.showText("Nom: " + client.getNom());
        contentStream.endText();

        yStart -= rowHeight;
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 12);
        contentStream.newLineAtOffset(margin, yStart);
        contentStream.showText("Email: " + client.getEmail());
        contentStream.endText();

        yStart -= rowHeight;
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 12);
        contentStream.newLineAtOffset(margin, yStart);
        contentStream.showText("Adresse: " + client.getAdresse());
        contentStream.endText();

        yStart -= rowHeight;
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 12);
        contentStream.newLineAtOffset(margin, yStart);
        contentStream.showText("Téléphone: " + client.getTelephone().toString());
        contentStream.endText();

        return yStart - rowHeight;
    }

    private void drawTableHeader(PDPageContentStream contentStream, float xStart, float yStart, float tableWidth, float rowHeight, float cellMargin) throws IOException {
        contentStream.setNonStrokingColor(85 / 255f, 107 / 255f, 47 / 255f); // Olive green color for header
        drawRow(contentStream, xStart, yStart, tableWidth, rowHeight, cellMargin, true,
                "Produit", "Prix", "Quantité", "Montant");
    }

    private void drawRow(PDPageContentStream contentStream, float xStart, float yStart, float width, float rowHeight, float cellMargin, boolean isHeader, String... content) throws IOException {
        PDType1Font font = isHeader ? PDType1Font.HELVETICA_BOLD : PDType1Font.HELVETICA;
        contentStream.setFont(font, 12);

        if (isHeader) {
            contentStream.setNonStrokingColor(85 / 255f, 107 / 255f, 47 / 255f); // Olive green color for header text
        } else {
            contentStream.setNonStrokingColor(0, 0, 0); // Black color for regular rows text
        }

        float cellWidth = width / content.length;
        float nextX = xStart;

        for (String text : content) {
            // Draw cell borders
            contentStream.addRect(nextX, yStart, cellWidth, rowHeight);
            contentStream.stroke();

            // Draw text
            contentStream.beginText();
            contentStream.newLineAtOffset(nextX + cellMargin, yStart + cellMargin);
            contentStream.showText(text);
            contentStream.endText();
            nextX += cellWidth;
        }
    }

    private void drawTotal(PDPageContentStream contentStream, float margin, float yStart, float tableWidth, float rowHeight, double totalAmount) throws IOException {
        PDType1Font font = PDType1Font.HELVETICA_BOLD;
        contentStream.setFont(font, 12);

        // Format total amount as currency
        String formattedTotal = formatCurrency(totalAmount);

        // Draw "Total" label
        float xPosition = margin + tableWidth - font.getStringWidth("Total: " + formattedTotal) / 1000 * 12;
        contentStream.beginText();
        contentStream.newLineAtOffset(xPosition, yStart);
        contentStream.showText("Total: " + formattedTotal);
        contentStream.endText();
    }

    private void drawFooter(PDPageContentStream contentStream, float margin, float yStart, float pageWidth, float rowHeight) throws IOException {
        contentStream.setNonStrokingColor(85 / 255f, 107 / 255f, 47 / 255f); // Olive green color
        contentStream.setFont(PDType1Font.HELVETICA_OBLIQUE, 12);
        contentStream.beginText();
        contentStream.newLineAtOffset((pageWidth - PDType1Font.HELVETICA_OBLIQUE.getStringWidth("Merci pour votre confiance !") / 1000 * 12) / 2, yStart);
        contentStream.showText("Merci pour votre confiance !");
        contentStream.endText();

        yStart -= rowHeight;
        contentStream.setFont(PDType1Font.HELVETICA, 10);
        contentStream.beginText();
        contentStream.newLineAtOffset((pageWidth - PDType1Font.HELVETICA.getStringWidth("contact@mediatek.com") / 1000 * 10) / 2, yStart);
        contentStream.showText("contact@mediatek.com");
        contentStream.endText();
    }

    private String formatCurrency(double amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator(' ');
        DecimalFormat format = new DecimalFormat("#,###.##", symbols);
        return format.format(amount) + " DH";
    }
}
