package com.infy.facebook_group3.utility;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Component;

import java.security.Key;

import java.util.Date;

@Component

public class JwtUtil {

    private static final String SECRET = "mysecretkeymysecretkeymysecretkey12345"; // use env variable in real apps

    private static final long EXPIARTION_TIME = 1000L * 60 * 60; // 1 hour

    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    public String generateToken(String username) {

        return Jwts.builder()

                .setSubject(username)

                .setIssuedAt(new Date(System.currentTimeMillis()))

                .setExpiration(new Date(System.currentTimeMillis() + EXPIARTION_TIME))

                .signWith(key, SignatureAlgorithm.HS256)

                .compact();

    }

    public String extractUsername(String token) {

        return Jwts.parserBuilder().setSigningKey(key).build()

                .parseClaimsJws(token)

                .getBody()

                .getSubject();

    }

    public boolean validateToken(String token, String username) {

        return username.equals(extractUsername(token)) && !isTokenExpired(token);

    }

    private boolean isTokenExpired(String token) {

        return Jwts.parserBuilder().setSigningKey(key).build()

                .parseClaimsJws(token)

                .getBody()

                .getExpiration()

                .before(new Date());

    }

}