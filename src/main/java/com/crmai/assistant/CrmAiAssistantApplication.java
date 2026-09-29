package com.crmai.assistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CrmAiAssistantApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrmAiAssistantApplication.class, args);
    }
}
