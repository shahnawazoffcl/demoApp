package com.admin.school.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Entity
@Setter
@Getter
public class UserSchoolRelationship extends BaseModel {

    @ManyToOne
    private User user;

    @ManyToOne
    private Organization school;

    @Enumerated(EnumType.STRING)
    private UserSchoolRole role; // STUDENT, TEACHER, ADMIN, etc.

    @Enumerated(EnumType.STRING)
    private RelationshipStatus status; // ACTIVE, INACTIVE, GRADUATED, TRANSFERRED

    private Date startDate;
    private Date endDate; // null if currently active

    private String grade; // For students: "10th Grade", "11th Grade", etc.
    private String subject; // For teachers: "Mathematics", "Science", etc.
    private String department; // For teachers: "Science Department", etc.

    private boolean isCurrent; // true if this is the current school
} 