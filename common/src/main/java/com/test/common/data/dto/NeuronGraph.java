package com.test.common.data.dto;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class NeuronGraph {
    private static final AtomicLong counter = new AtomicLong(0);
    public static final double RADIUS = 10;

    private final String id;

    private double x;
    private double y;

    private final NeuronTypes neuronTypes;
    private final List<String> outputConnect;
    private final List<String> inputConnect;
    private long neuron;

    public NeuronGraph(double x, double y, NeuronTypes neuronTypes) {
        this.id = String.valueOf(counter.addAndGet(1L));
        this.x = x;
        this.y = y;
        this.neuronTypes = neuronTypes;
        this.outputConnect = new ArrayList<>();
        this.inputConnect = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public boolean isOccupied(double x, double y, double multiplyer) {
        double distance = RADIUS * multiplyer;
        return x > (this.x - distance) && x < (this.x + distance) && y > (this.y - distance) && y < (this.y + distance);
    }

    public double getX() {
        return x;
    }
    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }
    public void setY(double y) {
        this.y = y;
    }

    public NeuronTypes getNeuronTypes() {
        return neuronTypes;
    }

    public long getNeuron() {
        return neuron;
    }

    public void setNeuron(long neuron) {
        this.neuron = neuron;
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