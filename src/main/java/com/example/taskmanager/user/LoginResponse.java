package com.example.taskmanager.user;
/*
LoginResponse is the DTO our API sends back after a successful login.

Notice:
We do NOT return the password
We do NOT return the passwordHash

For now, we return basic user information and a success message.
Later, we will add a JWT token here.
 */


public class LoginResponse {

    private Long id;
    private String name;
    private String email;
    private String message;

    public LoginResponse(Long id, String name, String email, String message){
        this.id = id;
        this.name = name;
        this.email = email;
        this.message = message;
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

    public String getMessage(){
        return message;
    }

}
