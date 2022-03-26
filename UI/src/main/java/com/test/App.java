package com.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    public static Stage canvasStage;
    public static Stage manageStage;
    public static Stage propertiesStage;

    @Override
    public void start(Stage canvasStage) throws IOException {

        ScrollPane scrollPane = FXMLLoader.load(getClass().getResource("/CanvasWindow.fxml"));
        Scene scene = new Scene(scrollPane, 480, 320);
        canvasStage.setScene(scene);

        Canvas canvas = (Canvas) scrollPane.getContent();
        canvas.setWidth(scrollPane.getWidth());
        scrollPane.widthProperty().addListener(((observable, oldValue, newValue) -> canvas.setWidth((Double) newValue)));
        canvas.setHeight(scrollPane.getHeight());
        scrollPane.heightProperty().addListener(((observable, oldValue, newValue) -> canvas.setHeight((Double) newValue)));

        Stage manageStage = new Stage();
        manageStage.initOwner(canvasStage);

        AnchorPane manage = FXMLLoader.load(getClass().getResource("/ManageWindow.fxml"));
        Scene manageScene = new Scene(manage, 200, 320);
        manageStage.setScene(manageScene);

        canvasStage.setOnShown((window) -> {
            Stage source = (Stage) window.getSource();
            manageStage.setX(source.getX() - manageScene.getWidth());
            manageStage.setY(source.getY());
        });

        Stage propertiesStage = new Stage();
        propertiesStage.initOwner(canvasStage);

        AnchorPane properties = FXMLLoader.load(getClass().getResource("/NeuronProperties.fxml"));
        Scene propertiesScene = new Scene(properties, 283, 320);
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
