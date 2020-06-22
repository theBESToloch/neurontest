package com.test.UIControllers;

import com.test.NeuronFactory;
import com.test.template.Neuron;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.test.UIControllers.ManageWindowController.isAdd;

public class CanvasWindowController {

    public Canvas canvas;

    public ScrollPane scrollPane;

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

        System.out.println("x: " + x1 + ", y: " + y1);

        if (first.isEmpty() && isAdd) {
            NeuronGraph addedNeuron = new NeuronGraph(x1, y1, RADIUS, ManageWindowController.color);
            Neuron neuron = NeuronFactory.createNeuron(ManageWindowController.neuronTypes);
            addedNeuron.setNeuron(neuron);
            addNeuronGraph(addedNeuron);
        } else {
            if (!isAdd && first.isPresent()) {
                NeuronGraph removedNeuron = first.get();
                removeNeuronGraph(removedNeuron);
                NeuronFactory.removeNeuron(removedNeuron.getNeuron());
            }
        }
    }

    private void addNeuronGraph(NeuronGraph neuronGraph) {
        neuronGraphList.add(neuronGraph);
        updateNeuronsGraph();
    }

    private void removeNeuronGraph(NeuronGraph neuronGraph) {
        GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();
        graphicsContext2D.clearRect(canvas.getLayoutX(), canvas.getLayoutY(), canvas.getHeight(), canvas.getWidth());

        neuronGraphList.remove(neuronGraph);
        updateNeuronsGraph();
    }

    private void updateNeuronsGraph() {
        GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();

        for (NeuronGraph neuronGraph : neuronGraphList) {
            graphicsContext2D.setFill(neuronGraph.getColor());
            double radius = neuronGraph.getRadius();
            graphicsContext2D.fillOval(neuronGraph.getX() - radius, neuronGraph.getY() - radius, radius * 2, radius * 2);
        }
        graphicsContext2D.stroke();
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
                Neuron neuronFrom = pressedNeuron.getNeuron();
                Neuron neuronTo = neuronGraph.getNeuron();
                neuronTo.addInputNeurons(List.of(neuronFrom));
                addSynapse(pressedNeuron, neuronGraph);
            }
        });

    }

    private void addSynapse(NeuronGraph from, NeuronGraph to) {
        GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();
        graphicsContext2D.moveTo(from.getX(), from.getY());
        graphicsContext2D.lineTo(to.getX(), to.getY());
        graphicsContext2D.stroke();
    }

}

class NeuronGraph {
    private final double x;
    private final double y;
    private final double radius;
    private final Color color;
    private Neuron neuron;

    public NeuronGraph(double x, double y, double radius, Color color) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.color = color;
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
}
