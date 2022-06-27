package com.test.context;

import com.test.NeuronFactory;
import com.test.data.NeuronGraph;
import com.test.data.enums.ActionTypes;
import com.test.enums.NeuronTypes;
import com.test.template.NN;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class ApplicationContext {

    private NeuronFactory neuronFactory;

    private List<NeuronGraph> neuronGraphList;

    public ApplicationContext() {
        neuronFactory = new NeuronFactory(new NN());
        neuronGraphList = new ArrayList<>();
        neuronType  = NeuronTypes.HIDDEN;
        actionType  = ActionTypes.ADD;
    }

    private ActionTypes actionType;
    private NeuronTypes neuronType;
    private volatile boolean isTrainButton = true;

    public class CanvasWindowState {
        private NeuronGraph pressedNeuron;
        private NeuronGraph releasedNeuron;

        public NeuronGraph getPressedNeuron() {
            return pressedNeuron;
        }

        public void setPressedNeuron(NeuronGraph pressedNeuron) {
            this.pressedNeuron = pressedNeuron;
        }

        public NeuronGraph getReleasedNeuron() {
            return releasedNeuron;
        }

        public void setReleasedNeuron(NeuronGraph releasedNeuron) {
            this.releasedNeuron = releasedNeuron;
        }

        public NeuronFactory getNeuronFactory() {
            return neuronFactory;
        }

        public List<NeuronGraph> getNeuronGraphList() {
            return neuronGraphList;
        }

        public ActionTypes getActionType() {
            return actionType;
        }

        public NeuronTypes getNeuronType() {
            return neuronType;
        }
    }

    public class ManageWindowState {

        public List<NeuronGraph> getNeuronGraphList() {
            return ApplicationContext.this.neuronGraphList;
        }

        public NeuronFactory getNeuronFactory() {
            return neuronFactory;
        }

        public void setNeuronFactory(NeuronFactory neuronFactory) {
            ApplicationContext.this.neuronFactory = neuronFactory;
        }

        public ActionTypes getActionType() {
            return actionType;
        }

        public void setActionType(ActionTypes actionType) {
            ApplicationContext.this.actionType = actionType;
        }

        public NeuronTypes getNeuronType() {
            return neuronType;
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
    }

    public class LoadWindowState{
        public void setNeuronGraphList(List<NeuronGraph> neuronGraphList){
            ApplicationContext.this.neuronGraphList = neuronGraphList;
        }
    }
}
