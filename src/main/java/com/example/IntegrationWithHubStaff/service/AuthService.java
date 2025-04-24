package com.example.IntegrationWithHubStaff.service;

import com.example.IntegrationWithHubStaff.dtos.TokenResponse;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;


public interface AuthService {


    void initiateOAuthFlow() throws IOException;

    TokenResponse exchangeCodeForToken(String code, String clientId);

    TokenResponse refreshToken(String clientId);

    void saveTokens(TokenResponse tokenResponse);

    String getAccessToken(String clientId);

     @Scheduled(fixedRate = 60*60*1000)
    void refreshTokensIfExpired();
}