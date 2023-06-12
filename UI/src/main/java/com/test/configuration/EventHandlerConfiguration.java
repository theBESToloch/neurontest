package com.test.configuration;

import com.test.context.EventQueueHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class EventHandlerConfiguration {

    @Bean("eventQueueHandler")
    public Map<String, EventQueueHandler> eventQueueHandlerMap(List<EventQueueHandler> eventHandlerList) {
        return eventHandlerList
                .stream()
                .collect(Collectors.toMap(EventQueueHandler::getCode, Function.identity()));
    }
}
