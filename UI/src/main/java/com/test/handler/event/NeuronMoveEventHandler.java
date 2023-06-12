package com.test.handler.event;

import com.test.common.data.dto.NeuronGraph;
import com.test.context.ApplicationContext;
import com.test.context.EventDescriptor;
import com.test.context.EventQueueHandler;
import com.test.event.NeedUpdateCanvasEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.Set;

import static javafx.scene.input.KeyCode.ALT;

@Slf4j
@Component
@RequiredArgsConstructor
public class NeuronMoveEventHandler implements EventQueueHandler {
    public static final String NEURON_MOVE_CODE = "neuronMove";
    private final ApplicationContext.CanvasWindowState state;
    private final ApplicationEventPublisher applicationEventPublisher;

    private MouseEvent preventMouseEvent = null;

    @Override
    public String getCode() {
        return NEURON_MOVE_CODE;
    }

    @Override
    public void handle(EventDescriptor lastEvent, Queue<EventDescriptor> eventQueue) {
        Set<NeuronGraph> pressedNeurons;

        if ((pressedNeurons = state.getSelectNeurons()).isEmpty()
                || !isTriggered(lastEvent, eventQueue)) {
            return;
        }

        MouseEvent currentMouseEvent = lastEvent.getAsMouseEvent();

        double delX = currentMouseEvent.getX() - preventMouseEvent.getX();
        double delY = currentMouseEvent.getY() - preventMouseEvent.getY();

        for (NeuronGraph pressedNeuron : pressedNeurons) {
            pressedNeuron.setX(pressedNeuron.getX() + delX);
            pressedNeuron.setY(pressedNeuron.getY() + delY);
        }

        preventMouseEvent = currentMouseEvent;
        applicationEventPublisher.publishEvent(NeedUpdateCanvasEvent.INSTANT);

    }

    private KeyEvent altButtonPressedEvent;
    private MouseEvent pressedMouseEvent;


    private boolean isTriggered(EventDescriptor lastEvent, Queue<EventDescriptor> eventQueue) {
        EventDescriptor.EventType eventType = lastEvent.getEventType();

        switch (eventType){

            case BUTTON_PRESSED -> {
                if (altButtonPressedEvent != null) {
                    return false;
                }
                KeyEvent asKeyEvent = lastEvent.getAsKeyEvent();
                if (asKeyEvent.getCode() == ALT) {
                    altButtonPressedEvent = asKeyEvent;
                    return false;
                }
            }
            case BUTTON_RELEASED -> {
                if (altButtonPressedEvent == null) {
                    return false;
                }
                KeyEvent asKeyEvent = lastEvent.getAsKeyEvent();
                if (asKeyEvent.getCode() == ALT) {
                    altButtonPressedEvent = null;
                    pressedMouseEvent = null;
                }
                return false;
            }
            case MOUSE_PRESSED -> {
                if (altButtonPressedEvent == null) {
                    return false;
                }
                MouseEvent asMouseEvent = lastEvent.getAsMouseEvent();
                if (asMouseEvent.getButton() == MouseButton.PRIMARY) {
                    pressedMouseEvent = asMouseEvent;
                    preventMouseEvent = asMouseEvent;
                    return true;
                }
            }
            case MOUSE_DRAGGED -> {
                if (altButtonPressedEvent == null || pressedMouseEvent == null) {
                    return false;
                }
                return true;
            }
            case MOUSE_RELEASED -> {
                if (altButtonPressedEvent == null) {
                    return false;
                }
                MouseEvent asMouseEvent = lastEvent.getAsMouseEvent();
                if (asMouseEvent.getButton() == MouseButton.PRIMARY) {
                    pressedMouseEvent = null;
                }
            }
        }
        return false;
    }
}
