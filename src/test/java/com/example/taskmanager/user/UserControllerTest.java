package com.example.taskmanager.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
 class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void register_shouldReturnUserResponse(){
        //Arrange
        RegisterRequest request = new RegisterRequest();
        request.setName("David");
        request.setEmail("david@example.com");
        request.setPassword("password123");

        UserResponse expectedResponse = new UserResponse(
                1L,
                "David",
                "david@example.com",
                LocalDateTime.now()
        );

        when(userService.register(request)).thenReturn(expectedResponse);

        //Act
        UserResponse actualResponse = userController.register(request);

        //Assert
        assertEquals(1L, actualResponse.getId());
        assertEquals("David", actualResponse.getName());
        assertEquals("david@example.com", actualResponse.getEmail());

        verify(userService).register(request);
    }

    @Test
    void login_shouldReturnLoginResponseWithToken(){
        //Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("david@example.com");
        request.setPassword("password123");

        LoginResponse expectedResponse = new LoginResponse(
                1L,
                "David",
                "david@example.com",
                "fake-jwt-token"
        );

        when(userService.login(request)).thenReturn(expectedResponse);

        //Act
        LoginResponse actualResponse = userController.login(request);

        //Assert
        assertEquals(1L, actualResponse.getId());
        assertEquals("David", actualResponse.getName());
        assertEquals("david@example.com", actualResponse.getEmail());
        assertEquals("fake-jwt-token", actualResponse.getToken());

        verify(userService).login(request);
    }

}
