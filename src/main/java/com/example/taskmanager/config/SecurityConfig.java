package com.example.taskmanager.config;

import com.example.taskmanager.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/*
SecurityConfig controls security rules for the API.

We are using JWT authentication.

Public endpoints:
    POST /users/register
    POST /users/login

Protected endpoints:
    /tasks/**

That means users must log in and send a JWT token before accessing tasks.
*/
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /*
    BCryptPasswordEncoder hashes passwords and checks passwords during login.
    */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    /*
    SecurityFilterChain defines the security rules for HTTP requests.
    */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors ->{})
                /*
                CSRF is mainly for browser form sessions.
                For this REST API using JWT tokens, we disable it.
                */
                .csrf(csrf -> csrf.disable())

                /*
                We do not want server-side login sessions.
                JWT is stateless, meaning each request must bring its own token.
                */
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                /*
                Disable default browser login forms and HTTP Basic login.
                We use our own /users/login endpoint instead.
                */
                .formLogin(form -> form.disable())
                .httpBasic(httpBasic -> httpBasic.disable())

                /*
                Authorization rules.
                */
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/users/register", "/users/login").permitAll()
                        .requestMatchers("/tasks/**").authenticated()
                        .anyRequest().permitAll()
                )

                /*
                Run our JWT filter before Spring's username/password filter.
                */
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }
}