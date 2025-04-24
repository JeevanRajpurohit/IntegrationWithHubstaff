package com.example.IntegrationWithHubStaff.controller;

import com.example.IntegrationWithHubStaff.config.AppConfig;
import com.example.IntegrationWithHubStaff.dtos.TokenResponse;
import com.example.IntegrationWithHubStaff.exception.BadRequestException;
import com.example.IntegrationWithHubStaff.service.AuthService;
import com.example.IntegrationWithHubStaff.util.MessageUtil;
import com.example.IntegrationWithHubStaff.util.RequestValidationUtil;
import com.example.IntegrationWithHubStaff.util.ResponseHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {
       @Autowired
       private AuthService authService;

       @Autowired
       private AppConfig hubstaffConfig;

       @Autowired
       private MessageUtil messageUtil;

       @GetMapping("/authorize")
       public ResponseEntity<ResponseHandler> authorize() {
              try {
                     authService.initiateOAuthFlow();
                     return ResponseEntity.ok()
                             .body(new ResponseHandler(
                                     null,
                                     messageUtil.getMessage("auth.authorize.success"),
                                     HttpStatus.OK.value(),
                                     true,
                                     "authorization"));
              } catch (IOException e) {
                     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(new ResponseHandler(
                                     null,
                                     e.getMessage(),
                                     HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                     false,
                                     "error"));
              }
       }

       @GetMapping("/callback")
       public ResponseEntity<ResponseHandler> callback(@RequestParam String code) {
              try {
                     RequestValidationUtil.checkNotNullOrBlank(code, "code");
                     TokenResponse tokenResponse = authService.exchangeCodeForToken(code, hubstaffConfig.getClientId());
                     authService.saveTokens(tokenResponse);
                     return ResponseEntity.ok()
                             .body(new ResponseHandler(
                                     tokenResponse,
                                     messageUtil.getMessage("auth.callback.success"),
                                     HttpStatus.OK.value(),
                                     true,
                                     "tokens"));
              } catch (BadRequestException e) {
                     log.error("callback - Bad request: {}", e.getMessage(), e);
                     return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseHandler(
                             null,
                             e.getMessage(),
                             HttpStatus.BAD_REQUEST.value(),
                             false,
                             "error"));
              } catch (Exception e) {
                     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(new ResponseHandler(
                                     null,
                                     e.getMessage(),
                                     HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                     false,
                                     "error"));
              }
       }
}