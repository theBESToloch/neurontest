package com.test.UIControllers;

import com.test.enums.NeuronTypes;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class ManageWindowController implements Initializable {

    public static boolean isAdd = true;
    public static Color color = Color.BLACK;
    public static NeuronTypes neuronTypes = NeuronTypes.HIDDEN;

    public Button removeFromProgressBax;
    public Button addToProgressBax;
    public ChoiceBox<String> choiceBox;

    public void add(MouseEvent mouseEvent) {
        isAdd = true;
    }

    public void remove(MouseEvent mouseEvent) {
        isAdd = false;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        choiceBox.setItems(FXCollections.observableArrayList("входной нейрон", "нейрон", "выходной нейрон"));
        choiceBox.setValue("нейрон");

        choiceBox.setOnAction(event -> {
            switch (choiceBox.getValue()) {
                case "входной нейрон" -> {
                    color = Color.BLUE;
                    neuronTypes = NeuronTypes.INPUT;
                }
                case "нейрон" -> {
                    color = Color.BLACK;
                    neuronTypes = NeuronTypes.HIDDEN;
                }
                case "выходной нейрон" -> {
                    color = Color.GRAY;
                    neuronTypes = NeuronTypes.OUTPUT;
                }
            }
        });
    }
}
