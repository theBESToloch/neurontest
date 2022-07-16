package com.test.context;

import com.test.context.checker.Checker;
import com.test.context.checker.FullCheck;
import com.test.context.checker.KeyCheck;
import com.test.context.checker.MouseCheck;
import javafx.scene.input.KeyCode;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class EventHandlerRegistrarImpl implements EventHandlerRegistrar, EventHandlerExecutor {

    private final Map<Checker, EventHandlerTrigger> checkers;

    public EventHandlerRegistrarImpl() {
        checkers = new HashMap<>();
    }

    @Override
    public void register(EventHandlerTrigger eventHandlerTrigger) {
        KeyCode[] keyForTrigger = eventHandlerTrigger.getKeyForTrigger();
        MouseEventCode[] mouseForTrigger = eventHandlerTrigger.getMouseForTrigger();
        if ((keyForTrigger == null || keyForTrigger.length == 0) &&
                (mouseForTrigger == null || mouseForTrigger.length == 0)) {
            return;
        }
        if (keyForTrigger != null && mouseForTrigger != null) {
            checkers.put(new FullCheck(keyForTrigger, mouseForTrigger), eventHandlerTrigger);
        } else if (keyForTrigger != null) {
            checkers.put(new KeyCheck(keyForTrigger), eventHandlerTrigger);
        } else {
            checkers.put(new MouseCheck(mouseForTrigger), eventHandlerTrigger);
        }
    }

    public void executeIfApproach(KeyCode[] keys, MouseEventCode[] mouseKeys, ButtonClickState buttonClickState) {
        checkers.forEach((checker, eht) -> {
            if (checker.check(keys, mouseKeys)) {
                eht.handle(buttonClickState);
            }
        });
    }

}
