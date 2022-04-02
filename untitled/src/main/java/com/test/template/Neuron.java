package com.test.template;

import java.util.concurrent.atomic.AtomicLong;

public class Neuron {
    private static final AtomicLong counter = new AtomicLong(0);

    private int inputCount;
    private double[] dendrites;
    private Neuron[] inputNeurons;
    protected double axon;
    private final long id;

    public Neuron() {
        this.inputNeurons = new Neuron[0];
        this.dendrites = new double[1];
        this.dendrites[0] = 0;  // последний элемент - это смещение
        this.id = counter.addAndGet(1);
    }

    public void addInputNeuron(Neuron neuron) {
        addInputNeuron(neuron, Math.random());
    }

    public void addInputNeuron(Neuron neuron, double dendrite) {
        Neuron[] prevInputNeurons = inputNeurons;
        inputNeurons = new Neuron[prevInputNeurons.length + 1];
        System.arraycopy(prevInputNeurons, 0, inputNeurons, 0, prevInputNeurons.length);
        inputNeurons[prevInputNeurons.length] = neuron;

        double[] prevDendrites = dendrites;
        dendrites = new double[prevDendrites.length + 1];
        System.arraycopy(prevDendrites, 0, dendrites, 0, prevDendrites.length);
        dendrites[prevDendrites.length - 1] = dendrite;
        dendrites[prevDendrites.length] = prevDendrites[prevDendrites.length - 1];  // копирую дендрид смещения
        inputCount++;
    }

    public void removeInputNeuron(Neuron neuron) {
        int i = -1;
        for (int j = 0; j < inputNeurons.length; j++) {
            if (inputNeurons[j] == neuron) {
                i = j;
                break;
            }
        }
        if (i == -1) return;
        Neuron[] prevInputNeurons = inputNeurons;
        inputNeurons = new Neuron[prevInputNeurons.length - 1];

        System.arraycopy(prevInputNeurons, 0, inputNeurons, 0, i);
        System.arraycopy(prevInputNeurons, i, inputNeurons, i, prevInputNeurons.length - 1 - i);

        double[] prevDendrites = dendrites;
        dendrites = new double[prevDendrites.length - 1];

        System.arraycopy(prevDendrites, 0, dendrites, 0, i);
        System.arraycopy(prevDendrites, i, dendrites, i, prevDendrites.length - 1 - i);
        inputCount--;
    }

    public long getId() {
        return id;
    }

    public void setAxon(double axon) {
        this.axon = axon;
    }

    public double getAxon() {
        return axon;
    }

    public int getInputCount() {
        return inputCount;
    }

    public Neuron[] getInputNeurons() {
        return inputNeurons;
    }

    public double[] getDendrites() {
        return dendrites;
    }

    @Override
    public String toString() {
        return "Neuron{" +
                "id=" + id +
                '}';
    }
}
