package com.example.IntegrationWithHubStaff.controller;

import com.example.IntegrationWithHubStaff.dtos.organizationResponseDto.OrganizationResponse;
import com.example.IntegrationWithHubStaff.service.OrganizationService;
import com.example.IntegrationWithHubStaff.util.MessageUtil;
import com.example.IntegrationWithHubStaff.util.ResponseHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private MessageUtil messageUtil;

    @GetMapping
    public ResponseEntity<ResponseHandler> getOrganizations() {
        try {
            List<OrganizationResponse> organizations = organizationService.getAllOrganizations();
            return ResponseEntity.ok()
                    .body(new ResponseHandler(
                            organizations,
                            messageUtil.getMessage("org.success"),
                            HttpStatus.OK.value(),
                            true,
                            "organizations"));
        } catch (Exception e) {
            log.error("getOrganizations - Internal error", e);
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
