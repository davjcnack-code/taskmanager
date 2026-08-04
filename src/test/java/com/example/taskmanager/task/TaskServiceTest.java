package com.example.taskmanager.task;

import com.example.taskmanager.user.AppUser;
import com.example.taskmanager.user.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private AppUserRepository appUserRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void createTask_shouldSaveTaskForLoggedInUser() {
        // Arrange
        String userEmail = "david@example.com";

        AppUser user = new AppUser();
        user.setEmail(userEmail);

        TaskRequest request = new TaskRequest();
        request.setTitle("Study tests");
        request.setDescription("Write first service test");
        request.setCompleted(false);

        when(appUserRepository.findByEmail(userEmail))
                .thenReturn(Optional.of(user));

        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TaskResponse response = taskService.createTask(request, userEmail);

        // Assert response
        assertEquals("Study tests", response.getTitle());
        assertEquals("Write first service test", response.getDescription());
        assertFalse(response.isCompleted());

        // Assert saved entity
        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);

        verify(taskRepository).save(taskCaptor.capture());

        Task savedTask = taskCaptor.getValue();

        assertEquals("Study tests", savedTask.getTitle());
        assertEquals("Write first service test", savedTask.getDescription());
        assertFalse(savedTask.isCompleted());
        assertEquals(user, savedTask.getUser());
    }

    @Test
    void updateTask_shouldUpdateTaskWhenTaskBelongsToUser(){
        //Arrange
        Long taskId = 1L;
        String userEmail = "david@example.com";

        Task existingTask = new Task(
                "Old title",
                "Old description",
                false
        );

        TaskRequest request = new TaskRequest();
        request.setTitle("New title");
        request.setDescription("New description");
        request.setCompleted(true);

        when(taskRepository.findByIdAndUserEmail(taskId, userEmail))
                .thenReturn(Optional.of(existingTask));

        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        //Act
        Optional<TaskResponse> response = taskService.updateTask(taskId, request, userEmail);

        //Assert
        assertTrue(response.isPresent());
        assertEquals("New title", response.get().getTitle());
        assertEquals("New description", response.get().getDescription());
        assertTrue(response.get().isCompleted());

        verify(taskRepository).save(existingTask);
    }

    @Test
    void update_task_shouldReturnEmptyWhenTasksDoesNotBelongToUser(){
        //Arrange
        Long taskId = 1L;
        String userEmail = "david@example.com";

        TaskRequest request = new TaskRequest();
        request.setTitle("New title");
        request.setDescription("New description");
        request.setCompleted(true);

        when(taskRepository.findByIdAndUserEmail(taskId, userEmail))
                .thenReturn(Optional.empty());

        //Act(
        Optional<TaskResponse> response = taskService.updateTask(taskId, request, userEmail);

        //Assert
        assertTrue(response.isEmpty());

        verify(taskRepository, never()).save(any(Task.class));

    }

    @Test
    void deleteTask_shouldDeleteTaskWhenTaskBelongsToUser(){
        //Arrange
        Long taskId = 1L;
        String userEmail = "david@example.com";

        Task existingTask = new Task(
                "Task to delete",
                "This task belongs to David",
                false

        );

        when(taskRepository.existsByIdAndUserEmail(taskId, userEmail))
                .thenReturn(true);

        when(taskRepository.findByIdAndUserEmail(taskId, userEmail))
                .thenReturn(Optional.of(existingTask));

        //Act
        boolean deleted = taskService.deleteTask(taskId, userEmail);

        //Assert
        assertTrue(deleted);
        verify(taskRepository).delete(existingTask);

    }

    @Test
    void deleteTask_shouldReturnFalseWhenTaskDoesNotBelongToUser() {
        //Arrange
        Long taskId = 1L;
        String userEmail = "david@example.com";

        when(taskRepository.existsByIdAndUserEmail(taskId, userEmail))
                .thenReturn(false);

        //Act
        boolean deleted = taskService.deleteTask(taskId, userEmail);

        //Assert
        assertFalse(deleted);

        verify(taskRepository, never()).delete(any(Task.class));

    }


}