package com.test.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class Neuron {

    private static final AtomicLong counter = new AtomicLong(0);

    private final List<Double> dendrites;
    private final List<Long> inputNeurons;
    protected double axon;
    private final long id;

    public Neuron() {
        this.inputNeurons = new ArrayList<>();
        this.dendrites = new ArrayList<>();
        dendrites.add(Math.random());       //нулевой дендрит - смещение
        this.id = counter.addAndGet(1);
    }

    public void addInputNeuron(Neuron neuron) {
        inputNeurons.add(neuron.getId());
        dendrites.add(Math.random());
    }

    public void addInputNeurons(List<Neuron> neuron) {
        neuron.forEach(neuron1 -> {
            inputNeurons.add(neuron1.getId());
            dendrites.add(Math.random());
        });

    }

    public long getId() {
        return id;
    }

    public double getAxon() {
        return axon;
    }

    public List<Long> getInputNeurons() {
        return inputNeurons;
    }

    public List<Double> getDendrites() {
        return dendrites;
    }

    public void calculate(Map<Long, Neuron> neurons) {
        double output = dendrites.get(0);
        for (int i = 0; i < inputNeurons.size(); i++) {
            output += neurons.get(inputNeurons.get(i)).getAxon() * dendrites.get(i + 1);
        }
        axon = 1 / (1 + Math.exp(-output));
    }

}
