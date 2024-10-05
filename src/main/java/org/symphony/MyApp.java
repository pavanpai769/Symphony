package org.symphony;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.net.URL;
import java.util.Objects;
import java.util.logging.Logger;

public class MyApp extends Application {

    Logger logger = Logger.getLogger(getClass().getName());

    @Override
    public void start(Stage primaryStage) {

        try {
            URL fxmlUrl = getClass().getResource("test.fxml");

            if (fxmlUrl == null) {
                throw new NullPointerException("FXML file not found");
            }

            Parent root = FXMLLoader.load(fxmlUrl);
            Scene scene = new Scene(root);


            URL cssUrl = getClass().getResource("application.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                logger.info("CSS file not found");
            }

            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("assets/logo.png")));


            primaryStage.getIcons().add(icon);

            primaryStage.setScene(scene);
            primaryStage.setTitle("Symphony");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
