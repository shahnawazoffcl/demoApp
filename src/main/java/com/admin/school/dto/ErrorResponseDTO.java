package com.admin.school.dto;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErrorResponseDTO {
    private final String type = "ERROR";
    private String message;
    private int messageCode;
}
