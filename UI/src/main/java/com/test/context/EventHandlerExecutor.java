package com.test.context;

import javafx.scene.input.KeyCode;

public interface EventHandlerExecutor {

    void executeIfApproach(KeyCode[] keys, MouseEventCode[] mouseKeys, ButtonClickState buttonClickState);
}
