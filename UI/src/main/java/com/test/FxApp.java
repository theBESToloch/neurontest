package com.test;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

public class FxApp extends Application {

    private ConfigurableApplicationContext applicationContext;

    @Override
    public void start(Stage canvasStage) {
        AnchorPane anchorPane = load(getClass().getResource("/CanvasWindow.fxml"));
        Scene scene = new Scene(anchorPane);
        canvasStage.setScene(scene);
        anchorPane.getStyleClass().add("black-theme");

        AnchorPane childAnchorPane = (AnchorPane)anchorPane.getChildren().get(0);
        Canvas canvas = (Canvas) childAnchorPane.getChildren().get(0);
        childAnchorPane.widthProperty().addListener(((observable, oldValue, newValue) -> canvas.setWidth((Double) newValue)));
        childAnchorPane.heightProperty().addListener(((observable, oldValue, newValue) -> canvas.setHeight((Double) newValue)));

        Stage loadStage = new Stage();
        loadStage.initOwner(canvasStage);

        SplitPane load = load(getClass().getResource("/LoadWindow.fxml"));
        Scene loadScene = new Scene(load, 600, 320);
        loadStage.setScene(loadScene);

        canvasStage.show();
    }

    public <T> T load(URL resource) {
        FXMLLoader fxmlLoader = new FXMLLoader(resource);
        fxmlLoader.setControllerFactory((aClass) -> applicationContext.getBean(aClass));
        try {
            return fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void init() {
        applicationContext = new SpringApplicationBuilder(com.test.Application.class).run();
    }

    @Override
    public void stop() {
        applicationContext.close();
        Platform.exit();
    }
}
