module com.example.mediatek {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.sql;
    requires org.apache.pdfbox;
    requires java.desktop;
    requires ojdbc10;


    opens com.example.mediatek to javafx.fxml;
    exports com.example.mediatek;
    exports com.example.mediatek.Controller;
    opens com.example.mediatek.Controller to javafx.fxml;
}