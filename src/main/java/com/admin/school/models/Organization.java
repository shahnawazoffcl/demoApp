package com.admin.school.models;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
public class Organization extends BaseModel {

    private String name;
    private String address;
    private String country;
    private String state;
    private String city;
    private String phone;
    private String email;
    private String password;
    private String profilePictureUrl;
    
    @OneToMany
    private List<User> followers = new ArrayList<>();

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Post> posts;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PostLike> likes;

    @OneToMany(mappedBy = "school", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserSchoolRelationship> userRelationships = new ArrayList<>();

}
