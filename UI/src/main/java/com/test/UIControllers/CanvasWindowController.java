package com.test.UIControllers;

import com.test.FxApp;
import com.test.NeuronFactory;
import com.test.template.Neuron;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.test.UIControllers.ManageWindowController.actionType;

@Slf4j
@Controller
public class CanvasWindowController {

    public Canvas canvas;

    public ScrollPane scrollPane;

    public static NeuronGraph viewNeuron;

    private static final List<NeuronGraph> neuronGraphList = new ArrayList<>();

    private static final double RADIUS = 10;

    public void onMouseClick(MouseEvent mouseEvent) {
        double layoutX = canvas.getLayoutX();
        double layoutY = canvas.getLayoutY();

        double x = mouseEvent.getX();
        double y = mouseEvent.getY();

        double x1 = x - layoutX;
        double y1 = y - layoutY;

        final Optional<NeuronGraph> first = neuronGraphList
                .stream()
                .filter(neuronGraph -> neuronGraph.isOccupied(x1, y1))
                .findFirst();

        log.info("x: " + x1 + ", y: " + y1);

        if (first.isEmpty()) {
            switch (actionType) {
                case ADD -> {
                    NeuronGraph addedNeuron = new NeuronGraph(x1, y1, RADIUS, ManageWindowController.color);
                    Neuron neuron = NeuronFactory.createNeuron(ManageWindowController.neuronTypes);
                    addedNeuron.setNeuron(neuron);
                    addNeuronGraph(addedNeuron);
                }
                case REMOVE, VIEW -> {
                }
            }
        }
        if (first.isPresent()) {
            switch (actionType) {
                case ADD -> {
                }
                case REMOVE -> {
                    NeuronGraph removedNeuron = first.get();
                    removeNeuronGraph(removedNeuron);
                }
                case VIEW -> {
                    viewNeuron = first.get();
                    NeuronPropertiesController.show(viewNeuron);
                    //FxApp.propertiesStage.show();
                }
            }
        }
    }

    private void addNeuronGraph(NeuronGraph neuronGraph) {
        neuronGraphList.add(neuronGraph);

        updateNeuronsGraph();
    }

    private void removeNeuronGraph(NeuronGraph neuronGraph) {
        for (NeuronGraph graph : neuronGraph.getInputConnect()) {
            graph.removeNeuronGraphsFromOutput(neuronGraph);
        }

        neuronGraph.getInputConnect().clear();

        for (NeuronGraph graph : neuronGraph.getOutputConnect()) {
            graph.removeNeuronGraphsFromInput(neuronGraph);
        }
        neuronGraph.getOutputConnect().clear();

        NeuronFactory.removeNeuron(neuronGraph.getNeuron());

        neuronGraphList.remove(neuronGraph);

        updateNeuronsGraph();
    }

    private NeuronGraph pressedNeuron = null;

    public void onMousePressed(MouseEvent mouseEvent) {
        final Optional<NeuronGraph> neuronPressed = neuronGraphList
                .stream()
                .filter(neuronGraph -> neuronGraph.isOccupied(mouseEvent.getX(), mouseEvent.getY()))
                .findFirst();

        neuronPressed.ifPresent(neuronGraph -> pressedNeuron = neuronGraph);
    }

    public void onMouseReleased(MouseEvent mouseEvent) {
        final Optional<NeuronGraph> neuronReleased = neuronGraphList
                .stream()
                .filter(neuronGraph -> neuronGraph.isOccupied(mouseEvent.getX(), mouseEvent.getY()))
                .findFirst();
        neuronReleased.ifPresent(neuronGraph -> {
            if (pressedNeuron != null) {
                addSynapse(pressedNeuron, neuronGraph);
            }
        });
    }

    private void addSynapse(NeuronGraph from, NeuronGraph to) {
        if (from.getNeuron().getId() != to.getNeuron().getId()) {
            NeuronFactory.bindNeurons(from.getNeuron(), to.getNeuron());
            from.addOutputNeuronGraph(to);
            to.addInputNeuronGraph(from);
            updateNeuronsGraph();
        }
    }

    private void updateNeuronsGraph() {
        GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();
        graphicsContext2D.rect(0, 0, canvas.getWidth(), canvas.getHeight());
        graphicsContext2D.setFill(Color.WHITE);
        graphicsContext2D.fill();

        for (NeuronGraph neuronGraph : neuronGraphList) {
            graphicsContext2D.setFill(neuronGraph.getColor());
            double radius = neuronGraph.getRadius();
            graphicsContext2D.fillOval(neuronGraph.getX() - radius, neuronGraph.getY() - radius, radius * 2, radius * 2);
        }

        for (NeuronGraph neuronGraph : neuronGraphList) {
            for (NeuronGraph graph : neuronGraph.getInputConnect()) {
                graphicsContext2D.strokeLine(graph.getX(), graph.getY(), neuronGraph.getX(), neuronGraph.getY());
            }
        }
    }

}

class NeuronGraph {
    private final double x;
    private final double y;
    private final double radius;
    private final Color color;
    private final List<NeuronGraph> outputConnect;
    private final List<NeuronGraph> inputConnect;
    private Neuron neuron;

    public NeuronGraph(double x, double y, double radius, Color color) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.color = color;
        this.outputConnect = new ArrayList<>();
        this.inputConnect = new ArrayList<>();
    }

    public boolean isOccupied(double x, double y) {
        return x > (this.x - radius) && x < (this.x + radius) && y > (this.y - radius) && y < (this.y + radius);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getRadius() {
        return radius;
    }

    public Color getColor() {
        return color;
    }

    public Neuron getNeuron() {
        return neuron;
    }

    public NeuronGraph setNeuron(Neuron neuron) {
        this.neuron = neuron;
        return this;
    }

    public void addOutputNeuronGraph(NeuronGraph neuronGraph) {
        this.outputConnect.add(neuronGraph);
    }

    public void removeNeuronGraphsFromOutput(NeuronGraph neuronGraph) {
        outputConnect.remove(neuronGraph);
    }

    public List<NeuronGraph> getOutputConnect() {
        return outputConnect;
    }

    public void addInputNeuronGraph(NeuronGraph neuronGraph) {
        this.inputConnect.add(neuronGraph);
    }

    public void removeNeuronGraphsFromInput(NeuronGraph neuronGraph) {
        inputConnect.remove(neuronGraph);
    }

    public List<NeuronGraph> getInputConnect() {
        return inputConnect;
    }
}
