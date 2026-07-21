package com.example.taskmanager.task;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
/*
    TaskService contains the business logic for task.

    The controller should focus on HTTP requests and responses.
    The repository should focus on database operations.
    The service sits in the middle and decides what should happen.

    Project flow:

    Controller -> Service -> Repository -> Database
     */
@Service
public class TaskService {

    private final TaskRepository taskRepository;
    /*
    Spring gives TaskService a TaskRepository object automatically.
    This is dependency injection.

    Dependency injection means an object receives what it needs from
    an outside source instead of creating it by itself.
     */
    public TaskService(TaskRepository taskRepository){
        this.taskRepository = taskRepository;
    }
    /*
    Gets all task from the database
     */
    public List<Task> getAllTasks(){
        return taskRepository.findAll();
    }

    /*
    Finds on task by id.
    Optional<Task> means:
    - There might be as Task
    - Or there might not be a Task

    This helps us handle missing task safely.
     */
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    /*Saves a task to the database.

     */
    public Task createTask(Task task){
        return taskRepository.save(task);
    }

    /*
    Updates an existing task.

    First, we search for the task by id.
    If it exists, we update the fields and save it.
    If it does not exist, we return Optional.empty().
     */
    public Optional<Task> updateTask(Long id, Task updatedTask){
        return taskRepository.findById(id).map(existingTask -> {
            existingTask.setTitle(updatedTask.getDescription());
            existingTask.setDescription(updatedTask.getDescription());
            existingTask.setCompleted(updatedTask.isCompleted());

            return taskRepository.save(existingTask);

        });

    }
    /*
    Deletes a task by id.

    Returns true if task was deleted.
    Returns false if the task does not exist.
     */
    public boolean deleteTask(Long id){
        if(!taskRepository.existsById(id)){
            return false;
        }

        taskRepository.deleteById(id);
        return true;

    }



}
