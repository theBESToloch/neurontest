package com.test.context;

import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class EventDescriptor {

    EventType eventType;

    InputEvent event;

    public KeyEvent getAsKeyEvent() {
        return (KeyEvent) event;
    }

    public MouseEvent getAsMouseEvent() {
        return (MouseEvent) event;
    }

    public ScrollEvent getAsScrollEvent() {
        return (ScrollEvent) event;
    }


    public enum EventType {
        BUTTON_PRESSED,
        BUTTON_RELEASED,

        MOUSE_PRESSED,
        MOUSE_MOVED,
        MOUSE_DRAGGED,
        MOUSE_RELEASED,
        MOUSE_SCROLL,
        UNDEFINED
    }
}
