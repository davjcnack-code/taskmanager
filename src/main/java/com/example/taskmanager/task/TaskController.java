package com.example.taskmanager.task;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/*
TaskController handles HTTP/API requests.

This class does not directly talk to the database anymore.
Instead, it talks to TaskService.

Flow:

HTTP Request -> TaskController -> TaskService -> TaskRepository -> Database
*/
@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    /*
    Spring automatically gives the controller a TaskService object.
    */
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /*
    Handles:

        GET /tasks

    Returns all tasks.
    */
    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    /*
    Handles:

        GET /tasks/{id}

    Example:

        GET /tasks/1

    If the task exists, return 200 OK.
    If the task does not exist, return 404 Not Found.
    */
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id)
                .map(task -> ResponseEntity.ok(task))
                .orElse(ResponseEntity.notFound().build());
    }

    /*
    Handles:

        POST /tasks

    @Valid checks validation rules in Task.java.
    @RequestBody turns JSON into a Task object.
    */
    @PostMapping
    public Task createTask(@Valid @RequestBody TaskRequest taskRequest) {
        return taskService.createTask(taskRequest);
    }

    /*
    Handles:

        PUT /tasks/{id}

    Updates an existing task.
    */
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequest taskRequest) {
        return taskService.updateTask(id, taskRequest)
                .map(task -> ResponseEntity.ok(task))
                .orElse(ResponseEntity.notFound().build());
    }

    /*
    Handles:

        DELETE /tasks/{id}

    Deletes a task if it exists.
    */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        boolean deleted = taskService.deleteTask(id);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}
