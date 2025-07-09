package com.admin.school.models;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class OrgSession extends BaseModel{
    private String token;
    @ManyToOne
    private Organization organization;
    @Enumerated(EnumType.ORDINAL)
    private SessionStatus  sessionStatus;
    private Date expiryAt;

}