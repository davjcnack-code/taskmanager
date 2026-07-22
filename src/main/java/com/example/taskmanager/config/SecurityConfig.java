package com.example.taskmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/*
SecurityConfig stores security-related Spring beans.

A bean is an object that Spring creates and manages for us.

Here we create a BCrpytPasswordEncoder bean.

BCryptPasswordEncoder is used to hash passwords before saving them in the
database.

We should never save plain text passwords.
*/

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
