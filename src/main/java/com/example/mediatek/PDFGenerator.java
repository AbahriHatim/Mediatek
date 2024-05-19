package com.example.mediatek;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.*;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.*;
import java.util.List;

public class PDFGenerator {

    public void generateInvoicePDF(Client client, List<Produit> produits, String filePath) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                float margin = 50;
                float yStart = page.getMediaBox().getHeight() - margin;
                float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
                float tableHeight = margin;
                float rowHeight = 20;
                float cellMargin = 5;

                // Draw header row
                drawRow(contentStream, margin, yStart, tableWidth, rowHeight, cellMargin, true, "Product Name", "Description", "Unit Price", "Quantity");
                yStart -= rowHeight;

                // Draw product rows
                for (Produit produit : produits) {
                    drawRow(contentStream, margin, yStart, tableWidth, rowHeight, cellMargin, false, produit.getNom(), produit.getDescription(), Double.toString(produit.getPrix_unitaire()), Integer.toString(produit.getQuantite_en_stock()));
                    yStart -= rowHeight;
                }

                // Draw client information
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(margin, yStart);
                contentStream.showText("Client Information:");
                contentStream.newLineAtOffset(0, -rowHeight);
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.showText("Name: " + client.getNom());
                contentStream.newLineAtOffset(0, -rowHeight);
                contentStream.showText("Address: " + client.getAdresse());
                // Add more client details as needed
            }

            document.save(filePath);
        }
    }

    private void drawRow(PDPageContentStream contentStream, float xStart, float yStart, float width, float rowHeight, float cellMargin, boolean isHeader, String... content) throws IOException {
        PDType1Font font = isHeader ? PDType1Font.HELVETICA_BOLD : PDType1Font.HELVETICA;
        contentStream.setFont(font, 12);

        float yPosition = yStart;
        for (String text : content) {
            float stringWidth = font.getStringWidth(text) / 1000 * 12;
            float cellWidth = width / content.length;
            float cellHeight = rowHeight;
            float xPosition = xStart + cellMargin;
            if (!isHeader) {
                contentStream.setFont(font, 12);
            }
            contentStream.beginText();
            contentStream.newLineAtOffset(xPosition, yPosition);
            contentStream.showText(text);
            contentStream.endText();
            xStart += cellWidth;
        }
    }
}
