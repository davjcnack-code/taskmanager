package com.example.taskmanager.task;

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

    public TaskResponse(Long id, String title, String description, boolean completed){
        this.id = id;
        this.title = title;
        this.description = description;
        this.completed = completed;
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





}
