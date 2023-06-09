package com.test.context;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

@Component
public class EventHandlerRegistrarImpl implements EventHandlerRegistrar, EventHandlerExecutor {

    private final Set<EventQueueHandler> queueCheckers;

    public EventHandlerRegistrarImpl() {
        queueCheckers = new HashSet<>();
    }

    @Override
    public void register(EventQueueHandler eventHandler) {
        queueCheckers.add(eventHandler);
    }

    @Override
    public void execute(EventDescriptor lastEvent, Queue<EventDescriptor> eventQueue) {
        queueCheckers.forEach(eventQueueHandler -> eventQueueHandler.handle(lastEvent, eventQueue));
    }

}
