package com.example.taskmanager.task;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/*"Entity tells Spring/JPA." create a database table for this class.*/
@Entity
public class Task {
    /*Marks as the primary key. */
    @Id
    /*This tells the database to automatically create the id value
    for each new task.
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*These fields become columns in the database table.*/


    private String title;

    private String description;

    private boolean completed;

    /*The empty constructor is required by JPA. The JPA needs it so it can
    * create Task object when reading from the database.*/
    public Task(){

    }
    public Task(String title, String description, boolean completed){
        this.title = title;
        this.description = description;
        this.completed = completed;
    }
    /*The getters and setter lets Spring/JPA read and change the private fields.*/
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
    public void setId(Long id){
        this.id = id;
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
