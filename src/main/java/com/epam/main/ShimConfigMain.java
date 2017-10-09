package com.epam.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import java.io.IOException;

public class ShimConfigMain extends Application {

    final static Logger logger = Logger.getLogger(ShimConfigMain.class);

    @Override
    public void start(Stage stage) throws Exception{
        stage.setTitle("Shims configs loading");

        stage.setScene(
                createScene(
                        loadMainPane()
                )
        );

        stage.show();
    }

    private Pane loadMainPane() throws IOException {
        FXMLLoader loader = new FXMLLoader();

        Pane mainPane = (Pane) loader.load(
                getClass().getResourceAsStream(
                        Navigator.MAIN
                )
        );

        MainController mainController = loader.getController();

        Navigator.setMainController(mainController);
        Navigator.loadScene(Navigator.SCENE_1);

        return mainPane;
    }

    private Scene createScene(Pane mainPane) {
        return new Scene(
                mainPane
        );
    }


    public static void main(String[] args) {
        launch(args);
    }
}