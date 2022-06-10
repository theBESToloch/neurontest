package com.test.UIControllers;

import com.test.context.ApplicationContext;
import com.test.data.enums.ActionTypes;
import com.test.enums.NeuronTypes;
import com.test.persistence.services.NNDescriptionService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

@Slf4j
@Component
public class ManageWindowController implements Initializable {
    public ChoiceBox<String> choiceBox;
    public Button addNeuron;
    public Button removeNeuron;
    public Button testNN;
    public Button trainButton;
    public Button view;
    public TextField currentError;

    private final ApplicationContext.ManageWindowState state;

    public ManageWindowController(ApplicationContext.ManageWindowState state) {
        this.state = state;
    }

    public void add(MouseEvent mouseEvent) {
        state.setActionType(ActionTypes.ADD);
    }

    public void remove(MouseEvent mouseEvent) {
        state.setActionType(ActionTypes.REMOVE);
    }

    public void onView(ActionEvent actionEvent) {
        state.setActionType(ActionTypes.VIEW);
    }

    public void test(MouseEvent mouseEvent) {
        currentError.setText("");
        double err = 0;
        for (int i = 0; i < inputVectors.size(); i++) {
            double[] calculate = state.getNeuronFactory().calculate(inputVectors.get(i));
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
        if (state.isTrainButton()) {
            state.setTrainButton(false);
            trainButton.setText("Остановить");
            log.info("Тренирую");
            state.getNeuronFactory().trainWithCondition((err) -> {
                log.info(String.valueOf(err));
                return state.isTrainButton();
            }, inputVectors, outputVectors);
        } else {
            state.setTrainButton(true);
            trainButton.setText("Тренировать");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        choiceBox.setItems(FXCollections.observableArrayList("входной нейрон", "нейрон", "выходной нейрон"));
        choiceBox.setValue("нейрон");

        choiceBox.setOnAction(event -> {
            switch (choiceBox.getValue()) {
                case "входной нейрон" -> state.setNeuronType(NeuronTypes.INPUT);
                case "нейрон" -> state.setNeuronType(NeuronTypes.HIDDEN);
                case "выходной нейрон" -> state.setNeuronType(NeuronTypes.OUTPUT);
            }
        });
    }
}
