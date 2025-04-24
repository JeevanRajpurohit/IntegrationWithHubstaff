package com.example.IntegrationWithHubStaff.util;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseHandler {
     private Object data;
     private String message;
     private int status;
     private Boolean success;
     private String entity;
}
