package com.example.taskmanager.task;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private TaskController taskController;

    @Test
    void getAllTasks_shouldReturnLoggedInUsersTasks() {
        // Arrange
        String userEmail = "david@example.com";

        TaskResponse taskResponse = new TaskResponse(
                1L,
                "Study Spring Boot",
                "Review controller tests",
                false,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(authentication.getName()).thenReturn(userEmail);
        when(taskService.getAllTasks(userEmail)).thenReturn(List.of(taskResponse));

        // Act
        List<TaskResponse> tasks = taskController.getAllTasks(authentication);

        // Assert
        assertEquals(1, tasks.size());
        assertEquals("Study Spring Boot", tasks.get(0).getTitle());
        assertEquals("Review controller tests", tasks.get(0).getDescription());
        assertEquals(false, tasks.get(0).isCompleted());

        verify(taskService).getAllTasks(userEmail);
    }

    @Test
    void createTask_shouldCreateTaskForLoggedInUser() {
        // Arrange
        String userEmail = "david@example.com";

        TaskRequest request = new TaskRequest();
        request.setTitle("New task");
        request.setDescription("Created from controller test");
        request.setCompleted(false);

        TaskResponse expectedResponse = new TaskResponse(
                1L,
                "New task",
                "Created from controller test",
                false,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(authentication.getName()).thenReturn(userEmail);
        when(taskService.createTask(request, userEmail)).thenReturn(expectedResponse);

        // Act
        TaskResponse actualResponse = taskController.createTask(request, authentication);

        // Assert
        assertEquals(1L, actualResponse.getId());
        assertEquals("New task", actualResponse.getTitle());
        assertEquals("Created from controller test", actualResponse.getDescription());
        assertEquals(false, actualResponse.isCompleted());

        verify(taskService).createTask(request, userEmail);
    }

    @Test
    void updateTask_shouldReturnUpdatedTaskWhenFound() {
        // Arrange
        Long taskId = 1L;
        String userEmail = "david@example.com";

        TaskRequest request = new TaskRequest();
        request.setTitle("Updated title");
        request.setDescription("Updated description");
        request.setCompleted(true);

        TaskResponse expectedResponse = new TaskResponse(
                taskId,
                "Updated title",
                "Updated description",
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(authentication.getName()).thenReturn(userEmail);
        when(taskService.updateTask(taskId, request, userEmail))
                .thenReturn(Optional.of(expectedResponse));

        // Act
        ResponseEntity<TaskResponse> response =
                taskController.updateTask(taskId, request, authentication);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Updated title", response.getBody().getTitle());
        assertEquals("Updated description", response.getBody().getDescription());
        assertEquals(true, response.getBody().isCompleted());

        verify(taskService).updateTask(taskId, request, userEmail);
    }

    @Test
    void updateTask_shouldReturnNotFoundWhenTaskDoesNotExistForUser() {
        // Arrange
        Long taskId = 1L;
        String userEmail = "david@example.com";

        TaskRequest request = new TaskRequest();
        request.setTitle("Updated title");
        request.setDescription("Updated description");
        request.setCompleted(true);

        when(authentication.getName()).thenReturn(userEmail);
        when(taskService.updateTask(taskId, request, userEmail))
                .thenReturn(Optional.empty());

        // Act
        ResponseEntity<TaskResponse> response =
                taskController.updateTask(taskId, request, authentication);

        // Assert
        assertEquals(404, response.getStatusCode().value());

        verify(taskService).updateTask(taskId, request, userEmail);
    }

    @Test
    void deleteTask_shouldReturnNoContentWhenDeleted() {
        // Arrange
        Long taskId = 1L;
        String userEmail = "david@example.com";

        when(authentication.getName()).thenReturn(userEmail);
        when(taskService.deleteTask(taskId, userEmail)).thenReturn(true);

        // Act
        ResponseEntity<Void> response = taskController.deleteTask(taskId, authentication);

        // Assert
        assertEquals(204, response.getStatusCode().value());

        verify(taskService).deleteTask(taskId, userEmail);
    }

    @Test
    void deleteTask_shouldReturnNotFoundWhenTaskDoesNotBelongToUser() {
        // Arrange
        Long taskId = 1L;
        String userEmail = "david@example.com";

        when(authentication.getName()).thenReturn(userEmail);
        when(taskService.deleteTask(taskId, userEmail)).thenReturn(false);

        // Act
        ResponseEntity<Void> response = taskController.deleteTask(taskId, authentication);

        // Assert
        assertEquals(404, response.getStatusCode().value());

        verify(taskService).deleteTask(taskId, userEmail);
    }
}