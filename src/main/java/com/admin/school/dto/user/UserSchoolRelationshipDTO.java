package com.admin.school.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserSchoolRelationshipDTO {
    private String id;
    private String userId;
    private String schoolId;
    private String schoolName;
    private String role;
    private String status;
    private Date startDate;
    private Date endDate;
    private String grade;
    private String subject;
    private String department;
    @JsonProperty("isCurrent")
    private boolean current;
} 