module com.beacon.client {
    requires javafx.controls;
    requires javafx.fxml;
            
        requires org.controlsfx.controls;
    requires java.net.http;

    opens com.beacon.client to javafx.fxml;
    exports com.beacon.client;
}