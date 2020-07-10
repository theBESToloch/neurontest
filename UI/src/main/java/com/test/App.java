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

    public static Stage canvasStage;
    public static Stage manageStage;
    public static Stage propertiesStage;

    @Override
    public void start(Stage canvasStage) throws IOException {
        Parent canvasParent = FXMLLoader.load(getClass().getResource("/CanvasWindow.fxml"));
        Scene scene = new Scene(canvasParent, 480, 320);
        canvasStage.setScene(scene);
        final ScrollPane scrollPane = (ScrollPane) canvasParent;
        scrollPane.setStyle("-fx-background-color: white");

        Canvas canvas = (Canvas) scrollPane.getContent();
        canvas.setWidth(scene.getWidth() * 2);
        canvas.setHeight(scene.getHeight() * 2);

        Stage manageStage = new Stage();
        manageStage.initOwner(canvasStage);

        Parent manageParent = FXMLLoader.load(getClass().getResource("/ManageWindow.fxml"));
        Scene manageScene = new Scene(manageParent);
        manageStage.setScene(manageScene);
        manageStage.setX(canvasStage.getX() + canvasStage.getWidth());
        manageStage.setY(canvasStage.getY());

        Stage propertiesStage = new Stage();
        propertiesStage.initOwner(canvasStage);

        Parent propertiesWindowRoot = FXMLLoader.load(getClass().getResource("/NeuronProperties.fxml"));
        Scene propertiesScene = new Scene(propertiesWindowRoot);
        propertiesStage.setScene(propertiesScene);

        App.canvasStage = canvasStage;
        App.manageStage = manageStage;
        App.propertiesStage = propertiesStage;

        canvasStage.show();
        manageStage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}
