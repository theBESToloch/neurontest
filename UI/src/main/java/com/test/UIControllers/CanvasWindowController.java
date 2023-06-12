package com.test.UIControllers;

import com.test.common.data.dto.NeuronGraph;
import com.test.common.data.dto.NeuronTypes;
import com.test.context.ApplicationContext;
import com.test.context.ButtonClickState;
import com.test.context.EventDescriptor;
import com.test.context.EventHandlerRegistrar;
import com.test.context.EventQueueHandler;
import com.test.event.LoadModelEvent;
import com.test.event.NeedUpdateCanvasEvent;
import com.test.services.VectorGeneratorService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;

import static com.test.handler.event.AddNeuronEventHandler.ADD_NEURON_CODE;
import static com.test.handler.event.AddSynapseEventHandler.ADD_SYNAPSE_CODE;
import static com.test.handler.event.CanvasOffsetEventHandler.OFFSET_CODE;
import static com.test.handler.event.CanvasScaleEventHandler.SCALE_CODE;
import static com.test.handler.event.NeuronMoveEventHandler.NEURON_MOVE_CODE;
import static com.test.handler.event.RemoveNeuronEventHandler.REMOVE_NEURON_CODE;
import static com.test.handler.event.SelectNeuronEventHandler.SELECT_NEURON_CODE;

@Slf4j
@Component
@RequiredArgsConstructor
public class CanvasWindowController implements Initializable {

    public Canvas canvas;

    public ChoiceBox<String> choiceBox;
    public Button trainButton;
    public Button testNN;
    public TextField currentError;


    private final ApplicationContext.ManageWindowState manageState;
    private final ApplicationContext.CanvasWindowState canvasState;

    private final ButtonClickState buttonClickState;
    private final EventHandlerRegistrar eventHandlerRegistrar;
    private final VectorGeneratorService vectorGeneratorService;


    private boolean needUpdate = false;

    @Qualifier("eventQueueHandler")
    private final Map<String, EventQueueHandler> eventQueueHandler;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        choiceBox.setItems(FXCollections.observableArrayList("входной нейрон", "нейрон", "выходной нейрон"));
        choiceBox.setValue("нейрон");

        choiceBox.setOnAction(event -> {
            switch (choiceBox.getValue()) {
                case "входной нейрон" -> manageState.setNeuronType(NeuronTypes.INPUT);
                case "нейрон" -> manageState.setNeuronType(NeuronTypes.HIDDEN);
                case "выходной нейрон" -> manageState.setNeuronType(NeuronTypes.OUTPUT);
            }
        });

        eventHandlerRegistrar.register(eventQueueHandler.get(ADD_NEURON_CODE));
        eventHandlerRegistrar.register(eventQueueHandler.get(NEURON_MOVE_CODE));
        eventHandlerRegistrar.register(eventQueueHandler.get(SELECT_NEURON_CODE));
        eventHandlerRegistrar.register(eventQueueHandler.get(REMOVE_NEURON_CODE));
        eventHandlerRegistrar.register(eventQueueHandler.get(ADD_SYNAPSE_CODE));

        eventHandlerRegistrar.register(eventQueueHandler.get(SCALE_CODE));
        eventHandlerRegistrar.register(eventQueueHandler.get(OFFSET_CODE));
    }

    public void onMousePressed(MouseEvent event) {
        handleEvent(EventDescriptor.EventType.MOUSE_PRESSED, event);
    }

    public void onMouseReleased(MouseEvent event) {
        handleEvent(EventDescriptor.EventType.MOUSE_RELEASED, event);
    }

    public void onMouseDragged(MouseEvent event) {
        handleEvent(EventDescriptor.EventType.MOUSE_DRAGGED, event);
    }

    public void onMouseMoved(MouseEvent event) {
        handleEvent(EventDescriptor.EventType.MOUSE_MOVED, event);
    }

    public void onScroll(ScrollEvent event) {
        handleEvent(EventDescriptor.EventType.MOUSE_SCROLL, event);
    }

    public void onKeyPressed(KeyEvent event) {
        handleEvent(EventDescriptor.EventType.BUTTON_PRESSED, event);
    }

    public void onKeyReleased(KeyEvent event) {
        handleEvent(EventDescriptor.EventType.BUTTON_RELEASED, event);
    }

    private void handleEvent(EventDescriptor.EventType eventType, InputEvent event) {
        needUpdate = false;
        buttonClickState.addEvent(new EventDescriptor(eventType, event));
        if (needUpdate) {
            updateNeuronsGraph();
        }
    }

    @EventListener
    public void LoadModelEventListener(LoadModelEvent event) {
        updateNeuronsGraph();
    }

    @EventListener
    public void updateCanvas(NeedUpdateCanvasEvent event) {
        needUpdate = true;
    }

    private void updateNeuronsGraph() {
        double scale = canvasState.getScale();
        double xOffset = canvasState.getXOffset();
        double yOffset = canvasState.getYOffset();
        double radius = NeuronGraph.RADIUS;

        GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();
        graphicsContext2D.setFill(Color.WHITE);
        graphicsContext2D.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (NeuronGraph neuronGraph : canvasState.getNeuronGraphList()) {
            graphicsContext2D.setFill(getColor(neuronGraph.getNeuronTypes()));
            graphicsContext2D.fillOval(xOffset + scale * (neuronGraph.getX() - radius),
                    yOffset + scale * (neuronGraph.getY() - radius),
                    scale * radius * 2, scale * radius * 2);

            for (String neuronId : neuronGraph.getInputConnect()) {
                Optional<NeuronGraph> first = canvasState.getNeuronGraphList()
                        .stream()
                        .filter(neuronGraph1 -> neuronGraph1.getId().equals(neuronId))
                        .findFirst();

                graphicsContext2D.strokeLine(
                        xOffset + scale * (first.get().getX()),
                        yOffset + scale * (first.get().getY()),
                        xOffset + scale * (neuronGraph.getX()),
                        yOffset + scale * (neuronGraph.getY()));
            }
        }

        graphicsContext2D.setStroke(Color.GRAY);
        graphicsContext2D.setLineDashes(6);
        graphicsContext2D.setLineWidth(2);
        Set<NeuronGraph> selectNeurons = canvasState.getSelectNeurons();
        for (NeuronGraph selectNeuron : selectNeurons) {
            graphicsContext2D.strokeOval(xOffset + scale * (selectNeuron.getX() - radius),
                    yOffset + scale * (selectNeuron.getY() - radius), scale * radius * 2, scale * radius * 2);
        }

        MouseEvent pressedMouse = canvasState.getPressedMouse();
        MouseEvent currentMouse = canvasState.getCurrentMouse();

        if (pressedMouse != null && currentMouse != null) {
            double x = pressedMouse.getX();
            double y = pressedMouse.getY();
            double x1 = currentMouse.getX();
            double y1 = currentMouse.getY();
            double minX = Math.min(x, x1);
            double minY = Math.min(y, y1);
            double maxX = Math.max(x, x1);
            double maxY = Math.max(y, y1);
            graphicsContext2D.strokeRect(minX, minY, maxX - minX, maxY - minY);
        }
    }

    private Color getColor(NeuronTypes neuronTypes) {
        switch (neuronTypes) {
            case INPUT -> {
                return Color.BLUE;
            }
            case HIDDEN -> {
                return Color.BLACK;
            }
            case OUTPUT -> {
                return Color.GRAY;
            }
            default -> throw new RuntimeException("Неуказан тип");
        }
    }

    public void test(MouseEvent mouseEvent) {
        currentError.setText("");
        double err = 0;
        List<double[]> inputVectors = manageState.getInputVectors();
        List<double[]> outputVectors = manageState.getOutputVectors();
        for (int i = 0; i < inputVectors.size(); i++) {
            double[] calculate = manageState.getNeuronFactory().calculate(inputVectors.get(i));
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
        List<double[]> inputVectors = manageState.getInputVectors();
        List<double[]> outputVectors = manageState.getOutputVectors();
        if (manageState.isTrainButton()) {
            manageState.setTrainButton(false);
            trainButton.setText("Остановить");
            log.info("Тренирую");
            manageState.getNeuronFactory().trainWithCondition((err) -> {
                currentError.setText(String.valueOf(err));
                return manageState.isTrainButton();
            }, inputVectors, outputVectors);
        } else {
            manageState.setTrainButton(true);
            trainButton.setText("Тренировать");
        }
    }

    private void checkVectors() {
        List<double[]> inputVectors = manageState.getInputVectors();
        List<double[]> outputVectors = manageState.getOutputVectors();

        List<NeuronGraph> neuronGraphList = manageState.getNeuronGraphList();

        if (inputVectors == null) {
            log.info("Генерирую входные вектора");
            long count = neuronGraphList.stream().filter(ng -> ng.getNeuronTypes() == NeuronTypes.INPUT).count();
            if (count == 0) throw new IllegalArgumentException("zero inputs");
            inputVectors = new ArrayList<>();
            for (int i = 0; i < manageState.getCount(); i++) {
                double[] vector = vectorGeneratorService.getVector((int) count);
                inputVectors.add(vector);
            }
        }
        if (outputVectors == null) {
            log.info("Генерирую выходные вектора");
            long count = neuronGraphList.stream().filter(ng -> ng.getNeuronTypes() == NeuronTypes.OUTPUT).count();
            if (count == 0) throw new IllegalArgumentException("zero outputs");
            outputVectors = new ArrayList<>();
            for (int i = 0; i < manageState.getCount(); i++) {
                double[] vector = vectorGeneratorService.getOutputVector((int) count);
                outputVectors.add(vector);
            }
        }
        manageState.setVectors(inputVectors, outputVectors);
    }
}
