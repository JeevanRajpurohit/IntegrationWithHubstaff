package com.example.IntegrationWithHubStaff.controller;

import com.example.IntegrationWithHubStaff.dtos.appTimeSpentDto.AppTimeActivityDto;
import com.example.IntegrationWithHubStaff.exception.BadRequestException;
import com.example.IntegrationWithHubStaff.model.UserApp;
import com.example.IntegrationWithHubStaff.repository.UserAppRepository;
import com.example.IntegrationWithHubStaff.service.HubstaffAPIService;
import com.example.IntegrationWithHubStaff.util.MessageUtil;
import com.example.IntegrationWithHubStaff.util.RequestValidationUtil;
import com.example.IntegrationWithHubStaff.util.ResponseHandler;
import com.example.IntegrationWithHubStaff.validations.Groups;
import jakarta.validation.groups.Default;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/app-usage")
public class AppUsageController {
    @Autowired
    private HubstaffAPIService hubstaffAPIService;

    @Autowired
    private MessageUtil messageUtil;

    @PostMapping("/user")
    public ResponseEntity<ResponseHandler> getUserAppTime(
            @Validated({Default.class, Groups.FetchAppTimeByUser.class}) @RequestBody AppTimeActivityDto request) {
        try {
            Map<String, Integer> result = hubstaffAPIService.getTimeSpentByUserOnApp(
                    request.getUserId(), request.getAppName());
            return ResponseEntity.ok()
                    .body(new ResponseHandler(
                            result,
                            messageUtil.getMessage("app.usage.user.success"),
                            HttpStatus.OK.value(),
                            true,
                            "userAppTime"));
        } catch (BadRequestException e) {
            log.error("getUserAppTime - Bad request: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseHandler(null, e.getMessage(), HttpStatus.BAD_REQUEST.value(), false, "error"));
        } catch (Exception e) {
            log.error("getUserAppTime - Internal error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseHandler(null, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), false, "error"));
        }
    }

    @PostMapping("/organization")
    public ResponseEntity<ResponseHandler> getOrganizationAppTime(
            @Validated({Default.class, Groups.FetchAppTimeByOrganization.class}) @RequestBody AppTimeActivityDto request) {
        try {
            Map<String, Integer> result = hubstaffAPIService.getTimeSpentByOrganizationOnApp(
                    request.getOrganizationId(), request.getAppName());
            return ResponseEntity.ok()
                    .body(new ResponseHandler(
                            result,
                            messageUtil.getMessage("app.usage.org.success"),
                            HttpStatus.OK.value(),
                            true,
                            "orgAppTime"));
        } catch (BadRequestException e) {
            log.error("getOrganizationAppTime - Bad request: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseHandler(null, e.getMessage(), HttpStatus.BAD_REQUEST.value(), false, "error"));
        } catch (Exception e) {
            log.error("getOrganizationAppTime - Internal error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseHandler(null, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), false, "error"));
        }
    }

    @PostMapping("/project")
    public ResponseEntity<ResponseHandler> getProjectAppTime(
            @Validated({Default.class, Groups.FetchAppTimeByProject.class}) @RequestBody AppTimeActivityDto request) {
        try {
            Map<String, Integer> result = hubstaffAPIService.getTimeSpentByProjectOnApp(
                    request.getProjectId(), request.getAppName());
            return ResponseEntity.ok()
                    .body(new ResponseHandler(
                            result,
                            messageUtil.getMessage("app.usage.project.success"),
                            HttpStatus.OK.value(),
                            true,
                            "projectAppTime"));
        } catch (BadRequestException e) {
            log.error("getProjectAppTime - Bad request", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseHandler(null, e.getMessage(), HttpStatus.BAD_REQUEST.value(), false, "error"));
        } catch (Exception e) {
            log.error("getProjectAppTime - Internal error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseHandler(null, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), false, "error"));
        }
    }

//    @GetMapping("/organization/{organizationId}/apps")
//    public ResponseEntity<ResponseHandler> getAllAppsByOrganization(@PathVariable String organizationId) {
//        try {
//            RequestValidationUtil.checkNotNullOrBlank(organizationId, "organizationId");
//            List<UserApp> allApps = userAppRepository.findByOrganizationId(organizationId);
//            Set<String> uniqueAppNames = allApps.stream()
//                    .map(UserApp::getAppName)
//                    .collect(Collectors.toSet());
//
//            Map<String, Object> result = Map.of("organizationId", organizationId, "uniqueAppNames", uniqueAppNames, "appCount", uniqueAppNames.size());
//
//            return ResponseEntity.ok()
//                    .body(new ResponseHandler(
//                            result,
//                            messageUtil.getMessage("app.usage.org.apps.success"),
//                            HttpStatus.OK.value(),
//                            true,
//                            "orgApps"));
//        } catch (Exception e) {
//            log.error("getAllAppsByOrganization - Internal error", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new ResponseHandler(
//                            null,
//                            e.getMessage(),
//                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                            false,
//                            "error"));
//        }
//    }
//
//    @GetMapping("/user/{userId}/apps")
//    public ResponseEntity<ResponseHandler> getAllAppsByUser(@PathVariable String userId) {
//        try {
//            RequestValidationUtil.checkNotNullOrBlank(userId,"userId");
//            List<UserApp> apps = userAppRepository.findByUserId(userId);
//            Map<String, Object> result = Map.of("userId", userId, "apps", apps, "appCount", apps.size());
//
//            return ResponseEntity.ok()
//                    .body(new ResponseHandler(
//                            result,
//                            messageUtil.getMessage("app.usage.user.apps.success"),
//                            HttpStatus.OK.value(),
//                            true,
//                            "userApps"));
//        } catch (Exception e) {
//            log.error("getAllAppsByUser - Internal error", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new ResponseHandler(
//                            null,
//                            e.getMessage(),
//                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                            false,
//                            "error"));
//        }
//    }
}