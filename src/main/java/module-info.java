module org.symphony {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires org.apache.tika.core;
    requires mp3agic;
    requires java.desktop;
    requires java.logging;

    exports org.symphony; // Add this line
    opens org.symphony to javafx.fxml;
    exports org.symphony.models;
    opens org.symphony.models to javafx.fxml;
    exports org.symphony.controller;
    opens org.symphony.controller to javafx.fxml;
    exports org.symphony.service;
    opens org.symphony.service to javafx.fxml; // Keep this for FXML loading
}
