package com.admin.school.dto.user;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserRequestGoogleDTO {
    private String token;
    private String email;
    private String name;
    private String picture;
    private String givenName;
}
