module com.beacon.client {
    requires javafx.controls;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.fxmisc.flowless;
    requires org.fxmisc.richtext;

    opens com.beacon.client to javafx.fxml;
    exports com.beacon.client;
}