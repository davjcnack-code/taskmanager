package com.example.taskmanager.task;

import com.example.taskmanager.user.AppUser;
import com.example.taskmanager.user.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
}