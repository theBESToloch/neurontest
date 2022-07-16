package com.test.context;


public interface EventHandler {

    String getCode();
    void handle(ButtonClickState buttonClickState);
}
