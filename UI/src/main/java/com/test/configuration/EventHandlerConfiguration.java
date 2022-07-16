package com.test.configuration;

import com.test.context.EventHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class EventHandlerConfiguration {

    @Bean("eventHandlers")
    public Map<String, EventHandler> eventHandlerMap(List<EventHandler> eventHandlerList){
        return eventHandlerList
                .stream()
                .collect(Collectors.toMap(EventHandler::getCode, Function.identity()));
    }
}
