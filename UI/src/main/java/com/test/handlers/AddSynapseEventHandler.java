package com.test.handlers;

import com.test.common.data.dto.NeuronGraph;
import com.test.context.ApplicationContext;
import com.test.context.EventDescriptor;
import com.test.context.EventQueueHandler;
import com.test.events.NeedUpdateCanvasEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Queue;

@Slf4j
@Component
public class AddSynapseEventHandler implements EventQueueHandler {
    public static final String ADD_SYNAPSE_CODE = "addSynapse";
    private final ApplicationContext.CanvasWindowState state;
    private final ApplicationEventPublisher applicationEventPublisher;

    public AddSynapseEventHandler(ApplicationContext.CanvasWindowState state,
                                  ApplicationEventPublisher applicationEventPublisher) {
        this.state = state;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public String getCode() {
        return ADD_SYNAPSE_CODE;
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

        Optional<NeuronGraph> neuronPressed = state.getNeuronGraphList()
                .stream()
                .filter(neuronGraph -> neuronGraph.isOccupied(targetPressedX, targetPressedY, 1))
                .findFirst();

        if (neuronPressed.isEmpty()) {
            return;
        }

        double releasedX = releasedMouseEvent.getX();
        double releasedY = releasedMouseEvent.getY();

        double targetReleasedX = (releasedX - xOffset) / scale;
        double targetReleasedY = (releasedY - yOffset) / scale;

        Optional<NeuronGraph> neuronReleased = state.getNeuronGraphList()
                .stream()
                .filter(neuronGraph -> neuronGraph.isOccupied(targetReleasedX, targetReleasedY, 1))
                .findFirst();

        if (neuronReleased.isEmpty()) {
            return;
        }

        NeuronGraph from = neuronPressed.get();
        NeuronGraph to = neuronReleased.get();
        if (from.getNeuron() != to.getNeuron()) {
            state.getNeuronFactory().bindNeurons(from.getNeuron(), to.getNeuron());
            from.addOutputNeuronGraph(to.getId());
            to.addInputNeuronGraph(from.getId());
            applicationEventPublisher.publishEvent(NeedUpdateCanvasEvent.INSTANT);
        }
    }

    private MouseEvent pressedMouseEvent;
    private MouseEvent releasedMouseEvent;

    private boolean isTriggered(EventDescriptor lastEvent, Queue<EventDescriptor> eventQueue) {
        EventDescriptor.EventType eventType = lastEvent.getEventType();

        switch (eventType) {
            case MOUSE_PRESSED -> {
                MouseEvent asMouseEvent = lastEvent.getAsMouseEvent();
                if (asMouseEvent.getButton() == MouseButton.PRIMARY) {
                    pressedMouseEvent = asMouseEvent;
                    releasedMouseEvent = null;
                    return false;
                }
            }
            case MOUSE_RELEASED -> {
                MouseEvent asMouseEvent = lastEvent.getAsMouseEvent();
                if (asMouseEvent.getButton() == MouseButton.PRIMARY) {
                    releasedMouseEvent = asMouseEvent;
                }
                return true;
            }
        }
        return false;
    }
}
