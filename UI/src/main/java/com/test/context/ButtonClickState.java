package com.test.context;

import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;

import java.util.Arrays;

import static com.test.context.MouseEventCode.MOUSE_DRAGGED;
import static com.test.context.MouseEventCode.MOUSE_MOVED;
import static com.test.context.MouseEventCode.MOUSE_PRESSED;
import static com.test.context.MouseEventCode.MOUSE_RELEASED;

public class ButtonClickState {

    private final EventHandlerExecutor eventHandlerExecutor;

    private final int MAX_COUNT;
    private final KeyCode[] keyPressedCodes;
    private volatile int pressedButtonCount = 0;

    public ButtonClickState(EventHandlerExecutor eventHandlerExecutor, int maxCountButtonPressed) {
        this.eventHandlerExecutor = eventHandlerExecutor;
        MAX_COUNT = maxCountButtonPressed;
        //+1 только для этапа удаления нажатой кнопки, т.к. удаляется по одной кнопке и последний элемент
        // нужен null для очистки
        keyPressedCodes = new KeyCode[maxCountButtonPressed + 1];

        mouseEventCodes = new MouseEventCode[4];
        mouseEvents = new MouseEvent[3];
    }

    public synchronized boolean addKey(KeyCode pressedButton) {
        if (pressedButtonCount == MAX_COUNT) {
            return false;
        }
        int i = 0;
        while (i < pressedButtonCount && keyPressedCodes[i] != pressedButton) {
            i++;
        }
        if (i != pressedButtonCount) {
            return false;
        }
        keyPressedCodes[pressedButtonCount++] = pressedButton;

        eventHandlerExecutor.executeIfApproach(keyPressedCodes, mouseEventCodes, this);
        return true;
    }

    public synchronized boolean removeKey(KeyCode releasedButton) {
        int i = 0;
        while (i < pressedButtonCount && keyPressedCodes[i] != releasedButton) {
            i++;
        }

        if (i == pressedButtonCount) {
            return false;
        }

        for (; i < pressedButtonCount; i++) {
            keyPressedCodes[i] = keyPressedCodes[i + 1];
        }
        pressedButtonCount--;

        return true;
    }

    public KeyCode[] getKeyPressedCodes() {
        return keyPressedCodes;
    }

    private final MouseEventCode[] mouseEventCodes;

    private final MouseEvent[] mouseEvents;
    public MouseEvent getPressedMouseEvent() {
        return mouseEvents[0];
    }

    public MouseEvent getCurrentMouseEvent() {
        return mouseEvents[1];
    }

    public MouseEvent getReleasedMouseEvent() {
        return mouseEvents[2];
    }

    public boolean addMouseEvent(MouseEvent mouseEvent) {
        mouseEvents[1] = mouseEvent;

        MouseEventCode mouseEventCode = MouseEventCode.valueOf(mouseEvent.getEventType().getName());

        boolean isChanged = false;
        switch (mouseEventCode) {
            case MOUSE_PRESSED -> {
                if (mouseEventCodes[0] == null) {
                    mouseEventCodes[0] = MOUSE_PRESSED;
                    isChanged = true;
                    mouseEvents[0] = mouseEvent;
                }
            }
            case MOUSE_MOVED -> {
                if (mouseEventCodes[1] == null) {
                    mouseEventCodes[1] = MOUSE_MOVED;
                    isChanged = true;
                }
            }
            case MOUSE_DRAGGED -> {
                if (mouseEventCodes[2] == null) {
                    mouseEventCodes[2] = MOUSE_DRAGGED;
                    isChanged = true;
                }
            }
            case MOUSE_RELEASED -> {
                if (mouseEventCodes[3] == null) {
                    mouseEventCodes[3] = MOUSE_RELEASED;
                    isChanged = true;
                    mouseEvents[2] = mouseEvent;
                }
            }
        }

        if (isChanged) {
            eventHandlerExecutor.executeIfApproach(keyPressedCodes, mouseEventCodes, this);
        }
        if (mouseEventCode == MOUSE_RELEASED) {
            Arrays.fill(mouseEventCodes, null);
            Arrays.fill(mouseEvents, null);
        }
        return isChanged;
    }

}
