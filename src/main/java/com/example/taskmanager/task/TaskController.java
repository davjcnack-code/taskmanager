package com.example.taskmanager.task;

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
@RestController tells Spring:
"This class handles web/API requests and returns data, usually as JSON."

API means Application Programming Interface.
An API is a set of rules that allows different software applications
to communicate with each other.

For example:
A frontend website can send a request to this backend API,
and this backend can send back task data as JSON.

@RequestMapping("/tasks") means every endpoint in this class starts with:

    /tasks

An endpoint is a specific URL where an API receives a request
and sends back a response.
*/
@RestController
@RequestMapping("/tasks")
public class TaskController {

    /*
    This gives the controller access to the database.

    TaskRepository contains built-in methods from JpaRepository, such as:

        findAll()
        findById()
        save()
        existsById()
        deleteById()

    We use this object to create, read, update, and delete tasks.
    */
    private final TaskRepository taskRepository;

    /*
    This is constructor injection.

    Spring automatically creates a TaskRepository object and gives it
    to this controller.

    This is called dependency injection.

    Dependency injection means an object receives what it needs from
    an outside source instead of creating it by itself.
    */
    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /*
    @GetMapping creates a GET /tasks endpoint.

    GET is used to retrieve data from the server.
    It should not change or delete anything.

    This method gets all tasks from the database and returns them as JSON.

    JSON is a text-based format used to send and receive data.

    Example JSON:

    {
        "id": 1,
        "title": "Study Java",
        "description": "Practice Spring Boot",
        "completed": false
    }
    */
    @GetMapping
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    /*
    @GetMapping("/{id}") handles URLs like:

        GET /tasks/1
        GET /tasks/2
        GET /tasks/10

    @PathVariable Long id takes the number from the URL
    and stores it in the variable named id.

    Example:

        /tasks/1

    means:

        id = 1

    taskRepository.findById(id) searches the database for a task
    with that specific ID.

    ResponseEntity lets us control the HTTP response.

    If the task exists:
        return 200 OK with the task

    If the task does not exist:
        return 404 Not Found
    */
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        return taskRepository.findById(id)
                .map(task -> ResponseEntity.ok(task))
                .orElse(ResponseEntity.notFound().build());
    }

    /*
    @PostMapping creates a POST /tasks endpoint.

    POST is used to send new data to the server.
    In this case, we use POST to create a new task.

    @RequestBody tells Spring to read the JSON from the request body.

    Spring automatically converts the JSON into a Task object.

    Example request body:

    {
        "title": "Study Java",
        "description": "Finish Spring Boot lesson",
        "completed": false
    }

    taskRepository.save(task) saves the new task in the database.

    The saved task is returned as JSON, including the generated id.
    */
    @PostMapping
    public Task createTask(@RequestBody Task task) {
        return taskRepository.save(task);
    }

    /*
    @PutMapping("/{id}") creates a PUT /tasks/{id} endpoint.

    PUT is used to update an existing resource.

    Example:

        PUT /tasks/1

    @PathVariable Long id gets the id from the URL.

    @RequestBody Task updatedTask reads the JSON body and turns it
    into a Task object.

    First, we search for the existing task using findById(id).

    If the task exists:
        update the title, description, and completed fields
        save the updated task
        return 200 OK with the updated task

    If the task does not exist:
        return 404 Not Found
    */
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task updatedTask) {
        return taskRepository.findById(id)
                .map(existingTask -> {
                    existingTask.setTitle(updatedTask.getTitle());
                    existingTask.setDescription(updatedTask.getDescription());
                    existingTask.setCompleted(updatedTask.isCompleted());

                    Task savedTask = taskRepository.save(existingTask);

                    return ResponseEntity.ok(savedTask);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /*
    @DeleteMapping("/{id}") creates a DELETE /tasks/{id} endpoint.

    DELETE is used to remove data from the server.

    Example:

        DELETE /tasks/1

    @PathVariable Long id gets the id from the URL.

    taskRepository.existsById(id) checks if a task with that id exists.

    The ! symbol means "not".

    So this condition:

        if (!taskRepository.existsById(id))

    means:

        If the task does NOT exist, return 404 Not Found.

    If the task does exist:
        delete it using deleteById(id)
        return 204 No Content

    204 No Content means the deletion worked, but there is no data
    to send back in the response body.
    */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        if (!taskRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        taskRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
