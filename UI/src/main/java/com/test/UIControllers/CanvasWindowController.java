package com.test.UIControllers;

import com.test.context.ApplicationContext;
import com.test.data.NeuronGraph;
import com.test.enums.NeuronTypes;
import com.test.events.NeuronPropertiesViewEvent;
import com.test.template.Neuron;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;

import java.util.Optional;

@Slf4j
@Controller
public class CanvasWindowController {

    public Canvas canvas;
    public ScrollPane scrollPane;


    private final ApplicationContext.CanvasWindowState state;
    private final ApplicationEventPublisher applicationEventPublisher;

    public CanvasWindowController(ApplicationContext.CanvasWindowState state,
                                  ApplicationEventPublisher applicationEventPublisher) {
        this.state = state;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void onMouseClick(MouseEvent mouseEvent) {
        double layoutX = canvas.getLayoutX();
        double layoutY = canvas.getLayoutY();

        double x = mouseEvent.getX();
        double y = mouseEvent.getY();

        double x1 = x - layoutX;
        double y1 = y - layoutY;

        final Optional<NeuronGraph> first = state.getNeuronGraphList()
                .stream()
                .filter(neuronGraph -> neuronGraph.isOccupied(x1, y1))
                .findFirst();

        log.info("x: " + x1 + ", y: " + y1);

        double RADIUS = 10;

        if (first.isEmpty()) {
            switch (state.getActionType()) {
                case ADD -> {
                    NeuronGraph addedNeuron = new NeuronGraph(x1, y1, RADIUS, state.getNeuronType());
                    Neuron neuron = state.getNeuronFactory().createNeuron(state.getNeuronType());
                    addedNeuron.setNeuron(neuron);
                    addNeuronGraph(addedNeuron);
                }
                case REMOVE, VIEW -> {
                }
            }
        }
        if (first.isPresent()) {
            switch (state.getActionType()) {
                case ADD -> {
                }
                case REMOVE -> {
                    NeuronGraph removedNeuron = first.get();
                    removeNeuronGraph(removedNeuron);
                }
                case VIEW -> {
                    NeuronGraph viewNeuron = first.get();
                    applicationEventPublisher.publishEvent(new NeuronPropertiesViewEvent(viewNeuron));
                }
            }
        }
    }

    private void addNeuronGraph(NeuronGraph neuronGraph) {
        state.getNeuronGraphList().add(neuronGraph);
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

        state.getNeuronFactory().removeNeuron(neuronGraph.getNeuron());

        state.getNeuronGraphList().remove(neuronGraph);

        updateNeuronsGraph();
    }

    public void onMousePressed(MouseEvent mouseEvent) {
        final Optional<NeuronGraph> neuronPressed = state.getNeuronGraphList()
                .stream()
                .filter(neuronGraph -> neuronGraph.isOccupied(mouseEvent.getX(), mouseEvent.getY()))
                .findFirst();

        neuronPressed.ifPresent(state::setPressedNeuron);
    }

    public void onMouseReleased(MouseEvent mouseEvent) {
        final Optional<NeuronGraph> neuronReleased = state.getNeuronGraphList()
                .stream()
                .filter(neuronGraph -> neuronGraph.isOccupied(mouseEvent.getX(), mouseEvent.getY()))
                .findFirst();
        neuronReleased.ifPresent(neuronGraph -> {
            state.setReleasedNeuron(neuronGraph);
            if (state.getPressedNeuron() != null) {
                addSynapse(state.getPressedNeuron(), neuronGraph);
            }
        });
    }

    private void addSynapse(NeuronGraph from, NeuronGraph to) {
        if (from.getNeuron().getId() != to.getNeuron().getId()) {
            state.getNeuronFactory().bindNeurons(from.getNeuron(), to.getNeuron());
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

        for (NeuronGraph neuronGraph : state.getNeuronGraphList()) {
            graphicsContext2D.setFill(getColor(neuronGraph.getNeuronTypes()));
            double radius = neuronGraph.getRadius();
            graphicsContext2D.fillOval(neuronGraph.getX() - radius, neuronGraph.getY() - radius, radius * 2, radius * 2);
        }

        for (NeuronGraph neuronGraph : state.getNeuronGraphList()) {
            for (NeuronGraph graph : neuronGraph.getInputConnect()) {
                graphicsContext2D.strokeLine(graph.getX(), graph.getY(), neuronGraph.getX(), neuronGraph.getY());
            }
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
            case default -> {
                throw new RuntimeException("Неуказан тип");
            }
        }
    }
}
