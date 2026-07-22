package com.example.taskmanager.user;

import java.time.LocalDateTime;

/*
UserResponse is the DTO our API sends back.

Notice: it does Not include passwordHash.
We never sends password information back to the client.
*/
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private LocalDateTime createdAt;

    public UserResponse(Long id, String name, String email, LocalDateTime createdAt){
        this.id = id;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;

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

    public LocalDateTime getCreatedAt(){
        return createdAt;
    }

}
