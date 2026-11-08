package com.thanmayee.envpipeline.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient.Builder restClientBuilder() {

        SimpleClientHttpRequestFactory requestFactory =
                new SimpleClientHttpRequestFactory();

        // Maximum time to establish a connection.
        requestFactory.setConnectTimeout(Duration.ofSeconds(5));

        // Maximum time waiting for the API response.
        requestFactory.setReadTimeout(Duration.ofSeconds(5));

        return RestClient.builder()
                .requestFactory(requestFactory);
    }
}
