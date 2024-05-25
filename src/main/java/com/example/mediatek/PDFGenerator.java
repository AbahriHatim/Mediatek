package com.example.mediatek;

import com.example.mediatek.Client;
import com.example.mediatek.ProduitFacture;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.io.*;
import java.util.List;

public class PDFGenerator {

    public byte[] generateInvoicePDF(Client client, List<ProduitFacture> produits) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                float margin = 50;
                float yStart = page.getMediaBox().getHeight() - margin;
                float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
                float rowHeight = 20;
                float cellMargin = 5;

                // Draw client information
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contentStream.newLineAtOffset(margin, yStart);
                contentStream.showText("Client Information:");
                yStart -= rowHeight;
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(0, -rowHeight);
                contentStream.showText("Name: " + client.getNom());
                yStart -= rowHeight;
                contentStream.showText("Address: " + client.getAdresse());
                yStart -= rowHeight;
                contentStream.showText("Email: " + client.getEmail());
                yStart -= rowHeight;
                contentStream.showText("Phone: " + client.getTelephone());
                contentStream.endText();

                // Draw table header
                yStart -= 2 * rowHeight;
                drawRow(contentStream, margin, yStart, tableWidth, rowHeight, cellMargin, true, "Product ID", "Quantity");

                // Draw product rows
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                for (ProduitFacture produit : produits) {
                    yStart -= rowHeight;
                    drawRow(contentStream, margin, yStart, tableWidth, rowHeight, cellMargin, false,
                            String.valueOf(produit.getProduit_id()),
                            String.valueOf(produit.getQuantite()));
                }
            }

            document.save(outputStream);
        }

        return outputStream.toByteArray();
    }

    private void drawRow(PDPageContentStream contentStream, float xStart, float yStart, float width, float rowHeight, float cellMargin, boolean isHeader, String... content) throws IOException {
        PDType1Font font = isHeader ? PDType1Font.HELVETICA_BOLD : PDType1Font.HELVETICA;
        contentStream.setFont(font, 12);

        float yPosition = yStart;
        for (String text : content) {
            float stringWidth = font.getStringWidth(text) / 1000 * 12;
            float cellWidth = width / content.length;
            float xPosition = xStart + cellMargin;
            contentStream.beginText();
            contentStream.newLineAtOffset(xPosition, yPosition);
            contentStream.showText(text);
            contentStream.endText();
            xStart += cellWidth;
        }
    }
}
