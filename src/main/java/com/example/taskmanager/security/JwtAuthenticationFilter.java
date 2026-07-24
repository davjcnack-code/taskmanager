package com.example.taskmanager.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
/*
JwtAuthenticationFilter runs before the request reaches the controller.

Its job is to check if the request has a JWT token.

The client sends the token in the Authorization header:

    Authorization: Bearer eyJGFciOiJIUx...

If the token is valid, we tell Spring Security:
"This request is authenticated."
 */
@Component
public class JwtAuthenticationFilter  extends OncePerRequestFilter{

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService){
        this.jwtService = jwtService;
    }

    /*
    This method runs once per HTTp request.
    */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    )throws ServletException, IOException{
        String authHeader = request.getHeader("Authorization");

        /*
        If there is no Authorization header,
        or it does not start with "Bearer",
        we let the request continue without authentication.

        If the endpoint requires authentication,
        Spring Security will reject it later.
         */

        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        /*
        Remove "Bearer" from the header to get only the token.
         */

        String token = authHeader.substring(7);

        /*
        If the token is valid, extract the email and create an authentication object.
         */
        if(jwtService.isTokenValid(token) && SecurityContextHolder.getContext().getAuthentication() == null){

            String email = jwtService.extractEmail(token);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                email,
                null,
                List.of()
                    );
            authenticationToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            }

            filterChain.doFilter(request, response);
        }
    }
