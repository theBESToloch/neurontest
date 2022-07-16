package com.test.handlers;

import com.test.context.ApplicationContext;
import com.test.context.ButtonClickState;
import com.test.context.EventHandler;
import com.test.data.NeuronGraph;
import com.test.events.NeedUpdateCanvasEvent;
import javafx.scene.input.MouseEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SelectNeuronEventHandler implements EventHandler {
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
    public void handle(ButtonClickState buttonClickState) {
        MouseEvent mouseEvent = buttonClickState.getCurrentMouseEvent();

        final Optional<NeuronGraph> neuronPressed = state.getNeuronGraphList()
                .stream()
                .filter(neuronGraph -> neuronGraph.isOccupied(mouseEvent.getX(), mouseEvent.getY()))
                .findFirst();

        if (neuronPressed.isPresent()) {
            state.addSelectNeuron(neuronPressed.get());
            applicationEventPublisher.publishEvent(new NeedUpdateCanvasEvent());
        } else {
            boolean needUpdate = !state.getSelectNeurons().isEmpty();
            state.cleanSelectNeurons();
            if (needUpdate) {
                applicationEventPublisher.publishEvent(new NeedUpdateCanvasEvent());
            }
        }
    }
}
