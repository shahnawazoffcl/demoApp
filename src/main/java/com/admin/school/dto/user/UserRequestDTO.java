package com.admin.school.dto.user;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserRequestDTO {

    private String email;
    private String password;
    private String name;

}
