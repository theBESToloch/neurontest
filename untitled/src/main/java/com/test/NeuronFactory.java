package com.test;

import com.test.enums.NeuronTypes;
import com.test.template.InputNeuron;
import com.test.template.Layer;
import com.test.template.Neuron;
import com.test.template.OutputNeuron;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

@Slf4j
public class NeuronFactory {
    private static final ExecutorService executorService = Executors.newWorkStealingPool();
    public static double err;

    //лист входных нейронов
    private static final List<InputNeuron> inputNeurons = new ArrayList<>();
    //Нейроны на скрытых слоях
    private static final List<Neuron> hiddenNeurons = new ArrayList<>();
    // Скрытый слой нейронной сети.
    // Необходим для расчетов.
    // Расчеты нейронов на скрытом слое можно выполнять параллельно.
    private static final List<Layer> hiddenLayers = new LinkedList<>();
    //лист выходных нейронов
    private static final List<OutputNeuron> outputNeurons = new ArrayList<>();

    public static void train(int epox, List<double[]> inputs, List<double[]> output) {
        for (int i = 0; i < epox; i++) {

            //region правим веса
            long neuronNumber = Math.round((hiddenNeurons.size() - 1) * Math.random());
            Neuron neuron = hiddenNeurons.get((int) neuronNumber);

            double[] dendrites = neuron.getDendrites();
            int dendriteNumber = (int) Math.round((dendrites.length - 1) * Math.random());

            double dendriteCurrentValue = dendrites[dendriteNumber];
            dendrites[dendriteNumber] = dendriteCurrentValue + Math.pow(Math.random() - 0.5, 3);
            //endregion

            double currentError = calcError(inputs, output);
            if (currentError < err) {
                err = currentError;
            } else {
                dendrites[dendriteNumber] = dendriteCurrentValue;
            }
        }
    }

    public static void trainWithCondition(Predicate<Double> isEnd, List<double[]> inputs, List<double[]> output) {
        executorService.submit(() -> {
            try {
                err = calcError(inputs, output);
                while (!isEnd.test(err)) train(100000, inputs, output);
            } catch (Throwable th) {
                log.error("Train err:", th);
            }
        });
    }

    public static double[] calculate(double[] inputs) {
        // устанавливаю значения в входные нейроны
        for (int i = 0; i < inputNeurons.size(); i++) {
            inputNeurons.get(i).setAxon(inputs[i]);
        }

        for (Layer hiddenLayer : hiddenLayers) {
            for (Neuron neuron : hiddenLayer.getNeurons()) {
                int inputCount = neuron.getInputCount();
                Neuron[] neuronInputNeurons = neuron.getInputNeurons();
                double[] neuronDendrites = neuron.getDendrites();

                double output = 0;
                for (int i = 0; i < inputCount; i++) {
                    output += neuronInputNeurons[i].getAxon() * neuronDendrites[i];
                }
                output += neuronDendrites[inputCount];

                neuron.setAxon(1 / (1 + Math.exp(-output)));
            }
        }

        double[] outputs = new double[outputNeurons.size()];

        for (int j = 0; j < outputNeurons.size(); j++) {
            OutputNeuron outputNeuron = outputNeurons.get(j);
            int inputCount = outputNeuron.getInputCount();
            Neuron[] neuronInputNeurons = outputNeuron.getInputNeurons();

            double output = 0;
            for (int i = 0; i < inputCount; i++) {
                output += neuronInputNeurons[i].getAxon();
            }
            outputs[j] = output;
        }

        return outputs;
    }

    private static double calcError(List<double[]> inputs, List<double[]> output) {
        double err = 0;
        for (int j = 0; j < inputs.size(); j++) {
            double[] calculateResult = calculate(inputs.get(j));
            double[] outputValues = output.get(j);
            for (int i = 0; i < calculateResult.length; i++) {
                err += Math.pow(calculateResult[i] - outputValues[i], 2);
            }
        }
        return Math.sqrt(err / inputs.size());
    }

    public static void removeNeuron(Neuron neuron) {
        if (neuron instanceof InputNeuron) {
            inputNeurons.remove(neuron);
            hiddenNeurons.forEach(hiddenNeuron -> hiddenNeuron.removeInputNeuron(neuron));
        } else if (neuron instanceof OutputNeuron) {
            outputNeurons.remove(neuron);
        } else {
            hiddenNeurons.remove(neuron);
            hiddenNeurons.forEach(hiddenNeuron -> hiddenNeuron.removeInputNeuron(neuron));
        }
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

    private static void addNeuron(Neuron neuron) {
        switch (neuron) {
            case InputNeuron inputNeuron -> inputNeurons.add(inputNeuron);
            case OutputNeuron outputNeuron -> outputNeurons.add(outputNeuron);
            case Neuron hiddenNeuron -> hiddenNeurons.add(hiddenNeuron);
        }
    }

    public static void bindNeurons(Neuron outputNeuron, Neuron inputNeuron) {
        inputNeuron.addInputNeuron(outputNeuron);

        //добавляем или обновляем нейрон в слоях
        if (inputNeuron instanceof OutputNeuron) return;

        hiddenLayers.forEach(hiddenLayer -> hiddenLayer.removeNeuron(inputNeuron));
        log.debug("Начинаю распределять по слоям");
        //тут ищем индекс слоя, на котором находится нейрон,
        //аксон которого находится в качестве входящего параметра в наш нейрон
        int targetLayer = -1;
        for (int i = hiddenLayers.size() - 1; i >= 0; i--) {
            Layer layer = hiddenLayers.get(i); //беру крайний слой
            List<Neuron> neurons = layer.getNeurons();
            if (containSomeNeuron(neurons, inputNeuron.getInputNeurons())) {
                targetLayer = i;
                break;
            }
        }
        log.debug("Найден слой: {}", targetLayer);
        if (targetLayer == -1) {
            if (hiddenLayers.isEmpty()) {
                hiddenLayers.add(new Layer());
            }
        } else {
            if(targetLayer + 1 == hiddenLayers.size()){
                hiddenLayers.add(new Layer());
            }
        }
        targetLayer++;
        Layer layer = hiddenLayers.get(targetLayer);

        if (containSomeNeuron(layer.getNeurons(), new Neuron[]{inputNeuron})) {
            log.debug("На слое {} уже присутсвует нейрон {}", targetLayer, inputNeuron.getId());
            return;
        }
        log.debug("Добавляю на слой {} нейрон {}", targetLayer, inputNeuron.getId());

        layer.addNeuron(inputNeuron);

    }

    private static boolean containSomeNeuron(List<Neuron> source, Neuron[] someOf) {
        for (Neuron neuron : source) {
            for (Neuron neuron1 : someOf) {
                if (neuron == neuron1) return true;
            }
        }
        return false;
    }

}
