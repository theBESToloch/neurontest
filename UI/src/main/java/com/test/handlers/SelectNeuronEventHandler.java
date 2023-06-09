package com.test.handlers;

import com.test.common.data.dto.NeuronGraph;
import com.test.context.ApplicationContext;
import com.test.context.EventDescriptor;
import com.test.context.EventQueueHandler;
import com.test.events.NeedUpdateCanvasEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Queue;
import java.util.Set;

import static javafx.scene.input.KeyCode.CONTROL;

@Component
public class SelectNeuronEventHandler implements EventQueueHandler {
    public static final String SELECT_NEURON_CODE = "selectNeuron";

    private final ApplicationContext.CanvasWindowState state;
    private final ApplicationEventPublisher applicationEventPublisher;

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

        double pressedX = pressedMouseEvent.getX();
        double pressedY = pressedMouseEvent.getY();

        double targetPressedX = (pressedX - xOffset) / scale;
        double targetPressedY = (pressedY - yOffset) / scale;

        final Optional<NeuronGraph> neuronPressed = state.getNeuronGraphList()
                .stream()
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

    private KeyEvent controlButtonPressedEvent;
    private MouseEvent pressedMouseEvent;

    private boolean isTriggered(EventDescriptor lastEvent, Queue<EventDescriptor> eventQueue) {
        EventDescriptor.EventType eventType = lastEvent.getEventType();

        switch (eventType){

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
                    pressedMouseEvent = null;
                }
                return false;
            }
            case MOUSE_PRESSED -> {
                if (controlButtonPressedEvent == null) {
                    return false;
                }
                MouseEvent asMouseEvent = lastEvent.getAsMouseEvent();
                if (asMouseEvent.getButton() == MouseButton.PRIMARY) {
                    pressedMouseEvent = asMouseEvent;
                    return true;
                }
            }
            case MOUSE_RELEASED -> {
                if (controlButtonPressedEvent == null) {
                    return false;
                }
                MouseEvent asMouseEvent = lastEvent.getAsMouseEvent();
                if (asMouseEvent.getButton() == MouseButton.PRIMARY) {
                    pressedMouseEvent = null;
                }
            }
        }
        return false;
    }
}
