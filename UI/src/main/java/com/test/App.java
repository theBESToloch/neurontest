package com.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/CanvasWindow.fxml"));
        Scene scene = new Scene(root, 480, 320);
        stage.setScene(scene);

        final ScrollPane scrollPane = (ScrollPane) root;
        scrollPane.setStyle("-fx-background-color: white");

        Canvas canvas = (Canvas) scrollPane.getContent();
        canvas.setWidth(scene.getWidth() * 2);
        canvas.setHeight(scene.getHeight() * 2);
        stage.show();

        Stage manageWindow = new Stage();
        manageWindow.initOwner(stage);
        Parent manageWindowRoot = FXMLLoader.load(getClass().getResource("/ManageWindow.fxml"));
        Scene manageWindowScene = new Scene(manageWindowRoot);
        manageWindow.setScene(manageWindowScene);
        manageWindow.setX(stage.getX() + stage.getWidth());
        manageWindow.setY(stage.getY());
        manageWindow.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
