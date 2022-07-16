package com.test.handlers;

import com.test.context.ApplicationContext;
import com.test.context.ButtonClickState;
import com.test.context.EventHandler;
import com.test.data.NeuronGraph;
import com.test.events.NeedUpdateCanvasEvent;
import com.test.template.Neuron;
import javafx.scene.input.MouseEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AddNeuronEventHandler implements EventHandler {
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
    public void handle(ButtonClickState buttonClickState) {
        MouseEvent mouseEvent = buttonClickState.getPressedMouseEvent();

        double x = mouseEvent.getX();
        double y = mouseEvent.getY();

        final Optional<NeuronGraph> neuronPressed = state.getNeuronGraphList()
                .stream()
                .filter(neuronGraph -> neuronGraph.isOccupied(x, y))
                .findFirst();

        if (neuronPressed.isEmpty()) {
            Neuron neuron = state.getNeuronFactory().createNeuron(state.getNeuronType());

            NeuronGraph addedNeuron = new NeuronGraph(x, y, state.getNeuronType());
            addedNeuron.setNeuron(neuron.getId());

            state.getNeuronGraphList().add(addedNeuron);

            applicationEventPublisher.publishEvent(new NeedUpdateCanvasEvent());
        }

    }
}
