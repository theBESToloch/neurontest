package com.test.handlers;

import com.test.context.ApplicationContext;
import com.test.context.ButtonClickState;
import com.test.context.EventHandler;
import com.test.data.NeuronGraph;
import com.test.events.NeedUpdateCanvasEvent;
import javafx.scene.input.MouseEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NeuronMoveEventHandler implements EventHandler {
    public static final String NEURON_MOVE_CODE = "neuronMove";
    private final ApplicationContext.CanvasWindowState state;
    private final ApplicationEventPublisher applicationEventPublisher;


    public NeuronMoveEventHandler(ApplicationContext.CanvasWindowState state,
                                  ApplicationEventPublisher applicationEventPublisher) {
        this.state = state;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public String getCode() {
        return NEURON_MOVE_CODE;
    }

    @Override
    public void handle(ButtonClickState buttonClickState) {
        MouseEvent pressedMouseEvent = buttonClickState.getPressedMouseEvent();
        MouseEvent currentMouseEvent = buttonClickState.getCurrentMouseEvent();

        double delX = currentMouseEvent.getX() - pressedMouseEvent.getX();
        double delY = currentMouseEvent.getY() - pressedMouseEvent.getY();

        List<NeuronGraph> pressedNeurons = state.getSelectNeurons();

        for (NeuronGraph pressedNeuron : pressedNeurons) {
            pressedNeuron.setX(pressedNeuron.getX() + delX);
            pressedNeuron.setY(pressedNeuron.getY() + delY);
        }

        applicationEventPublisher.publishEvent(new NeedUpdateCanvasEvent());
    }
}
