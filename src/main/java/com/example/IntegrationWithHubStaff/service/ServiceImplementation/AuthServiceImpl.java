package com.example.IntegrationWithHubStaff.service.ServiceImplementation;

import com.example.IntegrationWithHubStaff.config.AppConfig;
import com.example.IntegrationWithHubStaff.dtos.TokenResponse;
import com.example.IntegrationWithHubStaff.exception.EntityNotFoundException;
import com.example.IntegrationWithHubStaff.exception.OAuthException;
import com.example.IntegrationWithHubStaff.exception.UnsupportedOSException;
import com.example.IntegrationWithHubStaff.model.AuthToken;
import com.example.IntegrationWithHubStaff.repository.AuthTokenRepository;
import com.example.IntegrationWithHubStaff.service.AuthService;
import com.example.IntegrationWithHubStaff.util.HubstaffApiConstants;
import com.example.IntegrationWithHubStaff.util.MessageUtil;
import com.example.IntegrationWithHubStaff.util.RequestValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.*;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Autowired private AppConfig hubstaffConfig;
    @Autowired private AuthTokenRepository authTokenRepository;
    @Autowired private RestTemplate restTemplate;
    @Autowired private ModelMapper modelMapper;
    @Autowired private MessageUtil messageUtil;

    @Override
    public void initiateOAuthFlow() throws IOException {
        String state = UUID.randomUUID().toString();
        String nonce = UUID.randomUUID().toString();

        String finalUrl = HubstaffApiConstants.AUTH_URL +
                "?client_id=" + hubstaffConfig.getClientId() +
                "&response_type=" + HubstaffApiConstants.OAUTH_RESPONSE_TYPE +
                "&nonce=" + nonce +
                "&redirect_uri=" + hubstaffConfig.getRedirectUri() +
                "&scope=" + hubstaffConfig.getScope() +
                "&state=" + state;

        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + finalUrl);
        } else if (os.contains("mac")) {
            Runtime.getRuntime().exec("open " + finalUrl);
        } else if (os.contains("nix") || os.contains("nux")) {
            Runtime.getRuntime().exec("xdg-open " + finalUrl);
        } else {
            log.error("Unsupported OS: " + os);
            throw new UnsupportedOSException("Unsupported OS for browser launch: " + os);
        }
    }

    @Override
    public TokenResponse exchangeCodeForToken(String code, String clientId) {
        RequestValidationUtil.checkNotNullOrBlank(code, "Authorization code");
        RequestValidationUtil.checkNotNullOrBlank(clientId, "Client ID");

        try {
            log.info("Exchanging code for tokens for client: {}", clientId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setBasicAuth(clientId, hubstaffConfig.getClientSecret());

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", HubstaffApiConstants.OAUTH_GRANT_TYPE);
            body.add("code", code);
            body.add("redirect_uri", hubstaffConfig.getRedirectUri());

            ResponseEntity<TokenResponse> response = restTemplate.postForEntity(
                    HubstaffApiConstants.TOKEN_URL,
                    new HttpEntity<>(body, headers),
                    TokenResponse.class);

            if (response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new RuntimeException(messageUtil.getMessage("auth.token.invalid.response"));
            }

            log.info("Token response received: {}", response.getBody());
            return response.getBody();
        } catch (HttpClientErrorException e) {
            String errorDetails = e.getResponseBodyAsString();
            log.error("OAuth token exchange failed: {} - {}", e.getStatusCode(), errorDetails);
            throw new OAuthException("OAuth token exchange failed: " + errorDetails);
        } catch (Exception e) {
            log.error("Token exchange failed for client: {}", clientId, e);
            throw new OAuthException("Token exchange failed: " + e.getMessage());
        }
    }

    @Override
    public void saveTokens(TokenResponse tokenResponse) {
        RequestValidationUtil.checkNotNull(tokenResponse, "Token Response");
        long currentTime = System.currentTimeMillis();
        Date now = new Date();

        AuthToken authToken = new AuthToken();
        authToken.setClientId(hubstaffConfig.getClientId());
        authToken.setAccessToken(tokenResponse.getAccessToken());
        authToken.setRefreshToken(tokenResponse.getRefreshToken());
        authToken.setExpiresAt(currentTime + (tokenResponse.getExpiresIn() * 1000));
        authToken.setCreatedAt(now);
        authToken.setModifiedAt(now);

        authTokenRepository.save(authToken);
    }

    @Override
    public TokenResponse refreshToken(String clientId) {
        RequestValidationUtil.checkNotNullOrBlank(clientId, "Client ID");

        AuthToken authToken = authTokenRepository.findByClientId(clientId);
        if (RequestValidationUtil.isNullOrEmpty(authToken) || RequestValidationUtil.isNullOrEmpty(authToken.getRefreshToken())) {
            throw new EntityNotFoundException("No refresh token found for client: " + clientId);
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setBasicAuth(clientId, hubstaffConfig.getClientSecret());

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("grant_type", HubstaffApiConstants.REFRESH_GRANT_TYPE);
            map.add("refresh_token", authToken.getRefreshToken());

            ResponseEntity<TokenResponse> response = restTemplate.postForEntity(
                    HubstaffApiConstants.TOKEN_URL,
                    new HttpEntity<>(map, headers),
                    TokenResponse.class);

            RequestValidationUtil.checkNotNull(response.getBody(), "Refresh Token Response");

            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("OAuth error during token refresh: {}", e.getResponseBodyAsString());
            throw new OAuthException("OAuth error: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Refresh failed for client: {}", clientId, e);
            throw new OAuthException("Token refresh failed: " + e.getMessage());
        }
    }

    @Override
    public String getAccessToken(String clientId) {
        RequestValidationUtil.checkNotNullOrBlank(clientId, "Client ID");

        AuthToken authToken = authTokenRepository.findByClientId(clientId);
        if (RequestValidationUtil.isNullOrEmpty(authToken)) {
            throw new EntityNotFoundException("No access token found for organization: " + clientId);
        }
        return authToken.getAccessToken();
    }

    @Override
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void refreshTokensIfExpired() {
        log.info("Running scheduled access/refresh token maintenance job");

        try {
            AuthToken authToken = authTokenRepository.findByClientId(hubstaffConfig.getClientId());

            if (authToken == null) {
                log.warn("No auth token found for client: {}", hubstaffConfig.getClientId());
                return;
            }

            long currentTime = System.currentTimeMillis();
            long accessExpiresAt = authToken.getExpiresAt() - 3660000L;
            boolean isAccessExpired = currentTime >= accessExpiresAt;

            long refreshCreatedAt = authToken.getCreatedAt().getTime();
            long refreshExpiresAt = refreshCreatedAt + (29L * 24 * 60 * 60 * 1000);
            boolean isRefreshExpired = currentTime >= refreshExpiresAt;

            if (isAccessExpired || isRefreshExpired) {
                log.info("Token for client {} expired/expiring. Refreshing...", authToken.getClientId());
                TokenResponse newTokens = refreshToken(authToken.getClientId());
                saveTokens(newTokens);
                log.info("Successfully refreshed tokens for client {}", authToken.getClientId());
            } else {
                log.info("Token for client {} is still valid. No refresh needed.", authToken.getClientId());
            }
        } catch (Exception e) {
            log.error("Error during scheduled token refresh job", e);
        }
    }
}
