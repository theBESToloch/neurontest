package com.test.handlers;

import com.test.common.data.dto.NeuronGraph;
import com.test.context.ApplicationContext;
import com.test.context.EventDescriptor;
import com.test.context.EventQueueHandler;
import com.test.events.NeedUpdateCanvasEvent;
import com.test.template.Neuron;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Queue;

import static javafx.scene.input.KeyCode.SHIFT;

@Slf4j
@Component
public class AddNeuronEventHandler implements EventQueueHandler {
    public static final String ADD_NEURON_CODE = "addNeuron";
    private final ApplicationContext.CanvasWindowState state;
    private final ApplicationEventPublisher applicationEventPublisher;

    public AddNeuronEventHandler(ApplicationContext.CanvasWindowState state,
                                 ApplicationEventPublisher applicationEventPublisher) {
        this.state = state;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public String getCode() {
        return ADD_NEURON_CODE;
    }

    @Override
    public void handle(EventDescriptor lastEvent, Queue<EventDescriptor> eventQueue) {
        if (!isTriggered(lastEvent, eventQueue)) {
            return;
        }

        double x = pressedMouseEvent.getX();
        double y = pressedMouseEvent.getY();

        double scale = state.getScale();
        double xOffset = state.getXOffset();
        double yOffset = state.getYOffset();

        double targetX = (x - xOffset) / scale;
        double targetY = (y - yOffset) / scale;

        final Optional<NeuronGraph> neuronPressed = state.getNeuronGraphList()
                .stream()
                .filter(neuronGraph -> neuronGraph.isOccupied(targetX, targetY, 4))
                .findFirst();

        if (neuronPressed.isEmpty()) {
            Neuron neuron = state.getNeuronFactory().createNeuron(state.getNeuronType());

            NeuronGraph addedNeuron = new NeuronGraph(targetX, targetY, state.getNeuronType());
            addedNeuron.setNeuron(neuron.getId());

            state.getNeuronGraphList().add(addedNeuron);

            applicationEventPublisher.publishEvent(NeedUpdateCanvasEvent.INSTANT);
        }

    }

    private KeyEvent shiftButtonPressedEvent;
    private MouseEvent pressedMouseEvent;

    private boolean isTriggered(EventDescriptor lastEvent, Queue<EventDescriptor> eventQueue) {
        EventDescriptor.EventType eventType = lastEvent.getEventType();

        switch (eventType) {
            case BUTTON_PRESSED -> {
                if (shiftButtonPressedEvent != null) {
                    return false;
                }
                KeyEvent asKeyEvent = lastEvent.getAsKeyEvent();
                if (asKeyEvent.getCode() == SHIFT) {
                    shiftButtonPressedEvent = asKeyEvent;
                    return false;
                }
            }
            case BUTTON_RELEASED -> {
                if (shiftButtonPressedEvent == null) {
                    return false;
                }
                KeyEvent asKeyEvent = lastEvent.getAsKeyEvent();
                if (asKeyEvent.getCode() == SHIFT) {
                    shiftButtonPressedEvent = null;
                    pressedMouseEvent = null;
                }
                return false;
            }
            case MOUSE_PRESSED -> {
                if (shiftButtonPressedEvent == null) {
                    return false;
                }
                MouseEvent asMouseEvent = lastEvent.getAsMouseEvent();
                if (asMouseEvent.getButton() == MouseButton.PRIMARY) {
                    pressedMouseEvent = asMouseEvent;
                    return true;
                }
            }
            case MOUSE_RELEASED -> {
                if (shiftButtonPressedEvent == null) {
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
