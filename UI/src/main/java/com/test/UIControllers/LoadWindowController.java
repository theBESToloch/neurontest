package com.test.UIControllers;

import com.test.events.ModelLoadEvent;
import com.test.persistence.entities.NNDescription;
import com.test.persistence.services.NNDescriptionService;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Slf4j
@Component
public class LoadWindowController implements Initializable {

    @FXML
    public SplitPane splitPane;
    public ListView<NNDescription> listView;
    public ImageView imageView;

    private final NNDescriptionService nnDescriptionService;
    public ContextMenu contextMenu;


    public LoadWindowController(NNDescriptionService nnDescriptionService) {
        this.nnDescriptionService = nnDescriptionService;
    }

    @EventListener
    public void show(ModelLoadEvent event) {
        updateItems();

        Stage window = (Stage) splitPane.getScene().getWindow();
        window.show();
    }

    private void updateItems() {
        listView.getItems().clear();

        List<NNDescription> nnDescriptions = nnDescriptionService.load();
        listView.getItems().addAll(nnDescriptions);

        setPreviewImage(!nnDescriptions.isEmpty() ? nnDescriptions.get(0) : null);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listView = new ListView<>();
        listView.setCellFactory(callBack -> new ListCell<>() {
            @Override
            public void updateItem(NNDescription nnDescription, boolean empty) {
                super.updateItem(nnDescription, empty);
                if (empty || nnDescription == null) {
                    setText(null);
                } else {
                    setText(String.valueOf(nnDescription.getId()));
                }
            }
        });

        // получаем модель выбора элементов
        MultipleSelectionModel<NNDescription> selectionModel = listView.getSelectionModel();
        // устанавливаем слушатель для отслеживания изменений
        selectionModel.selectedItemProperty().addListener(this::onSelectListener);

        splitPane.getItems().add(listView);

        imageView = new ImageView();

        splitPane.getItems().add(imageView);

        listView.setOnMouseClicked(event -> {
            if (MouseButton.SECONDARY == event.getButton()) {
                contextMenu.show(splitPane.getScene().getWindow());
            }
        });

        MenuItem deleteButton = new MenuItem("Удалить");
        deleteButton.setOnAction(this::onContextDeleteButtonMouseClick);

        contextMenu.getItems().addAll(deleteButton);
    }

    private void onContextDeleteButtonMouseClick(ActionEvent actionEvent) {
        contextMenu.hide();
        NNDescription selectedItem = listView.getSelectionModel().getSelectedItem();
        nnDescriptionService.delete(selectedItem.getId());
        updateItems();
    }

    private void onSelectListener(ObservableValue<? extends NNDescription> observable, NNDescription oldValue, NNDescription newValue) {
        setPreviewImage(newValue);
    }

    private void setPreviewImage(NNDescription nnDescription) {
        imageView.setImage(null);
        if (nnDescription == null) return;
        byte[] previewImage = nnDescription.getNnPreview().getPreviewImage();
        Image value = new Image(new ByteArrayInputStream(previewImage));
        imageView.setImage(value);
    }
}
