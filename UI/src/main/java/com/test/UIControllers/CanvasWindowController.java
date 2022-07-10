package com.test.UIControllers;

import com.test.context.ApplicationContext;
import com.test.data.NeuronGraph;
import com.test.enums.NeuronTypes;
import com.test.events.LoadModelEvent;
import com.test.events.ShowModelLoadWindowEvent;
import com.test.events.NeuronPropertiesViewEvent;
import com.test.persistence.entities.NNPreview;
import com.test.persistence.services.NNDescriptionService;
import com.test.template.Neuron;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;

@Slf4j
@Component
public class CanvasWindowController implements Initializable {

    public Canvas canvas;
    public ScrollPane scrollPane;
    public ContextMenu canvasContextMenu;

    private final ApplicationContext.CanvasWindowState state;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final NNDescriptionService nnDescriptionService;

    public CanvasWindowController(ApplicationContext.CanvasWindowState state,
                                  ApplicationEventPublisher applicationEventPublisher,
                                  NNDescriptionService nnDescriptionService) {
        this.state = state;
        this.applicationEventPublisher = applicationEventPublisher;
        this.nnDescriptionService = nnDescriptionService;
    }

    public void onMouseClick(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.SECONDARY) {
            return;
        }
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

        double RADIUS = 10;

        if (first.isEmpty()) {
            switch (state.getActionType()) {
                case ADD -> {
                    NeuronGraph addedNeuron = new NeuronGraph(x1, y1, RADIUS, state.getNeuronType());
                    Neuron neuron = state.getNeuronFactory().createNeuron(state.getNeuronType());
                    addedNeuron.setNeuron(neuron.getId());
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
        for (String graphId : neuronGraph.getInputConnect()) {
            state.getNeuronGraphList()
                    .stream()
                    .filter(neuronGraph1 -> neuronGraph1.getId().equals(graphId))
                    .findFirst()
                    .ifPresent(neuronGraph1 -> neuronGraph1.removeFromOutput(neuronGraph.getId()));
        }

        neuronGraph.getInputConnect().clear();

        for (String graphId : neuronGraph.getOutputConnect()) {
            state.getNeuronGraphList()
                    .stream()
                    .filter(neuronGraph1 -> neuronGraph1.getId().equals(graphId))
                    .findFirst()
                    .ifPresent(neuronGraph1 -> neuronGraph1.removeFromInput(neuronGraph.getId()));

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
        if (from.getNeuron() != to.getNeuron()) {
            state.getNeuronFactory().bindNeurons(from.getNeuron(), to.getNeuron());
            from.addOutputNeuronGraph(to.getId());
            to.addInputNeuronGraph(from.getId());
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
            for (String neuronId : neuronGraph.getInputConnect()) {
                Optional<NeuronGraph> first = state.getNeuronGraphList().stream().filter(neuronGraph1 -> neuronGraph1.getId().equals(neuronId)).findFirst();
                graphicsContext2D.strokeLine(first.get().getX(), first.get().getY(), neuronGraph.getX(), neuronGraph.getY());
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

    public void onContextSaveButtonMouseClick(ActionEvent mouseEvent) {
        canvasContextMenu.hide();
        try {
            int width = 480;
            int height = 320;

            double min = Math.min(480 / canvas.getWidth(), 320 / canvas.getHeight());

            WritableImage image = new WritableImage(width, height);

            SnapshotParameters params = new SnapshotParameters();
            params.setTransform(new Scale(min, min));

            canvas.snapshot(params, image);

            RenderedImage renderedImage = SwingFXUtils.fromFXImage(image, null);
            ByteArrayOutputStream imagePreview = new ByteArrayOutputStream();
            ImageIO.write(renderedImage, "png", imagePreview);

            nnDescriptionService.save(state.getNeuronGraphList(), new NNPreview()
                    .setDate(LocalDateTime.now())
                    .setPreviewImage(imagePreview.toByteArray()));

        } catch (IOException e) {
            log.error("Error save image: ", e);
        }
    }

    public void onContextLoadButtonMouseClick(ActionEvent mouseEvent) {
        canvasContextMenu.hide();
        applicationEventPublisher.publishEvent(new ShowModelLoadWindowEvent());
    }

    @EventListener
    public void LoadModelEventListener(LoadModelEvent event) {
        updateNeuronsGraph();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        MenuItem saveButton = new MenuItem("Сохранить");
        saveButton.setOnAction(this::onContextSaveButtonMouseClick);

        MenuItem loadButton = new MenuItem("Загрузить");
        loadButton.setOnAction(this::onContextLoadButtonMouseClick);

        canvasContextMenu.getItems().addAll(saveButton, loadButton);
    }
}
