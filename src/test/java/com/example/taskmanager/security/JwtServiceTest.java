package com.example.taskmanager.security;

import com.example.taskmanager.user.AppUser;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

 class JwtServiceTest {

     private JwtService createJwtService(){
         String rawSecret = "1234567890123456789012345678901212345678901234567890123456789012";
         String base64Secret = Base64.getEncoder()
                 .encodeToString(rawSecret.getBytes(StandardCharsets.UTF_8));

         long expirationMs = 3600000;

         return new JwtService(base64Secret, expirationMs);

     }

     @Test
     void generateToken_shouldCreateValidTokenWithUserEmail(){
         //Arrange
         JwtService jwtService = createJwtService();

         AppUser user = new AppUser();
         user.setId(1L);
         user.setName("David");
         user.setEmail("david@example.com");

         //Act
         String token = jwtService.generateToken(user);

         //Assert
         assertTrue(jwtService.isTokenValid(token));
         assertEquals("david@example.com", jwtService.extractEmail(token));
     }

     @Test
     void isTokenValid_shouldReturnFalseForInvalidToken(){
         //Arrange
         JwtService jwtService = createJwtService();

         //Act
         boolean result = jwtService.isTokenValid("not-a-real-token");

         //Assert
         assertFalse(result);
     }






}
