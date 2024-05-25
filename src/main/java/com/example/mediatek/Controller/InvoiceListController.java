package com.example.mediatek.Controller;

import com.example.mediatek.Dao.ImpFacture;
import com.example.mediatek.Invoice;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class InvoiceListController {
    @FXML
    private TableView<Invoice> invoiceTableView;

    @FXML
    private void initialize() {
        TableColumn<Invoice, Long> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Invoice, Long> idFactureColumn = new TableColumn<>("Facture ID");
        idFactureColumn.setCellValueFactory(new PropertyValueFactory<>("idFacture"));

        TableColumn<Invoice, String> pdfFileNameColumn = new TableColumn<>("PDF File Name");
        pdfFileNameColumn.setCellValueFactory(new PropertyValueFactory<>("pdfFileName"));

        TableColumn<Invoice, Void> downloadColumn = new TableColumn<>("Download");
        downloadColumn.setCellFactory(param -> new TableCellWithButtons());

        invoiceTableView.getColumns().addAll(idColumn, idFactureColumn, pdfFileNameColumn, downloadColumn);

        loadInvoices();
    }

    private void loadInvoices() {
        ImpFacture impFacture = new ImpFacture();
        try {
            List<Invoice> invoices = impFacture.getAllInvoices();
            invoiceTableView.getItems().addAll(invoices);
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle error
        }
    }

    private class TableCellWithButtons extends TableCell<Invoice, Void> {
        private final Button viewButton = new Button("View");
        private final Button downloadButton = new Button("Download");

        TableCellWithButtons() {
            viewButton.setOnAction(event -> {
                Invoice invoice = getTableView().getItems().get(getIndex());
                openPDFViewer(invoice);
            });

            downloadButton.setOnAction(event -> {
                Invoice invoice = getTableView().getItems().get(getIndex());
                downloadPDF(invoice);
            });
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                setGraphic(new HBox(viewButton, downloadButton));
            }
        }
    }

    private void openPDFViewer(Invoice invoice) {
        // Implement PDF viewer window
    }

    private void downloadPDF(Invoice invoice) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialFileName(invoice.getPdfFileName());
            File file = fileChooser.showSaveDialog(null);
            if (file != null) {
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(invoice.getPdfFile());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle error
        }
    }
}
