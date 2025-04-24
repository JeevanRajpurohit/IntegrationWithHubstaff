package com.example.IntegrationWithHubStaff.controller;

import com.example.IntegrationWithHubStaff.dtos.AppDto;
import com.example.IntegrationWithHubStaff.service.AppService;
import com.example.IntegrationWithHubStaff.util.MessageUtil;
import com.example.IntegrationWithHubStaff.util.RequestValidationUtil;
import com.example.IntegrationWithHubStaff.util.ResponseHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/apps")
public class AppController {

    @Autowired
    private AppService appService;

    @Autowired
    private MessageUtil messageUtil;

    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<ResponseHandler> getAllAppsByOrganization(@PathVariable String organizationId) {
        try {
            RequestValidationUtil.checkNotNullOrBlank(organizationId, "organizationId");

            List<AppDto> uniqueAppNames = appService.getUniqueAppDtosByOrganization(organizationId);
            Map<String, Object> result = Map.of("organizationId", organizationId, "uniqueAppNames", uniqueAppNames, "appCount", uniqueAppNames.size()
            );

            return ResponseEntity.ok()
                    .body(new ResponseHandler(
                            result,
                            messageUtil.getMessage("app.usage.org.apps.success"),
                            HttpStatus.OK.value(),
                            true,
                            "orgApps"));
        } catch (Exception e) {
            log.error("getAllAppsByOrganization - Internal error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseHandler(
                            null,
                            e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            false,
                            "error"));
        }
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseHandler> getAllAppsByUser(@PathVariable String userId) {
        try {
            RequestValidationUtil.checkNotNullOrBlank(userId, "userId");

            List<AppDto> apps = appService.getAppsByUserId(userId);
            Map<String, Object> result = Map.of("userId", userId, "apps", apps, "appCount", apps.size());

            return ResponseEntity.ok()
                    .body(new ResponseHandler(
                            result,
                            messageUtil.getMessage("app.usage.user.apps.success"),
                            HttpStatus.OK.value(),
                            true,
                            "userApps"));
        } catch (Exception e) {
            log.error("getAllAppsByUser - Internal error", e);
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
