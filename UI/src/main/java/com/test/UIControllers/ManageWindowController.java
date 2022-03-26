package com.test.UIControllers;

import com.test.NeuronFactory;
import com.test.data.enums.ActionTypes;
import com.test.enums.NeuronTypes;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class ManageWindowController implements Initializable {
    public static ActionTypes actionType = ActionTypes.ADD;
    public static Color color = Color.BLACK;
    public static NeuronTypes neuronTypes = NeuronTypes.HIDDEN;

    public ChoiceBox<String> choiceBox;
    public Button addNeuron;
    public Button removeNeuron;
    public Button viewNeuron;
    public Button stopButton;
    public Button trainButton;
    public TextField currentError;
    public static volatile boolean isNeedStop = false;
    public static volatile boolean isCanRun = true;

    public void add(MouseEvent mouseEvent) {
        actionType = ActionTypes.ADD;
    }

    public void remove(MouseEvent mouseEvent) {
        actionType = ActionTypes.REMOVE;
    }

    public void view(MouseEvent mouseEvent) {
        actionType = ActionTypes.VIEW;
    }

    public synchronized void onButtonTrainClick(ActionEvent actionEvent) {
        if (isCanRun) {
            isCanRun = false;
            isNeedStop = false;
            trainButton.setDisable(true);
            stopButton.setDisable(false);
            NeuronFactory.trainWithCondition((err) -> {
                        synchronized (currentError) {
                            currentError.setText(String.valueOf(err));
                        }
                        return isNeedStop;
                    },
                    List.of(new double[]{0, 0}, new double[]{0, 1}, new double[]{1, 0}, new double[]{1, 1}),
                    List.of(new double[]{1, 0}, new double[]{1, 0}, new double[]{0, 1}, new double[]{0, 1})
            );
        }
    }

    public synchronized void onButtonStopClick(ActionEvent actionEvent) {
        isCanRun = true;
        isNeedStop = true;
        trainButton.setDisable(false);
        stopButton.setDisable(true);
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
