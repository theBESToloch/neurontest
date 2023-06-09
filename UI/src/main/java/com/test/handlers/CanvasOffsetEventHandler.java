package com.test.handlers;

import com.test.context.ApplicationContext;
import com.test.context.EventDescriptor;
import com.test.context.EventQueueHandler;
import com.test.events.NeedUpdateCanvasEvent;
import javafx.scene.input.MouseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Queue;

import static javafx.scene.input.MouseButton.SECONDARY;

@Slf4j
@Component
@RequiredArgsConstructor
public class CanvasOffsetEventHandler implements EventQueueHandler {

    public static final String OFFSET_CODE = "canvasOffset";

    private final ApplicationContext.CanvasWindowState state;
    private final ApplicationEventPublisher applicationEventPublisher;

    private MouseEvent preventMouseEvent = null;
    private MouseEvent currentMouseEvent = null;

    @Override
    public String getCode() {
        return OFFSET_CODE;
    }

    @Override
    public void handle(EventDescriptor lastEvent, Queue<EventDescriptor> eventQueue) {

        if (!isTriggered(lastEvent, eventQueue)) {
            return;
        }

        double delX = currentMouseEvent.getX() - preventMouseEvent.getX();
        double delY = currentMouseEvent.getY() - preventMouseEvent.getY();

        double xOffset = state.getXOffset() + delX;
        double yOffset = state.getYOffset() + delY;

        state.setXOffset(xOffset);
        state.setYOffset(yOffset);

        preventMouseEvent = currentMouseEvent;
        applicationEventPublisher.publishEvent(NeedUpdateCanvasEvent.INSTANT);
    }


    private boolean isTriggered(EventDescriptor lastEvent, Queue<EventDescriptor> eventQueue) {
        EventDescriptor.EventType eventType = lastEvent.getEventType();

        switch (eventType) {
            case MOUSE_PRESSED -> {
                MouseEvent asMouseEvent = lastEvent.getAsMouseEvent();
                if (asMouseEvent.getButton() == SECONDARY) {
                    preventMouseEvent = asMouseEvent;
                    return false;
                }
            }
            case MOUSE_RELEASED -> {
                if (preventMouseEvent == null) {
                    return false;
                }
                MouseEvent asMouseEvent = lastEvent.getAsMouseEvent();
                if (asMouseEvent.getButton() == SECONDARY) {
                    preventMouseEvent = null;
                    return false;
                }
            }
            case MOUSE_DRAGGED -> {
                if (preventMouseEvent == null) {
                    return false;
                }
                currentMouseEvent = lastEvent.getAsMouseEvent();
                return true;
            }
        }
        return false;
    }
}
