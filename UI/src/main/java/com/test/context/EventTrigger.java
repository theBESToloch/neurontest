package com.test.context;

import javafx.scene.input.KeyCode;

public interface EventTrigger {

    KeyCode[] getKeyForTrigger();

    MouseEventCode[] getMouseForTrigger();

}
