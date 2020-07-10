package com.test;

import com.test.enums.NeuronTypes;
import com.test.template.InputNeuron;
import com.test.template.Neuron;
import com.test.template.OutputNeuron;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NeuronFactory {
    //мапа всех нейронов
    private static final Map<Long, Neuron> allNeurons = new HashMap<>();

    //лист нейронов, кроме входных
    private static final List<Neuron> neurons = new ArrayList<>();

    //лист входных нейронов
    private static final List<InputNeuron> inputNeurons = new ArrayList<>();
    //лист выходных нейронов
    private static final List<OutputNeuron> outputNeurons = new ArrayList<>();

    //лист сортированных нейронов, для вычислений
    private static final List<Neuron> neuronSequence = new ArrayList<>();

    private static void addNeuron(Neuron neuron) {
        allNeurons.put(neuron.getId(), neuron);
        if (neuron instanceof InputNeuron) {
            inputNeurons.add((InputNeuron) neuron);
        } else if (neuron instanceof OutputNeuron) {
            outputNeurons.add((OutputNeuron) neuron);
            neurons.add(neuron);
            neuronSequence.add(neuron);
        } else {
            neurons.add(neuron);
            neuronSequence.add(neuron);
        }
        sort();
    }

    public static void removeNeuron(Neuron neuron) {
        allNeurons.remove(neuron.getId());
        if (neuron instanceof InputNeuron) {
            inputNeurons.remove(neuron);
        } else if (neuron instanceof OutputNeuron) {
            outputNeurons.remove(neuron);
            neurons.remove(neuron);
            neuronSequence.remove(neuron);
        } else {
            neurons.remove(neuron);
            neuronSequence.remove(neuron);
        }
        neurons.forEach(outputNeuron -> outputNeuron.getInputNeurons().remove(neuron.getId()));

        sort();
    }

    private static void sort() {
        neuronSequence.sort((neuron1, neuron2) -> {
            if (checkInputNeuron(neuron1, neuron2)) {
                return 1;
            }
            if (checkInputNeuron(neuron2, neuron1)) {
                return -1;
            }
            return 0;
        });
    }

    private static boolean checkInputNeuron(Neuron neuron1, Neuron neuron2) {
        if (neuron1.getInputNeurons().contains(neuron2.getId())) {
            return true;
        }
        for (Long inputNeuron : neuron1.getInputNeurons()) {
            Neuron neuron = allNeurons.get(inputNeuron);
            if (checkInputNeuron(neuron, neuron2)) {
                return true;
            }
        }
        return false;
    }

    public static double[] calculate(double[] inputs) {
        for (int i = 0; i < inputNeurons.size(); i++) {
            inputNeurons.get(i).setValue(inputs[i]);
        }
        neuronSequence.forEach(neuron -> neuron.calculate(allNeurons));
        double[] outputs = new double[outputNeurons.size()];
        for (int i = 0; i < outputNeurons.size(); i++) {
            outputs[i] = outputNeurons.get(i).getValue();
        }
        return outputs;
    }

    public static void calculate() {
        neuronSequence.forEach(neuron -> neuron.calculate(allNeurons));
    }

    public static void train(int epox, List<double[]> inputs, List<double[]> output) {

        double err = calcError(inputs, output);
        for (int i = 0; i < epox; i++) {
            long neuronNumber = Math.round((neurons.size() - 1) * Math.random());
            Neuron neuron = neurons.get((int) neuronNumber);
            List<Double> dendrites = neuron.getDendrites();
            long dendriteNumber = Math.round((dendrites.size() - 1) * Math.random());

            Double dendriteCurrentValue = dendrites.get((int) dendriteNumber);
            double dendriteNextValue = dendriteCurrentValue + Math.pow(Math.random() - 0.5, 3);
            dendrites.set((int) dendriteNumber, dendriteNextValue);
            double currentError = calcError(inputs, output);
            if (currentError < err) {
                err = currentError;
                System.out.println("Neuron = " + neuron.getId() + " dendriteNumber = " + dendriteNumber +
                        " from - " + dendriteCurrentValue + " to - " + dendriteNextValue +
                        ". err = " + currentError);
            } else {
                dendrites.set((int) dendriteNumber, dendriteCurrentValue);
            }
        }
    }

    private static double calcError(List<double[]> inputs, List<double[]> output) {
        double err = 0;
        for (int j = 0; j < inputs.size(); j++) {
            double[] inputValues = inputs.get(j);
            double[] outputValues = output.get(j);
            for (int i = 0; i < inputNeurons.size(); i++) {
                inputNeurons.get(i).setValue(inputValues[i]);
            }
            calculate();
            for (int i = 0; i < outputNeurons.size(); i++) {
                err += Math.abs(outputNeurons.get(i).getValue() - outputValues[i]);
            }
        }
        return err;
    }

    public static Neuron createNeuron(NeuronTypes neuronTypes) {
        Neuron neuron = null;
        switch (neuronTypes) {
            case INPUT -> neuron = new InputNeuron();
            case HIDDEN -> neuron = new Neuron();
            case OUTPUT -> neuron = new OutputNeuron();
        }
        addNeuron(neuron);
        return neuron;
    }

    public static void bindNeurons(Neuron outputNeuron, Neuron inputNeuron){
        inputNeuron.addInputNeurons(List.of(outputNeuron));
    };

}
