package com.test.data;

import com.test.enums.NeuronTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class NeuronGraph {
    private static final AtomicLong counter = new AtomicLong(0);

    private final String id;
    private final double x;
    private final double y;
    private final double radius;

    private final NeuronTypes neuronTypes;
    private final List<String> outputConnect;
    private final List<String> inputConnect;
    private long neuron;

    public NeuronGraph(double x, double y, double radius, NeuronTypes neuronTypes) {
        this.id = String.valueOf(counter.addAndGet(1L));
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.neuronTypes = neuronTypes;
        this.outputConnect = new ArrayList<>();
        this.inputConnect = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public boolean isOccupied(double x, double y) {
        return x > (this.x - radius) && x < (this.x + radius) && y > (this.y - radius) && y < (this.y + radius);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getRadius() {
        return radius;
    }

    public NeuronTypes getNeuronTypes() {
        return neuronTypes;
    }

    public long getNeuron() {
        return neuron;
    }

    public NeuronGraph setNeuron(long neuron) {
        this.neuron = neuron;
        return this;
    }

    public void addOutputNeuronGraph(String neuronId) {
        this.outputConnect.add(neuronId);
    }

    public void removeFromOutput(String neuronId) {
        outputConnect.remove(neuronId);
    }

    public List<String> getOutputConnect() {
        return outputConnect;
    }

    public void addInputNeuronGraph(String neuronId) {
        this.inputConnect.add(neuronId);
    }

    public void removeFromInput(String neuronId) {
        inputConnect.remove(neuronId);
    }

    public List<String> getInputConnect() {
        return inputConnect;
    }
}