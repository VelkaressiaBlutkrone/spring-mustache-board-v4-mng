package com.example.v4.global.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${app.rest-client.base-url}")
    private String baseUrl;

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @Bean
    public RestClient.Builder restClientBuilder() {
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory();
        factory.setReadTimeout(Duration.ofSeconds(10));

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory);
    }
}
