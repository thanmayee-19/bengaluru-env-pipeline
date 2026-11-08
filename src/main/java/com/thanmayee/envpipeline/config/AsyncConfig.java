package com.thanmayee.envpipeline.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AsyncConfig {

    @Bean(name = "ingestionExecutor", destroyMethod = "shutdown")
    public ExecutorService ingestionExecutor() {
        return Executors.newFixedThreadPool(6);
    }
}
