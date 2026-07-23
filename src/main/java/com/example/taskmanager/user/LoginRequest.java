package com.example.taskmanager.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/*
LoginRequest is the DTO for Logging in.

This class represents the JSON data the client sends
when trying to long into an account.
 */
public class LoginRequest {

    @NotBlank(message = "Email is required.")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required.")
    private String password;

    public String getEmail() {
        return email;
    }

    public String getPassword(){
    return password;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setPassword(String password){
        this.password = password;
    }

}
