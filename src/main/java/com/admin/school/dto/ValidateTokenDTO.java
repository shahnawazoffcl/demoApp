package com.admin.school.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ValidateTokenDTO {
    public ValidateTokenDTO(String userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    public ValidateTokenDTO() {
    }

    private String userId;
    private String token;
}
