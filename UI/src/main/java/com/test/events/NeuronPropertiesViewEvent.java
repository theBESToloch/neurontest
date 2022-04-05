package com.test.events;

import com.test.data.NeuronGraph;

public class NeuronPropertiesViewEvent {
    private NeuronGraph neuronGraph;

    public NeuronPropertiesViewEvent(NeuronGraph neuronGraph) {
        this.neuronGraph = neuronGraph;
    }

    public NeuronGraph getNeuronGraph() {
        return neuronGraph;
    }

}
