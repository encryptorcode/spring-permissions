package com.example.myshop.entities;

import com.example.myshop.data.Entity;

public class User extends Entity {
    private Long id;
    private String name;
    private String email;
    private String profileImage;
    private Role role;

    public enum Role{
        USER,
        ADMIN
    }

    public User() {
    }

    public User(Long id, String name, String email, String profileImage, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.profileImage = profileImage;
        this.role = role;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
