package com.test.UIControllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.common.data.dto.NeuronGraph;
import com.test.context.ApplicationContext;
import com.test.events.LoadModelEvent;
import com.test.events.ShowModelLoadWindowEvent;
import com.test.persistence.entities.NNPreview;
import com.test.persistence.services.NNPreviewService;
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
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoadWindowController implements Initializable {

    @FXML
    public SplitPane splitPane;
    public ListView<NNPreview> listView;
    public ImageView imageView;
    public ContextMenu contextMenu;

    private final NNPreviewService nnPreviewService;
    private final ApplicationContext.LoadWindowState loadWindowState;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ObjectMapper objectMapper;

    @EventListener
    public void show(ShowModelLoadWindowEvent event) {
        updateItems();

        Stage window = (Stage) splitPane.getScene().getWindow();
        window.show();
    }

    private void updateItems() {
        listView.getItems().clear();

        Page<NNPreview> nnPreviews = nnPreviewService.load(PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC,"date")));
        listView.getItems().addAll(nnPreviews.getContent());

        setPreviewImage(!nnPreviews.isEmpty() ? nnPreviews.getContent().get(0) : null);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listView = new ListView<>();
        listView.setCellFactory(callBack -> new ListCell<>() {
            @Override
            public void updateItem(NNPreview nnPreview, boolean empty) {
                super.updateItem(nnPreview, empty);
                if (empty || nnPreview == null) {
                    setText(null);
                } else {
                    setText(String.valueOf(nnPreview.getId()));
                }
            }
        });

        // получаем модель выбора элементов
        MultipleSelectionModel<NNPreview> selectionModel = listView.getSelectionModel();
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

        MenuItem openButton = new MenuItem("Открыть");
        openButton.setOnAction(this::onContextOpenButtonMouseClick);

        contextMenu.getItems().addAll(openButton, deleteButton);
    }

    @SneakyThrows
    private void onContextOpenButtonMouseClick(ActionEvent actionEvent) {
        contextMenu.hide();
        NNPreview selectedItem = listView.getSelectionModel().getSelectedItem();
        splitPane.getScene().getWindow().hide();


        loadWindowState.setNeuronGraphList(objectMapper.readValue(selectedItem.getNnDescription().getStruct(),
                new TypeReference<>() {}));
        applicationEventPublisher.publishEvent(new LoadModelEvent());
    }

    private void onContextDeleteButtonMouseClick(ActionEvent actionEvent) {
        contextMenu.hide();
        NNPreview selectedItem = listView.getSelectionModel().getSelectedItem();
        nnPreviewService.delete(selectedItem.getId());
        updateItems();
    }

    private void onSelectListener(ObservableValue<? extends NNPreview> observable,
                                  NNPreview oldValue, NNPreview newValue) {
        setPreviewImage(newValue);
    }

    private void setPreviewImage(NNPreview nnPreview) {
        imageView.setImage(null);
        if (nnPreview == null) return;
        byte[] previewImage = nnPreview.getPreviewImage();
        Image value = new Image(new ByteArrayInputStream(previewImage));
        imageView.setImage(value);
    }
}
