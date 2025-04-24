package com.example.IntegrationWithHubStaff.controller;

import com.example.IntegrationWithHubStaff.dtos.userResponseDto.UserResponse;
import com.example.IntegrationWithHubStaff.exception.BadRequestException;
import com.example.IntegrationWithHubStaff.service.UserService;
import com.example.IntegrationWithHubStaff.util.MessageUtil;
import com.example.IntegrationWithHubStaff.util.ResponseHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageUtil messageUtil;

    @GetMapping
    public ResponseEntity<ResponseHandler> getUsersByOrganization(@RequestParam String organizationId) {
        try {
            List<UserResponse> users = userService.getUsersByOrganizationId(organizationId);

            return ResponseEntity.ok(
                    new ResponseHandler(
                            users,
                            messageUtil.getMessage("user.success"),
                            HttpStatus.OK.value(),
                            true,
                            "users"
                    )
            );
        } catch (BadRequestException e) {
            log.error("getUsersByOrganization - Bad request", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseHandler(
                            null,
                            e.getMessage(),
                            HttpStatus.BAD_REQUEST.value(),
                            false,
                            "error"));
        } catch (Exception e) {
            log.error("getUsersByOrganization - Internal error", e);
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
