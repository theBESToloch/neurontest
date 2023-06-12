package com.test.template;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class NN {

    //лист входных нейронов
    private final List<InputNeuron> inputNeurons;

    //Нейроны на скрытых слоях
    private final List<Neuron> hiddenNeurons;

    //лист выходных нейронов
    private final List<OutputNeuron> outputNeurons;

    // Скрытый слой нейронной сети.
    // Необходим для расчетов.
    // Расчеты нейронов на скрытом слое можно выполнять параллельно.
    private final List<Layer> hiddenLayers;

    public NN() {
        inputNeurons = new ArrayList<>();
        hiddenNeurons = new ArrayList<>();
        outputNeurons = new ArrayList<>();

        hiddenLayers = new LinkedList<>();
    }

    public List<InputNeuron> getInputNeurons() {
        return inputNeurons;
    }

    public List<Neuron> getHiddenNeurons() {
        return hiddenNeurons;
    }

    public List<OutputNeuron> getOutputNeurons() {
        return outputNeurons;
    }

    public List<Layer> getHiddenLayers() {
        return hiddenLayers;
    }
}
