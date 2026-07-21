package com.example.taskmanager.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

    /*
    TaskRequest is DTO

    DTO means Data Transfer Object

    This class represents the JSON data that a client sends
    when creating or updating a task.

    We use this instead of using the Task entity directly.
    Why?

    Task is our database model.
    TaskRequest is our API request model.

    This revents the client from controlling fields like id.
    The database should create the id automatically,
     */

public class TaskRequest {

    @NotBlank(message = "Title is required.")
    @Size(max = 100, message = "Title must be at most 100 characters.")
    private String title;

    @Size(max = 500, message = "Description must be at most 500 characters.")
    private String description;

    private boolean completed;

    public String getTitle(){
        return title;
    }

    public String getDescription(){
        return description;
    }

    public boolean isCompleted(){
        return completed;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void setCompleted(boolean completed){
        this.completed = completed;
    }

}
