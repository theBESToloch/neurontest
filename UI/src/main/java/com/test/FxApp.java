package com.test;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.net.URL;

public class FxApp extends Application {

    private ConfigurableApplicationContext applicationContext;

    @Override
    public void start(Stage canvasStage) {

        ScrollPane scrollPane = load(getClass().getResource("/CanvasWindow.fxml"));
        Scene scene = new Scene(scrollPane, 480, 320);
        canvasStage.setScene(scene);

        Canvas canvas = (Canvas) scrollPane.getContent();
        canvas.setWidth(scrollPane.getWidth());
        scrollPane.widthProperty().addListener(((observable, oldValue, newValue) -> canvas.setWidth((Double) newValue)));
        canvas.setHeight(scrollPane.getHeight());
        scrollPane.heightProperty().addListener(((observable, oldValue, newValue) -> canvas.setHeight((Double) newValue)));

        Stage manageStage = new Stage();
        manageStage.initOwner(canvasStage);

        AnchorPane manage = load(getClass().getResource("/ManageWindow.fxml"));
        Scene manageScene = new Scene(manage, 200, 320);
        manageStage.setScene(manageScene);

        canvasStage.setOnShown((window) -> {
            Stage source = (Stage) window.getSource();
            manageStage.setX(source.getX() - manageScene.getWidth());
            manageStage.setY(source.getY());
        });

        Stage propertiesStage = new Stage();
        propertiesStage.initOwner(canvasStage);

        AnchorPane properties = load(getClass().getResource("/NeuronProperties.fxml"));
        Scene propertiesScene = new Scene(properties, 200, 320);
        propertiesStage.setScene(propertiesScene);

        canvasStage.show();
        manageStage.show();
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
