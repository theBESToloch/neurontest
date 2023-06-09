package com.test.context;


import java.util.Queue;

public interface EventHandlerExecutor {

    void execute(EventDescriptor lastEvent, Queue<EventDescriptor> eventQueue);
}
