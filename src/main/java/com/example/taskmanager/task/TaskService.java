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
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /*
    Gets all task from the database
     */
    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /*
    Finds on task by id.
    Optional<Task> means:
    - There might be as Task
    - Or there might not be a Task

    This helps us handle missing task safely.
     */
    public Optional<TaskResponse> getTaskById(Long id) {
        return taskRepository.findById(id)
                .map(this::toResponse);
    }

    /*
   Creates a new task from a TaskRequest DTO.

   The client sends TaskRequest data.
   We create a Task entity from that data.
   Then we save the Task entity to the database.
   */
    public TaskResponse createTask(TaskRequest taskRequest) {
        Task task = new Task(
                taskRequest.getTitle(),
                taskRequest.getDescription(),
                taskRequest.isCompleted()
        );
        Task savedTask = taskRepository.save(task);

        return toResponse(savedTask);
    }

    /*
    Updates an existing task.

    First, we search for the task by id.
    If it exists, we update the fields and save it.
    If it does not exist, we return Optional.empty().
     */
    public Optional<TaskResponse> updateTask(Long id, TaskRequest taskRequest) {
        return taskRepository.findById(id)
                .map(existingTask -> {
                    existingTask.setTitle(taskRequest.getDescription());
                    existingTask.setDescription(taskRequest.getDescription());
                    existingTask.setCompleted(taskRequest.isCompleted());

                    Task savedTask = taskRepository.save(existingTask);
                    return toResponse(savedTask);

                });

    }

    /*
    Deletes a task by id.

    Returns true if task was deleted.
    Returns false if the task does not exist.
     */
    public boolean deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            return false;
        }

        taskRepository.deleteById(id);
        return true;

    }

    /* This helper method converts a Task entity into a TaskResponse DTO

        We keep this conversion in one place so we do not repeat the same code
     */
    private TaskResponse toResponse(Task task){
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.isCompleted(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }


}
