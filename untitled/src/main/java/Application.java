import com.test.template.InputNeuron;
import com.test.template.Neuron;
import com.test.template.OutputNeuron;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Application {

    //мапа всех нейронов
    private final Map<Long, Neuron> allNeurons = new HashMap<>();
    //лист нейронов, кроме входных
    private final List<Neuron> neurons = new ArrayList<>();
    //лист входных нейронов
    private final List<InputNeuron> inputNeurons = new ArrayList<>();
    //лист выходных нейронов
    private final List<OutputNeuron> outputNeurons = new ArrayList<>();
    //лист сортированных нейронов, для вычислений
    private final List<Neuron> neuronSequence = new ArrayList<>();

    public void addNeuron(Neuron neuron) {
        allNeurons.put(neuron.getId(), neuron);
        if (neuron instanceof InputNeuron) {
            inputNeurons.add((InputNeuron) neuron);
        } else if (neuron instanceof OutputNeuron) {
            outputNeurons.add((OutputNeuron) neuron);
            neuronSequence.add(neuron);
        } else {
            neurons.add(neuron);
            neuronSequence.add(neuron);
        }
        sorted();
    }

    public void sorted() {
        neuronSequence.sort((neuron1, neuron2) -> {
            if (checkInputNeuron(neuron1, neuron2.getId())) {
                return 1;
            }
            if (checkInputNeuron(neuron2, neuron1.getId())) {
                return -1;
            }
            return 0;
        });
    }

    private boolean checkInputNeuron(Neuron neuron1, long id) {
        if (neuron1.getInputNeurons().contains(id)) {
            return true;
        }
        for (Long inputNeuron : neuron1.getInputNeurons()) {
            Neuron neuron = allNeurons.get(inputNeuron);
            if (checkInputNeuron(neuron, id)) {
                return true;
            }
        }
        return false;
    }

    public void calculate() {
        neuronSequence.forEach(neuron -> neuron.calculate(allNeurons));
    }

    public void train(int epox, List<double[]> inputs, List<double[]> output) {

        double err = calcError(inputs, output);
        for (int i = 0; i < epox; i++) {
            long neuronNumber = Math.round((neurons.size() - 1) * Math.random());
            Neuron neuron = neurons.get((int) neuronNumber);
            List<Double> dendrites = neuron.getDendrites();
            long dendriteNumber = Math.round((dendrites.size() - 1) * Math.random());
            Double dendriteCurrentValue = dendrites.get((int) dendriteNumber);
            double dendriteNextValue = Math.random();
            dendrites.set((int) dendriteNumber, dendriteNextValue);
            double currentError = calcError(inputs, output);
            if (currentError < err) {
                err = currentError;
                System.out.println("Neuron = " + neuron.getId() + " dendriteNumber = " + dendriteNumber +
                        " from - " + dendriteCurrentValue + " to - " + dendriteNextValue +
                        ". err = " + currentError + ", value = " + neuronSequence.get(neuronSequence.size() - 1).getAxon());
            } else {
                dendrites.set((int) dendriteNumber, dendriteCurrentValue);
            }
        }
    }

    private double calcError(List<double[]> inputs, List<double[]> output) {
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

    public static void main(String[] args) {
        Application application = new Application();

        InputNeuron inputNeuron1 = new InputNeuron();
        inputNeuron1.setValue(1);
        application.addNeuron(inputNeuron1);

        InputNeuron inputNeuron2 = new InputNeuron();
        inputNeuron2.setValue(1);
        application.addNeuron(inputNeuron2);

        Neuron neuron1 = new Neuron();
        neuron1.addInputNeurons(List.of(inputNeuron1, inputNeuron2));
        application.addNeuron(neuron1);

        Neuron neuron2 = new Neuron();
        neuron2.addInputNeurons(List.of(inputNeuron1, inputNeuron2, neuron1));
        application.addNeuron(neuron2);

        Neuron neuron3 = new Neuron();
        neuron3.addInputNeurons(List.of(inputNeuron1, inputNeuron2, neuron1, neuron2));
        application.addNeuron(neuron3);

        Neuron neuron4 = new Neuron();
        neuron4.addInputNeurons(List.of(neuron1, neuron2, neuron3));
        application.addNeuron(neuron4);

        Neuron neuron5 = new Neuron();
        neuron5.addInputNeurons(List.of(neuron1));
        application.addNeuron(neuron5);

        neuron3.addInputNeuron(neuron5);

        OutputNeuron outputNeuron = new OutputNeuron();
        outputNeuron.addInputNeurons(List.of(neuron2, neuron3, neuron4, neuron5));
        application.addNeuron(outputNeuron);


        application.train(10000,
                List.of(new double[]{1, 1}, new double[]{2, 2}, new double[]{3, 3}),
                List.of(new double[]{2}, new double[]{4}, new double[]{6}));

        inputNeuron1.setValue(1);
        inputNeuron2.setValue(1);
        application.calculate();
        System.out.println(outputNeuron.getAxon());

        inputNeuron1.setValue(2);
        inputNeuron2.setValue(2);
        application.calculate();
        System.out.println(outputNeuron.getAxon());

        inputNeuron1.setValue(3);
        inputNeuron2.setValue(3);
        application.calculate();
        System.out.println(outputNeuron.getAxon());
    }
}
