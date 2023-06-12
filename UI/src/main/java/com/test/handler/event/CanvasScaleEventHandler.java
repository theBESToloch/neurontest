package com.test.handler.event;

import com.test.context.ApplicationContext;
import com.test.context.EventDescriptor;
import com.test.context.EventQueueHandler;
import com.test.event.NeedUpdateCanvasEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Queue;

import static javafx.scene.input.KeyCode.CONTROL;

@Slf4j
@Component
@RequiredArgsConstructor
public class CanvasScaleEventHandler implements EventQueueHandler {

    public static final String SCALE_CODE = "canvasScale";

    private final ApplicationContext.CanvasWindowState state;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public String getCode() {
        return SCALE_CODE;
    }

    @Override
    public void handle(EventDescriptor lastEvent, Queue<EventDescriptor> eventQueue) {
        if (!isTriggered(lastEvent, eventQueue)) {
            return;
        }

        double scale = state.getScale();
        double calcScale = scale + (scrollEvent.getDeltaY() * 0.001);
        state.setScale(calcScale);

        applicationEventPublisher.publishEvent(NeedUpdateCanvasEvent.INSTANT);
    }

    private KeyEvent controlButtonPressedEvent;
    private ScrollEvent scrollEvent;

    private boolean isTriggered(EventDescriptor lastEvent, Queue<EventDescriptor> eventQueue) {
        EventDescriptor.EventType eventType = lastEvent.getEventType();

        if (eventType == EventDescriptor.EventType.BUTTON_PRESSED) {
            if (controlButtonPressedEvent != null) {
                return false;
            }
            KeyEvent asKeyEvent = lastEvent.getAsKeyEvent();
            if (asKeyEvent.getCode() == CONTROL) {
                controlButtonPressedEvent = asKeyEvent;
                return false;
            }
        }
        if (eventType == EventDescriptor.EventType.BUTTON_RELEASED) {
            if (controlButtonPressedEvent == null) {
                return false;
            }
            KeyEvent asKeyEvent = lastEvent.getAsKeyEvent();
            if (asKeyEvent.getCode() == CONTROL) {
                controlButtonPressedEvent = null;
                scrollEvent = null;
            }
            return false;
        }
        if (eventType == EventDescriptor.EventType.MOUSE_SCROLL) {
            if (controlButtonPressedEvent == null) {
                return false;
            }
            scrollEvent = lastEvent.getAsScrollEvent();
            return true;
        }
        return false;
    }
}
