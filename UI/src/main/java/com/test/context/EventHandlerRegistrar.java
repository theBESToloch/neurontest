package com.test.context;

import javafx.scene.input.KeyCode;

public interface EventHandlerRegistrar {

    void register(EventHandlerTrigger eventHandlerTrigger);

    default void register(KeyCode[] keyCodes, MouseEventCode[] mouseEventCodes, EventHandler eventHandler){
        register(new EventHandlerTrigger(keyCodes, mouseEventCodes, eventHandler));
    }


}
