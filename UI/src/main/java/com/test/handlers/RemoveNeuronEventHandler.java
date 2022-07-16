package com.test.handlers;

import com.test.context.ApplicationContext;
import com.test.context.ButtonClickState;
import com.test.context.EventHandler;
import com.test.data.NeuronGraph;
import com.test.events.NeedUpdateCanvasEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RemoveNeuronEventHandler implements EventHandler {
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
    public void handle(ButtonClickState buttonClickState) {
        List<NeuronGraph> neuronGraphs = state.getSelectNeurons();

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

        applicationEventPublisher.publishEvent(new NeedUpdateCanvasEvent());

    }
}
