package com.example.taskmanager.security;

import com.example.taskmanager.user.AppUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
/*
JwtService creates and validates JWT tokens.

A Jwt token proves that user logged in successfully.

This class does two main things:

1. generateToken()
    Creates a JWT token after login

2. validate/extract methods
    Read a JWT token later when the user sends it back.
 */

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs
    ){

        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expirationMs = expirationMs;
    }
    /*
    Generates a JWT token for a logged_in user.

    subject = main identity of the toke. We use the user's email.
    claims = extra informationwe include, like userId and name.
    issuedAt = when the token was created.
    expiration = when the token expires.
    signWtih = sign the token so it cannot be changed secretly.
     */
    public String generateToken(AppUser user) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .claim("name", user.getName())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();

    }

    /*
    Extracts the user's email from the token.

    The email was stored as the token subject.
     */
    public String extractEmail(String token){
        return extractClaims(token).getSubject();
    }

    /*
    Check if a token is valid

    If the token is expired, changed, fake, or signed with the wrong secret,
    JHWT will throw an exception.

    We catch the exception and return false.
     */
    public boolean isTokenValid(String token){
        try{
            extractClaims(token);
            return true;
        }catch(JwtException | IllegalArgumentException exception){
            return false;
        }
    }

    /*
    Parses the token and returns its claims

    Claims are the data inside the JWT, such as:

    -subject/email
    -userId
    -name
    -issuedAt
    -expiration
     */
    private Claims extractClaims(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


}
