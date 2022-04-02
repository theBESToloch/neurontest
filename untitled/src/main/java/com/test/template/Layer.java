package com.test.template;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class Layer {
    private static final AtomicLong counter = new AtomicLong(0);

    private final long id;
    private final List<Neuron> neurons;

    public Layer() {
        id = counter.addAndGet(1);
        neurons = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public List<Neuron> getNeurons() {
        return neurons;
    }

    public void addNeuron(Neuron neuron) {
        neurons.add(neuron);
    }

    public void removeNeuron(Neuron neuron) {
        neurons.remove(neuron);
    }
}
