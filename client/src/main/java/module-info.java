module client.main {
    requires java.prefs;
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.controls;
    requires org.fxmisc.flowless;
    requires org.fxmisc.richtext;

    exports org.beacon.client;
}