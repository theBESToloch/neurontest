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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.*;

@Slf4j
@Controller
public class ManageWindowController implements Initializable {
    public static ActionTypes actionType = ActionTypes.ADD;
    public static Color color = Color.BLACK;
    public static NeuronTypes neuronTypes = NeuronTypes.HIDDEN;
    public static volatile boolean isTrainButton = true;

    public ChoiceBox<String> choiceBox;
    public Button addNeuron;
    public Button removeNeuron;
    public Button testNN;
    public Button trainButton;
    public Button view;
    public TextField currentError;

    public void add(MouseEvent mouseEvent) {
        actionType = ActionTypes.ADD;
    }

    public void remove(MouseEvent mouseEvent) {
        actionType = ActionTypes.REMOVE;
    }

    public void onView(ActionEvent actionEvent) {
        actionType = ActionTypes.VIEW;
    }

    public void test(MouseEvent mouseEvent) {
        currentError.setText("");
        double err = 0;
        for (int i = 0; i < inputVectors.size(); i++) {
            double[] calculate = NeuronFactory.calculate(inputVectors.get(i));
            double[] output = outputVectors.get(i);
            log.info("Выборка номер {}: выходной вектор: {}, рассчитанный вектор: {}",
                    i, Arrays.toString(output), Arrays.toString(calculate));
            for (int j = 0; j < calculate.length; j++) {
                err += Math.pow(output[j] - calculate[j], 2);
            }
        }
        currentError.setText(String.valueOf(Math.sqrt(err / inputVectors.size())));
    }

    public static List<double[]> inputVectors = new ArrayList<>();

    public static List<double[]> outputVectors = new ArrayList<>();

    static {
        int inputNeuron = 3;    // количество входных нейронов
        int outputNeuron = 3;   // количество выходных нейронов
        int count = 10;         // количество тестовых векторов

        Random ran = new Random();

        for (int i = 0; i < count; i++) {
            inputVectors.add(getVector(inputNeuron, ran));
            outputVectors.add(getOutputVector(outputNeuron, ran));
        }
        log.info("Сгенерировал вектора.");
    }

    private static double[] getOutputVector(int elementsCount, Random ran) {
        double[] vector = new double[elementsCount];
        for (int in = 0; in < elementsCount; in++) {
            vector[in] = 0;
        }
        vector[ran.nextInt(elementsCount)] = 1;
        return vector;
    }

    private static double[] getVector(int elementsCount, Random ran) {
        double[] vector = new double[elementsCount];
        for (int in = 0; in < elementsCount; in++) {
            vector[in] = ran.nextInt(100);
        }
        return vector;
    }

    public synchronized void onButtonTrainClick(ActionEvent actionEvent) {
        if (isTrainButton) {
            isTrainButton = false;
            trainButton.setText("Остановить");
            log.info("Тренирую");
            NeuronFactory.trainWithCondition((err) -> {
                log.info(String.valueOf(err));
                return isTrainButton;
            }, inputVectors, outputVectors);
        } else {
            isTrainButton = true;
            trainButton.setText("Тренировать");
        }
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
