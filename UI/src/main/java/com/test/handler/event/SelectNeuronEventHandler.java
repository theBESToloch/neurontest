package com.test.handler.event;

import com.test.common.data.dto.NeuronGraph;
import com.test.context.ApplicationContext;
import com.test.context.EventDescriptor;
import com.test.context.EventQueueHandler;
import com.test.event.NeedUpdateCanvasEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

import static javafx.scene.input.KeyCode.CONTROL;

@Slf4j
@Component
public class SelectNeuronEventHandler implements EventQueueHandler {
    public static final String SELECT_NEURON_CODE = "selectNeuron";

    private final ApplicationContext.CanvasWindowState state;
    private final ApplicationEventPublisher applicationEventPublisher;

    private boolean isRectSelected = false;
    private KeyEvent controlButtonPressedEvent;

    public SelectNeuronEventHandler(ApplicationContext.CanvasWindowState state,
                                    ApplicationEventPublisher applicationEventPublisher) {
        this.state = state;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public String getCode() {
        return SELECT_NEURON_CODE;
    }

    @Override
    public void handle(EventDescriptor lastEvent, Queue<EventDescriptor> eventQueue) {
        if (!isTriggered(lastEvent, eventQueue)) {
            return;
        }

        double scale = state.getScale();
        double xOffset = state.getXOffset();
        double yOffset = state.getYOffset();

        MouseEvent pressedMouse = state.getPressedMouse();
        double pressedX = pressedMouse.getX();
        double pressedY = pressedMouse.getY();

        double targetPressedX = (pressedX - xOffset) / scale;
        double targetPressedY = (pressedY - yOffset) / scale;

        if (isRectSelected) {
            MouseEvent releasedMouseEvent = lastEvent.getAsMouseEvent();
            double releasedX = releasedMouseEvent.getX();
            double releasedY = releasedMouseEvent.getY();

            double targetReleasedX = (releasedX - xOffset) / scale;
            double targetReleasedY = (releasedY - yOffset) / scale;

            selectRectNeuron(targetPressedX, targetPressedY, targetReleasedX, targetReleasedY);
        } else {
            selectSingleNeuron(targetPressedX, targetPressedY);
        }

        isRectSelected = false;
        state.setCurrentMouse(null);
        state.setPressedMouse(null);

    }

    private void selectRectNeuron(double targetPressedX, double targetPressedY, double targetReleasedX, double targetReleasedY) {
        double minX = Math.min(targetPressedX, targetReleasedX);
        double maxX = Math.max(targetPressedX, targetReleasedX);
        double minY = Math.min(targetPressedY, targetReleasedY);
        double maxY = Math.max(targetPressedY, targetReleasedY);

        List<NeuronGraph> rectSelectNeurons = state.getNeuronGraphList().stream()
                .filter(neuronGraph -> {
                    double x = neuronGraph.getX();
                    double y = neuronGraph.getY();
                    return x > minX && x < maxX && y > minY && y < maxY;
                })
                .toList();
        if (!rectSelectNeurons.isEmpty()) {
            state.getSelectNeurons().addAll(rectSelectNeurons);
            applicationEventPublisher.publishEvent(NeedUpdateCanvasEvent.INSTANT);
        }
    }

    private void selectSingleNeuron(double targetPressedX, double targetPressedY) {
        Optional<NeuronGraph> neuronPressed = state.getNeuronGraphList().stream()
                .filter(neuronGraph -> neuronGraph.isOccupied(targetPressedX, targetPressedY, 1))
                .findFirst();

        Set<NeuronGraph> selectNeurons = state.getSelectNeurons();
        if (neuronPressed.isPresent()) {
            NeuronGraph clickedNeuron = neuronPressed.get();
            boolean contains = selectNeurons.contains(clickedNeuron);
            if (contains) {
                selectNeurons.remove(clickedNeuron);
            } else {
                selectNeurons.add(clickedNeuron);
            }
            applicationEventPublisher.publishEvent(NeedUpdateCanvasEvent.INSTANT);
        } else {
            boolean needUpdate = !selectNeurons.isEmpty();
            state.cleanSelectNeurons();
            if (needUpdate) {
                applicationEventPublisher.publishEvent(NeedUpdateCanvasEvent.INSTANT);
            }
        }
    }


    private boolean isTriggered(EventDescriptor lastEvent, Queue<EventDescriptor> eventQueue) {
        EventDescriptor.EventType eventType = lastEvent.getEventType();

        switch (eventType) {
            case BUTTON_PRESSED -> {
                if (controlButtonPressedEvent != null) {
                    return false;
                }
                KeyEvent asKeyEvent = lastEvent.getAsKeyEvent();
                if (asKeyEvent.getCode() == CONTROL) {
                    controlButtonPressedEvent = asKeyEvent;
                    return false;
                }
            }
            case BUTTON_RELEASED -> {
                if (controlButtonPressedEvent == null) {
                    return false;
                }
                KeyEvent asKeyEvent = lastEvent.getAsKeyEvent();
                if (asKeyEvent.getCode() == CONTROL) {
                    controlButtonPressedEvent = null;
                    state.setPressedMouse(null);
                }
                return false;
            }
            case MOUSE_PRESSED -> {
                if (controlButtonPressedEvent == null) {
                    return false;
                }
                MouseEvent asMouseEvent = lastEvent.getAsMouseEvent();
                if (asMouseEvent.getButton() == MouseButton.PRIMARY) {
                    state.setPressedMouse(asMouseEvent);
                    return false;
                }
            }
            case MOUSE_DRAGGED -> {
                if (controlButtonPressedEvent == null) {
                    return false;
                }
                state.setCurrentMouse(lastEvent.getAsMouseEvent());
                isRectSelected = true;
                applicationEventPublisher.publishEvent(NeedUpdateCanvasEvent.INSTANT);
            }
            case MOUSE_RELEASED -> {
                if (controlButtonPressedEvent == null) {
                    return false;
                }
                MouseEvent asMouseEvent = lastEvent.getAsMouseEvent();
                if (asMouseEvent.getButton() != MouseButton.PRIMARY) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }
}
