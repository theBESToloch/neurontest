package com.test.handler.event;

import com.test.common.data.dto.NeuronGraph;
import com.test.context.ApplicationContext;
import com.test.context.EventDescriptor;
import com.test.context.EventQueueHandler;
import com.test.event.NeedUpdateCanvasEvent;
import javafx.scene.input.KeyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.Set;

import static javafx.scene.input.KeyCode.DELETE;

@Component
public class RemoveNeuronEventHandler implements EventQueueHandler {
    public static final String REMOVE_NEURON_CODE = "removeNeuronCode";
    private final ApplicationContext.CanvasWindowState state;
    private final ApplicationEventPublisher applicationEventPublisher;

    public RemoveNeuronEventHandler(ApplicationContext.CanvasWindowState state,
                                    ApplicationEventPublisher applicationEventPublisher) {
        this.state = state;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public String getCode() {
        return REMOVE_NEURON_CODE;
    }

    @Override
    public void handle(EventDescriptor lastEvent, Queue<EventDescriptor> eventQueue) {
        Set<NeuronGraph> neuronGraphs;

        if ((neuronGraphs = state.getSelectNeurons()).isEmpty()
                || !isTriggered(lastEvent, eventQueue)) {
            return;
        }

        for (NeuronGraph neuronGraph : neuronGraphs) {
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
        }

        state.cleanSelectNeurons();

        applicationEventPublisher.publishEvent(NeedUpdateCanvasEvent.INSTANT);
    }

    private boolean isTriggered(EventDescriptor lastEvent, Queue<EventDescriptor> eventQueue) {
        EventDescriptor.EventType eventType = lastEvent.getEventType();

        switch (eventType) {
            case BUTTON_PRESSED -> {
                KeyEvent asKeyEvent = lastEvent.getAsKeyEvent();
                if (asKeyEvent.getCode() == DELETE) {
                    return true;
                }
            }
        }
        return false;
    }
}
