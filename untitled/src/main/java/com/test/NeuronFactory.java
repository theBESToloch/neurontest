package com.test;

import com.test.enums.NeuronTypes;
import com.test.template.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

@Slf4j
public class NeuronFactory {
    private static final ExecutorService executorService = Executors.newWorkStealingPool();
    private final NNTrain nnTrain;
    private final NN nn;

    public NeuronFactory(NN nn) {
        this.nn = nn;
        nnTrain = new NNTrain();
    }

    public void train(int epox, List<double[]> inputs, List<double[]> output) {
        List<Neuron> hiddenNeurons = nn.getHiddenNeurons();
        double err = nnTrain.getErr();
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
        nnTrain.setErr(err);
    }

    public void trainWithCondition(Predicate<Double> isEnd, List<double[]> inputs, List<double[]> output) {
        executorService.submit(() -> {
            try {
                double err = calcError(inputs, output);
                while (!isEnd.test(err)) train(100000, inputs, output);
            } catch (Throwable th) {
                log.error("Train err:", th);
            }
        });
    }

    public double[] calculate(double[] inputs) {
        List<InputNeuron> inputNeurons = nn.getInputNeurons();
        List<Layer> hiddenLayers = nn.getHiddenLayers();
        List<OutputNeuron> outputNeurons = nn.getOutputNeurons();
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

    private double calcError(List<double[]> inputs, List<double[]> output) {
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

    public void removeNeuron(long neuron) {
        Optional<Neuron> hiddenNeuron = nn.getHiddenNeurons().stream().filter(hidden-> hidden.getId() == neuron).findFirst();
        Optional<InputNeuron> inputNeuron = nn.getInputNeurons().stream().filter(input-> input.getId() == neuron).findFirst();
        Optional<OutputNeuron> outputNeuron = nn.getOutputNeurons().stream().filter(output-> output.getId() == neuron).findFirst();



        if (inputNeuron.isPresent()) {
            nn.getInputNeurons().remove(inputNeuron.get());
            nn.getHiddenNeurons().forEach(hidden -> hidden.removeInputNeuron(inputNeuron.get()));
        } else if (outputNeuron.isPresent()) {
            nn.getOutputNeurons().remove(outputNeuron.get());
        } else if (hiddenNeuron.isPresent()) {
            nn.getHiddenNeurons().remove(hiddenNeuron.get());
            nn.getHiddenNeurons().forEach(hidden-> hidden.removeInputNeuron(hiddenNeuron.get()));
        }
    }

    public Neuron createNeuron(NeuronTypes neuronTypes) {
        Neuron neuron = null;
        switch (neuronTypes) {
            case INPUT -> neuron = new InputNeuron();
            case HIDDEN -> neuron = new Neuron();
            case OUTPUT -> neuron = new OutputNeuron();
        }
        addNeuron(neuron);
        return neuron;
    }

    private void addNeuron(Neuron neuron) {
        switch (neuron) {
            case InputNeuron inputNeuron -> nn.getInputNeurons().add(inputNeuron);
            case OutputNeuron outputNeuron -> nn.getOutputNeurons().add(outputNeuron);
            case Neuron hiddenNeuron -> nn.getHiddenNeurons().add(hiddenNeuron);
        }
    }

    //todo поправить ошибку со слоями - если от текущего нейрона зависят нейроны на следующем слое, то создавать слой между ними и вставлять туда нейрон
    public void bindNeurons(long outputNeuronId, long inputNeuronId) {

        Neuron outputNeuron = getNeuron(outputNeuronId);
        Neuron inputNeuron = getNeuron(inputNeuronId);

        inputNeuron.addInputNeuron(outputNeuron);
        List<Layer> hiddenLayers = nn.getHiddenLayers();
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
            if (targetLayer + 1 == hiddenLayers.size()) {
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

    private Neuron getNeuron(long neuronId) {
        Optional<InputNeuron> input = nn.getInputNeurons().stream().filter(n -> n.getId() == neuronId).findFirst();
        if(input.isPresent()) return input.get();

        Optional<Neuron> hidden = nn.getHiddenNeurons().stream().filter(n -> n.getId() == neuronId).findFirst();
        if(hidden.isPresent()) return hidden.get();

        Optional<OutputNeuron> output = nn.getOutputNeurons().stream().filter(n -> n.getId() == neuronId).findFirst();
        if(output.isPresent()) return output.get();

        throw new RuntimeException("Нет такого нейрона");
    }

    private boolean containSomeNeuron(List<Neuron> source, Neuron[] someOf) {
        for (Neuron neuron : source) {
            for (Neuron neuron1 : someOf) {
                if (neuron == neuron1) return true;
            }
        }
        return false;
    }

}
