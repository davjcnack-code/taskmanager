package com.example.taskmanager.task;

import java.time.LocalDateTime;

    /*
    TaskResponse is a DTO

    DTO means Data Transfer Object

    This class represents the data our API sends back to the client.

    We use this instead of returning the Task entity directly.

    Task = database model
    TaskRequest = data coming into the API
    TaskResponse = data going out of the API
    */

public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private boolean completed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TaskResponse(Long id, String title, String description, boolean completed, LocalDateTime createdAt, LocalDateTime updatedAt){
        this.id = id;
        this.title = title;
        this.description = description;
        this.completed = completed;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId(){
        return id;
    }

    public String getTitle(){
        return title;
    }

    public String getDescription(){
        return description;
    }

    public boolean isCompleted(){
        return completed;
    }

    public LocalDateTime getCreatedAt(){
        return createdAt;
    }

    public LocalDateTime getUpdatedAt(){
        return updatedAt;
    }



}
