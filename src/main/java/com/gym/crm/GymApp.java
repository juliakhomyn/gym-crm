package com.gym.crm;

import com.gym.crm.config.AppConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@Slf4j
public class GymApp {

    public static void main( String[] args ) {
        try {
            ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

            log.info("Spring context started");
        } catch (Exception e) {
            log.error("Application failed to start", e);
        }
    }
}
