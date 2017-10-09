package com.epam.main;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

public class MainController {
    @FXML
    private StackPane sceneHolder;

    public void setScene(Node node) {
        sceneHolder.getChildren().setAll(node);
    }


}
