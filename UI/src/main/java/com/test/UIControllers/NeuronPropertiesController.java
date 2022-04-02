package com.test.UIControllers;

import com.test.data.dto.NeuronPropertiesDendriteTableColumns;
import com.test.template.Neuron;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class NeuronPropertiesController implements Initializable {

    public TextField neuronId;
    public TableView<NeuronPropertiesDendriteTableColumns> dendritesTable;
    public TableColumn<NeuronPropertiesDendriteTableColumns, Long> neuronColumn;
    public TableColumn<NeuronPropertiesDendriteTableColumns, Double> dendriteColumn;

    public static TextField staticNeuronId;
    public static TableView<NeuronPropertiesDendriteTableColumns> staticDendritesTable;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        neuronColumn.setCellValueFactory(new PropertyValueFactory<>("neuron"));
        dendriteColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));

        staticDendritesTable = dendritesTable;
        staticNeuronId = neuronId;
    }

    public static void show(NeuronGraph neuronGraph) {
        staticNeuronId.setText(String.valueOf(neuronGraph.getNeuron().getId()));
        staticDendritesTable.getItems().clear();
        Neuron[] inputNeurons = neuronGraph.getNeuron().getInputNeurons();
        double[] dendrites = neuronGraph.getNeuron().getDendrites();
        for (int i = 0; i < inputNeurons.length; i++) {
            staticDendritesTable
                    .getItems()
                    .add(new NeuronPropertiesDendriteTableColumns(inputNeurons[i].getId(), dendrites[i]));
        }
    }
}
