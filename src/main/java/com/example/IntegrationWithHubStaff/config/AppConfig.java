package com.example.IntegrationWithHubStaff.config;

import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
@Data
public class AppConfig {
    @Value("${hubstaff.client.id}")
    private String clientId;

    @Value("${hubstaff.client.secret}")
    private String clientSecret;

    @Value("${hubstaff.redirect.uri}")
    private String redirectUri;

    @Value("${hubstaff.scope}")
    private String scope;

    @Value("${hubstaff.response.type}")
    private String responsetype;

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public ScheduledExecutorService scheduledExecutorService() {
        return Executors.newScheduledThreadPool(2);
    }
}