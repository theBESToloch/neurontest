package com.test.context;


import java.util.Queue;

public interface EventQueueHandler {

    String getCode();
    void handle(EventDescriptor lastEvent, Queue<EventDescriptor> eventQueue);
}
