package com.test.configuration;

import com.test.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ContextConfiguration {

    @Bean
    public ApplicationContext applicationContext() {
        return new ApplicationContext();
    }

    @Bean
    public ApplicationContext.ManageWindowState manageWindowState(ApplicationContext applicationContext) {
        return applicationContext.new ManageWindowState();
    }

    @Bean
    public ApplicationContext.CanvasWindowState canvasWindowState(ApplicationContext applicationContext) {
        return applicationContext.new CanvasWindowState();
    }

    @Bean
    public ApplicationContext.LoadWindowState loadWindowState(ApplicationContext applicationContext) {
        return applicationContext.new LoadWindowState();
    }
}
