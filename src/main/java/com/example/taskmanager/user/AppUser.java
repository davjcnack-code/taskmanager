package com.example.taskmanager.user;

import jakarta.persistence.*;

import java.time.LocalDateTime;


/*
AppUser is the database entity for user accounts.

We use the name AppUser instead of User because "User" can conflict
with database keywords.

The table name is app_users.

We store passwordHask, not the real password.
 */
@Entity
@Table(name = "app_users")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private String passwordHash;

    private LocalDateTime createdAt;

    public AppUser(){
    }

    public AppUser(String name, String email, String passwordHash){
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    @PrePersist
    public void onCreate(){
        this.createdAt = LocalDateTime.now();
    }

    public Long getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public String getEmail(){
        return email;
    }

    public String getPasswordHash(){
        return passwordHash;
    }

    public LocalDateTime getCreatedAt(){
        return createdAt;
    }

    public void setId(Long id){
        this.id = id;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setPasswordHash(String passwordHash){
        this.passwordHash = passwordHash;
    }

}
