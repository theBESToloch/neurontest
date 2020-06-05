package com.test.template;

import java.util.Map;

public class InputNeuron extends Neuron {

    public void setValue(double value) {
        super.axon = value;
    }

    @Override
    public void calculate(Map<Long, Neuron> neurons) {
    }
}
