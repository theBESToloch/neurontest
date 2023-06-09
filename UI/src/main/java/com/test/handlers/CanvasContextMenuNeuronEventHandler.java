package com.test.handlers;

import com.test.context.ApplicationContext;
import com.test.context.ButtonClickState;
import com.test.context.EventDescriptor;
import com.test.context.EventQueueHandler;
import com.test.events.ShowModelLoadWindowEvent;
import com.test.persistence.entities.NNPreview;
import com.test.persistence.services.NNDescriptionService;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Scale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Queue;

@Slf4j
@Component
public class CanvasContextMenuNeuronEventHandler implements EventQueueHandler {
    public static final String CANVAS_CONTEXT_MENU_NEURON_CODE = "canvasContextMenuNeuron";
    private final ApplicationContext.CanvasWindowState state;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final NNDescriptionService nnDescriptionService;
    private final ContextMenu canvasContextMenu;
    private Canvas canvas;

    public CanvasContextMenuNeuronEventHandler(ApplicationContext.CanvasWindowState state,
                                               ApplicationEventPublisher applicationEventPublisher,
                                               NNDescriptionService nnDescriptionService) {
        this.state = state;
        this.applicationEventPublisher = applicationEventPublisher;
        this.nnDescriptionService = nnDescriptionService;
        canvasContextMenu = new ContextMenu();

        MenuItem saveButton = new MenuItem("Сохранить");
        saveButton.setOnAction(this::onContextSaveButtonMouseClick);

        MenuItem loadButton = new MenuItem("Загрузить");
        loadButton.setOnAction(this::onContextLoadButtonMouseClick);

        canvasContextMenu.getItems().addAll(saveButton, loadButton);

    }

    @Override
    public String getCode() {
        return CANVAS_CONTEXT_MENU_NEURON_CODE;
    }

    @Override
    public void handle(EventDescriptor lastEvent, Queue<EventDescriptor> eventQueue) {
        if (!isTriggered(lastEvent, eventQueue)) {
            return;
        }

        MouseEvent releasedMouseEvent = pressedMouseEvent;
        if (releasedMouseEvent.getButton() == MouseButton.SECONDARY) {
            canvasContextMenu.show(canvas, releasedMouseEvent.getScreenX(), releasedMouseEvent.getScreenY());
        } else if (canvasContextMenu.isShowing()) {
            canvasContextMenu.hide();
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

    private MouseEvent pressedMouseEvent;

    private boolean isTriggered(EventDescriptor lastEvent, Queue<EventDescriptor> eventQueue) {
        EventDescriptor.EventType eventType = lastEvent.getEventType();

        return false;
    }

}
