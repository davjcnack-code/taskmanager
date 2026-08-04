package com.example.taskmanager.user;

import com.example.taskmanager.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    @Test
    void register_shouldCreateUserWithHashedPassword() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setName("David");
        request.setEmail("David@Example.com");
        request.setPassword("password123");

        when(appUserRepository.existsByEmail("david@example.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("password123"))
                .thenReturn("hashed-password");

        when(appUserRepository.save(any(AppUser.class)))
                .thenAnswer(invocation -> {
                    AppUser savedUser = invocation.getArgument(0);
                    savedUser.setId(1L);
                    return savedUser;
                });

        // Act
        UserResponse response = userService.register(request);

        // Assert response
        assertEquals(1L, response.getId());
        assertEquals("David", response.getName());
        assertEquals("david@example.com", response.getEmail());

        // Assert saved user
        ArgumentCaptor<AppUser> userCaptor = ArgumentCaptor.forClass(AppUser.class);

        verify(appUserRepository).save(userCaptor.capture());

        AppUser savedUser = userCaptor.getValue();

        assertEquals("David", savedUser.getName());
        assertEquals("david@example.com", savedUser.getEmail());
        assertEquals("hashed-password", savedUser.getPasswordHash());
    }

    @Test
    void register_shouldThrowExceptionWhenEmailAlreadyExists() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setName("David");
        request.setEmail("david@example.com");
        request.setPassword("password123");

        when(appUserRepository.existsByEmail("david@example.com"))
                .thenReturn(true);

        // Act + Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.register(request)
        );

        assertEquals("Email is already in use.", exception.getMessage());
    }

    @Test
    void login_shouldReturnJwtTokenWhenCredentialsAreValid() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("David@Example.com");
        request.setPassword("password123");

        AppUser user = new AppUser();
        user.setId(1L);
        user.setName("David");
        user.setEmail("david@example.com");
        user.setPasswordHash("hashed-password");

        when(appUserRepository.findByEmail("david@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("password123", "hashed-password"))
                .thenReturn(true);

        when(jwtService.generateToken(user))
                .thenReturn("fake-jwt-token");

        // Act
        LoginResponse response = userService.login(request);

        // Assert
        assertEquals(1L, response.getId());
        assertEquals("David", response.getName());
        assertEquals("david@example.com", response.getEmail());
        assertEquals("fake-jwt-token", response.getToken());
    }

    @Test
    void login_shouldThrowExceptionWhenPasswordIsWrong() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("david@example.com");
        request.setPassword("wrongpassword");

        AppUser user = new AppUser();
        user.setEmail("david@example.com");
        user.setPasswordHash("hashed-password");

        when(appUserRepository.findByEmail("david@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("wrongpassword", "hashed-password"))
                .thenReturn(false);

        // Act + Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.login(request)
        );

        assertEquals("Invalid email or password", exception.getMessage());
    }
}
