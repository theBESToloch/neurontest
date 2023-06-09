package com.test.context;

import java.util.LinkedList;
import java.util.Queue;

public class ButtonClickState {

    private final EventHandlerExecutor eventHandlerExecutor;

    private final Queue<EventDescriptor> eventQueue;

    public ButtonClickState(EventHandlerExecutor eventHandlerExecutor, int maxEventQueueElement) {
        this.eventHandlerExecutor = eventHandlerExecutor;
        eventQueue = new LinkedList<>() {
            @Override
            public boolean add(EventDescriptor eventDescriptor) {
                if (this.size() == maxEventQueueElement) {
                    this.remove();
                }
                return super.add(eventDescriptor);
            }
        };
    }

    public void addEvent(EventDescriptor eventDescriptor) {
        eventQueue.add(eventDescriptor);
        eventHandlerExecutor.execute(eventDescriptor, eventQueue);
    }
}
