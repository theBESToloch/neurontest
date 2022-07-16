package com.test.UIControllers;

import com.test.context.ApplicationContext;
import com.test.context.ButtonClickState;
import com.test.context.EventHandler;
import com.test.context.EventHandlerRegistrar;
import com.test.context.MouseEventCode;
import com.test.data.NeuronGraph;
import com.test.enums.NeuronTypes;
import com.test.events.LoadModelEvent;
import com.test.events.NeedUpdateCanvasEvent;
import com.test.events.ShowModelLoadWindowEvent;
import com.test.persistence.entities.NNPreview;
import com.test.persistence.services.NNDescriptionService;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.test.context.MouseEventCode.MOUSE_DRAGGED;
import static com.test.context.MouseEventCode.MOUSE_PRESSED;
import static com.test.context.MouseEventCode.MOUSE_RELEASED;
import static com.test.handlers.AddNeuronEventHandler.ADD_NEURON_CODE;
import static com.test.handlers.NeuronMoveEventHandler.NEURON_MOVE_CODE;
import static com.test.handlers.RemoveNeuronEventHandler.REMOVE_NEURON_CODE;
import static com.test.handlers.SelectNeuronEventHandler.SELECT_NEURON_CODE;
import static javafx.scene.input.KeyCode.ALT;
import static javafx.scene.input.KeyCode.CONTROL;
import static javafx.scene.input.KeyCode.DELETE;

@Slf4j
@Component
public class CanvasWindowController implements Initializable {

    public Canvas canvas;
    public ScrollPane scrollPane;
    public ContextMenu canvasContextMenu;

    private final ApplicationContext.CanvasWindowState state;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final NNDescriptionService nnDescriptionService;
    private final ButtonClickState buttonClickState;

    public CanvasWindowController(ApplicationContext.CanvasWindowState state,
                                  ApplicationEventPublisher applicationEventPublisher,
                                  NNDescriptionService nnDescriptionService,
                                  ButtonClickState buttonClickState,
                                  EventHandlerRegistrar handlerRegistrar,
                                  @Qualifier("eventHandlers") Map<String, EventHandler> eventHandlerMap
    ) {
        this.state = state;
        this.applicationEventPublisher = applicationEventPublisher;
        this.nnDescriptionService = nnDescriptionService;
        this.buttonClickState = buttonClickState;

        handlerRegistrar.register(new KeyCode[]{ALT}, new MouseEventCode[]{MOUSE_PRESSED, MOUSE_DRAGGED},
                eventHandlerMap.get(NEURON_MOVE_CODE));
        handlerRegistrar.register(null, new MouseEventCode[]{MOUSE_PRESSED, MOUSE_RELEASED},
                eventHandlerMap.get(ADD_NEURON_CODE));
        handlerRegistrar.register(new KeyCode[]{CONTROL}, new MouseEventCode[]{MOUSE_PRESSED, MOUSE_RELEASED},
                eventHandlerMap.get(SELECT_NEURON_CODE));
        handlerRegistrar.register(new KeyCode[]{DELETE}, null,
                eventHandlerMap.get(REMOVE_NEURON_CODE));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        MenuItem saveButton = new MenuItem("Сохранить");
        saveButton.setOnAction(this::onContextSaveButtonMouseClick);

        MenuItem loadButton = new MenuItem("Загрузить");
        loadButton.setOnAction(this::onContextLoadButtonMouseClick);

        canvasContextMenu.getItems().addAll(saveButton, loadButton);
    }

    public void onMousePressed(MouseEvent mouseEvent) {
        buttonClickState.addMouseEvent(mouseEvent);
    }

    public void onMouseReleased(MouseEvent mouseEvent) {
        buttonClickState.addMouseEvent(mouseEvent);
    }

    public void onMouseDragged(MouseEvent mouseEvent) {
        buttonClickState.addMouseEvent(mouseEvent);
    }

    public void onKeyPressed(KeyEvent keyEvent) {
        buttonClickState.addKey(keyEvent.getCode());
    }

    public void onKeyReleased(KeyEvent keyEvent) {
        buttonClickState.removeKey(keyEvent.getCode());
    }

    @EventListener
    public void LoadModelEventListener(LoadModelEvent event) {
        updateNeuronsGraph();
    }

    @EventListener
    public void updateCanvas(NeedUpdateCanvasEvent event) {
        updateNeuronsGraph();
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
        double radius = NeuronGraph.RADIUS;

        GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();
        graphicsContext2D.setFill(Color.WHITE);
        graphicsContext2D.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (NeuronGraph neuronGraph : state.getNeuronGraphList()) {
            graphicsContext2D.setFill(getColor(neuronGraph.getNeuronTypes()));
            graphicsContext2D.fillOval(neuronGraph.getX() - radius, neuronGraph.getY() - radius, radius * 2, radius * 2);

            for (String neuronId : neuronGraph.getInputConnect()) {
                Optional<NeuronGraph> first = state.getNeuronGraphList()
                        .stream()
                        .filter(neuronGraph1 -> neuronGraph1.getId().equals(neuronId))
                        .findFirst();

                graphicsContext2D.strokeLine(first.get().getX(), first.get().getY(), neuronGraph.getX(), neuronGraph.getY());
            }
        }

        graphicsContext2D.setStroke(Color.GRAY);
        graphicsContext2D.setLineDashes(6);
        graphicsContext2D.setLineWidth(2);
        for (NeuronGraph selectNeuron : state.getSelectNeurons()) {
            graphicsContext2D.strokeOval(selectNeuron.getX() - radius, selectNeuron.getY() - radius, radius * 2, radius * 2);
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
            case default -> throw new RuntimeException("Неуказан тип");
        }
    }

    private void onContextSaveButtonMouseClick(ActionEvent mouseEvent) {
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

    private void onContextLoadButtonMouseClick(ActionEvent mouseEvent) {
        canvasContextMenu.hide();
        applicationEventPublisher.publishEvent(new ShowModelLoadWindowEvent());
    }

}
