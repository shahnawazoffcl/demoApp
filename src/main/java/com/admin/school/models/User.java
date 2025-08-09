package com.admin.school.models;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
public class User extends BaseModel {

    private String username;
    private String password;
    private String email;
    private String profilePictureUrl;
    private String role;
    private String phone;
    private String address;
    
    @ManyToMany
    private List<User> connections = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Post> posts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PostLike> likes;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserSchoolRelationship> schoolRelationships = new ArrayList<>();

    @Column(columnDefinition = "varchar(15) default 'CREATED'", insertable = false)
    private String profileStatus;

}
