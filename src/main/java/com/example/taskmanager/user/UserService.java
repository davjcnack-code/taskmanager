package com.example.taskmanager.user;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(AppUserRepository appUserRepository, BCryptPasswordEncoder passwordEncoder){
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse register(RegisterRequest request) {
        String normalizedEmail = request.getEmail().toLowerCase();

        if (appUserRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("Email is already in use.");
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        AppUser user = new AppUser(
                request.getName(),
                normalizedEmail,
                hashedPassword
        );

        AppUser savedUser = appUserRepository.save(user);

        return toResponse(savedUser);
    }

    public LoginResponse login(LoginRequest request){
        String normalizedEmail = request.getEmail().toLowerCase();

        AppUser user = appUserRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));

        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash()
        );

        if(!passwordMatches){
            throw new IllegalArgumentException("Invalid email or password");
        }

        return new LoginResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                "Login successful"
        );

    }

        private UserResponse toResponse(AppUser user){
            return new UserResponse(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getCreatedAt()
            );
        }
    }
