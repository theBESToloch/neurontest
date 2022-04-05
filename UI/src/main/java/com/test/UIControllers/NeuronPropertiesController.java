package com.test.UIControllers;

import com.test.data.NeuronGraph;
import com.test.data.dto.NeuronPropertiesDendriteTableColumns;
import com.test.events.NeuronPropertiesViewEvent;
import com.test.template.Neuron;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ResourceBundle;


@Controller
public class NeuronPropertiesController implements Initializable {

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private TextField neuronId;
    @FXML
    private TableView<NeuronPropertiesDendriteTableColumns> dendritesTable;
    @FXML
    private TableColumn<NeuronPropertiesDendriteTableColumns, Long> neuronColumn;
    @FXML
    private TableColumn<NeuronPropertiesDendriteTableColumns, Double> dendriteColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        neuronColumn.setCellValueFactory(new PropertyValueFactory<>("neuron"));
        dendriteColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));
    }

    @EventListener
    public void show(NeuronPropertiesViewEvent event) {
        NeuronGraph neuronGraph = event.getNeuronGraph();

        neuronId.setText(String.valueOf(neuronGraph.getNeuron().getId()));
        dendritesTable.getItems().clear();
        Neuron[] inputNeurons = neuronGraph.getNeuron().getInputNeurons();
        double[] dendrites = neuronGraph.getNeuron().getDendrites();
        for (int i = 0; i < inputNeurons.length; i++) {
            dendritesTable
                    .getItems()
                    .add(new NeuronPropertiesDendriteTableColumns(inputNeurons[i].getId(), dendrites[i]));
        }
        Stage window = (Stage) anchorPane.getScene().getWindow();
        Stage owner = (Stage)window.getOwner();
        window.setX(owner.getX() + owner.getWidth());
        window.setY(owner.getY());
        window.show();
    }
}
