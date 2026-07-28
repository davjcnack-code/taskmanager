package com.example.taskmanager.task;

import com.example.taskmanager.user.AppUser;
import com.example.taskmanager.user.AppUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
/*
    TaskService contains the business logic for task.

    Now tasks belong to users.

    That means users should only see, update, and delete their own task.
     */
@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final AppUserRepository appUserRepository;

    /*
    Spring gives TaskService a TaskRepository object automatically.
    This is dependency injection.

    Dependency injection means an object receives what it needs from
    an outside source instead of creating it by itself.
     */
    public TaskService(TaskRepository taskRepository, AppUserRepository appUserRepository) {
        this.taskRepository = taskRepository;
        this.appUserRepository = appUserRepository;
    }

    /*
    Gets all task from the database
     */
    public List<TaskResponse> getAllTasks(String userEmail) {
        return taskRepository.findByUserEmail(userEmail)
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
    public Optional<TaskResponse> getTaskById(Long id, String userEmail) {
        return taskRepository.findByIdAndUserEmail(id, userEmail)
                .map(this::toResponse);
    }

    /*
   Creates a new task from a TaskRequest DTO.

   The client sends TaskRequest data.
   We create a Task entity from that data.
   Then we save the Task entity to the database.
   */
    public TaskResponse createTask(TaskRequest taskRequest, String userEmail) {
        AppUser user = appUserRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Task task = new Task(
                taskRequest.getTitle(),
                taskRequest.getDescription(),
                taskRequest.isCompleted()
        );
        task.setUser(user);

        Task savedTask = taskRepository.save(task);

        return toResponse(savedTask);
    }

    /*
    Updates an existing task.

    First, we search for the task by id.
    If it exists, we update the fields and save it.
    If it does not exist, we return Optional.empty().
     */
    public Optional<TaskResponse> updateTask(Long id, TaskRequest taskRequest, String userEmail) {
        return taskRepository.findByIdAndUserEmail(id, userEmail)
                .map(existingTask -> {
                    existingTask.setTitle(taskRequest.getTitle());
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
    public boolean deleteTask(Long id, String userEmail) {
        if (!taskRepository.existsByIdAndUserEmail(id, userEmail)) {
            return false;
        }
        Task task = taskRepository.findByIdAndUserEmail(id, userEmail)
                        .orElseThrow();


        taskRepository.delete(task);
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
