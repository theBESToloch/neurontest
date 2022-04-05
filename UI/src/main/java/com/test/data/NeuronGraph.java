package com.test.data;

import com.test.enums.NeuronTypes;
import com.test.template.Neuron;

import java.util.ArrayList;
import java.util.List;

public class NeuronGraph {
    private final double x;
    private final double y;
    private final double radius;

    private final NeuronTypes neuronTypes;
    private final List<NeuronGraph> outputConnect;
    private final List<NeuronGraph> inputConnect;
    private Neuron neuron;

    public NeuronGraph(double x, double y, double radius, NeuronTypes neuronTypes) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.neuronTypes = neuronTypes;
        this.outputConnect = new ArrayList<>();
        this.inputConnect = new ArrayList<>();
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

    public Neuron getNeuron() {
        return neuron;
    }

    public NeuronGraph setNeuron(Neuron neuron) {
        this.neuron = neuron;
        return this;
    }

    public void addOutputNeuronGraph(NeuronGraph neuronGraph) {
        this.outputConnect.add(neuronGraph);
    }

    public void removeNeuronGraphsFromOutput(NeuronGraph neuronGraph) {
        outputConnect.remove(neuronGraph);
    }

    public List<NeuronGraph> getOutputConnect() {
        return outputConnect;
    }

    public void addInputNeuronGraph(NeuronGraph neuronGraph) {
        this.inputConnect.add(neuronGraph);
    }

    public void removeNeuronGraphsFromInput(NeuronGraph neuronGraph) {
        inputConnect.remove(neuronGraph);
    }

    public List<NeuronGraph> getInputConnect() {
        return inputConnect;
    }
}