package com.test.UIControllers;

import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public class ManageWindowController {

    public static boolean isAdd = true;

    public Button removeFromProgressBax;
    public Button addToProgressBax;

    public void add(MouseEvent mouseEvent) {
        isAdd = true;
    }

    public void remove(MouseEvent mouseEvent) {
        isAdd = false;
    }
}
