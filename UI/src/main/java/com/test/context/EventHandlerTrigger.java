package com.test.context;

import javafx.scene.input.KeyCode;

public class EventHandlerTrigger implements EventTrigger {

    private final KeyCode[] keyCodes;
    private final MouseEventCode[] mouseEventCodes;
    private final EventHandler eventHandler;

    public EventHandlerTrigger(KeyCode[] keyCodes, MouseEventCode[] mouseEventCodes, EventHandler eventHandler) {
        this.keyCodes = keyCodes;
        this.mouseEventCodes = mouseEventCodes;
        this.eventHandler = eventHandler;
    }

    public void handle(ButtonClickState buttonClickState) {
        eventHandler.handle(buttonClickState);
    }

    @Override
    public KeyCode[] getKeyForTrigger() {
        return keyCodes;
    }

    @Override
    public MouseEventCode[] getMouseForTrigger() {
        return mouseEventCodes;
    }
}
