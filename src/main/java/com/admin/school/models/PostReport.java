package com.admin.school.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "post_reports", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"post_id", "reported_by_user_id"})
})
public class PostReport extends BaseModel {

    @ManyToOne(optional = false)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(optional = false)
    @JoinColumn(name = "reported_by_user_id")
    private User reportedBy;

    @Enumerated(EnumType.STRING)
    private ReportReason reason;

    @Enumerated(EnumType.STRING)
    private ReportStatus status = ReportStatus.OPEN;
} 