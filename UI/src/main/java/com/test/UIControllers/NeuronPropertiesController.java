package com.test.UIControllers;

import com.test.data.dto.NeuronPropertiesDendriteTableColumns;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class NeuronPropertiesController implements Initializable {

    public static TableView<NeuronPropertiesDendriteTableColumns> dendritesTable;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TableColumn<NeuronPropertiesDendriteTableColumns, Long> neuron = new TableColumn<>("neuron");
        TableColumn<NeuronPropertiesDendriteTableColumns, Double> weight = new TableColumn<>("weight");
        neuron.setCellValueFactory(new PropertyValueFactory<>("neuron"));
        weight.setCellValueFactory(new PropertyValueFactory<>("weight"));
    }
}
