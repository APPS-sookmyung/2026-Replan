package com.yuan.replan.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "app_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String googleSub;
    private String email;
    private String name;

    protected User() {}

    public User(String googleSub, String email, String name) {
        this.googleSub = googleSub;
        updateProfile(email, name);
    }

    public void updateProfile(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public Long getId() { return id; }
    public String getGoogleSub() { return googleSub; }
    public String getEmail() { return email; }
    public String getName() { return name; }
}
