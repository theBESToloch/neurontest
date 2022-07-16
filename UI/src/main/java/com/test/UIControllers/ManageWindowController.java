package com.test.UIControllers;

import com.test.context.ApplicationContext;
import com.test.data.NeuronGraph;
import com.test.enums.NeuronTypes;
import com.test.services.VectorGeneratorService;
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
import java.util.ResourceBundle;

@Slf4j
@Component
public class ManageWindowController implements Initializable {
    public ChoiceBox<String> choiceBox;
    public Button testNN;
    public Button trainButton;
    public TextField currentError;

    private final ApplicationContext.ManageWindowState state;
    private final VectorGeneratorService vectorGeneratorService;

    public ManageWindowController(ApplicationContext.ManageWindowState state,
                                  VectorGeneratorService vectorGeneratorService) {
        this.state = state;
        this.vectorGeneratorService = vectorGeneratorService;
    }

    public void test(MouseEvent mouseEvent) {
        currentError.setText("");
        double err = 0;
        List<double[]> inputVectors = state.getInputVectors();
        List<double[]> outputVectors = state.getOutputVectors();
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

    public synchronized void onButtonTrainClick(ActionEvent actionEvent) {
        checkVectors();
        List<double[]> inputVectors = state.getInputVectors();
        List<double[]> outputVectors = state.getOutputVectors();
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

    private void checkVectors() {
        List<double[]> inputVectors = state.getInputVectors();
        List<double[]> outputVectors = state.getOutputVectors();

        List<NeuronGraph> neuronGraphList = state.getNeuronGraphList();

        if (inputVectors == null) {
            log.info("Генерирую входные вектора");
            long count = neuronGraphList.stream().filter(ng -> ng.getNeuronTypes() == NeuronTypes.INPUT).count();
            if (count == 0) throw new IllegalArgumentException("zero inputs");
            inputVectors = new ArrayList<>();
            for (int i = 0; i < state.getCount(); i++) {
                double[] vector = vectorGeneratorService.getVector((int) count);
                inputVectors.add(vector);
            }
        }
        if (outputVectors == null) {
            log.info("Генерирую выходные вектора");
            long count = neuronGraphList.stream().filter(ng -> ng.getNeuronTypes() == NeuronTypes.OUTPUT).count();
            if (count == 0) throw new IllegalArgumentException("zero outputs");
            outputVectors = new ArrayList<>();
            for (int i = 0; i < state.getCount(); i++) {
                double[] vector = vectorGeneratorService.getOutputVector((int) count);
                outputVectors.add(vector);
            }
        }
        state.setVectors(inputVectors, outputVectors);
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
