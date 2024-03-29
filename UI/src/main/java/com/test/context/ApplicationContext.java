package com.test.context;

import com.test.NeuronFactory;
import com.test.common.data.dto.NeuronGraph;
import com.test.common.data.dto.NeuronTypes;
import com.test.template.NN;
import javafx.scene.input.MouseEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Accessors(chain = true)
public class ApplicationContext {

    private NeuronFactory neuronFactory;

    private List<NeuronGraph> neuronGraphList;

    private NeuronTypes neuronType;
    private volatile boolean isTrainButton = true;

    public ApplicationContext() {
        neuronFactory = new NeuronFactory(new NN());
        neuronGraphList = new ArrayList<>();
        neuronType = NeuronTypes.HIDDEN;
    }

    @Getter
    @Setter
    public class CanvasWindowState {

        private double scale = 1;
        private double xOffset = 0;
        private double yOffset = 0;

        private final Set<NeuronGraph> selectNeurons = new HashSet<>();

        //for select rectangle
        private MouseEvent pressedMouse = null;
        private MouseEvent currentMouse = null;
        public void cleanSelectNeurons(){
            selectNeurons.clear();
        }

        public NeuronFactory getNeuronFactory() {
            return neuronFactory;
        }

        public List<NeuronGraph> getNeuronGraphList() {
            return neuronGraphList;
        }

        public NeuronTypes getNeuronType() {
            return neuronType;
        }
    }

    @Getter
    public class ManageWindowState {

        private static final int count = 20;
        private List<double[]> inputVectors;
        private List<double[]> outputVectors;

        public void setVectors(List<double[]> inputVectors, List<double[]> outputVectors) {
            this.inputVectors = inputVectors;
            this.outputVectors = outputVectors;
        }


        public List<NeuronGraph> getNeuronGraphList() {
            return ApplicationContext.this.neuronGraphList;
        }

        public NeuronFactory getNeuronFactory() {
            return neuronFactory;
        }

        public void setNeuronType(NeuronTypes neuronType) {
            ApplicationContext.this.neuronType = neuronType;
        }

        public boolean isTrainButton() {
            return isTrainButton;
        }

        public void setTrainButton(boolean trainButton) {
            ApplicationContext.this.isTrainButton = trainButton;
        }

        public int getCount() {
            return count;
        }
    }

    public class LoadWindowState {
        public void setNeuronGraphList(List<NeuronGraph> neuronGraphList) {
            ApplicationContext.this.neuronGraphList = neuronGraphList;
        }
    }
}
