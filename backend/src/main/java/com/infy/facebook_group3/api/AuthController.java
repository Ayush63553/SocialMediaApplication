package com.infy.facebook_group3.api;

import org.springframework.http.ResponseEntity;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.bind.annotation.*;

import com.infy.facebook_group3.dto.JwtResponse;
import com.infy.facebook_group3.dto.LoginRequest;
import com.infy.facebook_group3.entity.User;
import com.infy.facebook_group3.exception.FacebookException;
import com.infy.facebook_group3.repository.UserRepository;
import com.infy.facebook_group3.utility.JwtUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {

        this.userRepository = userRepository;

        this.passwordEncoder = passwordEncoder;

        this.jwtUtil = jwtUtil;

    }

    @PostMapping("/login")

    public ResponseEntity<JwtResponse> login(@RequestBody @Valid LoginRequest loginRequest) throws FacebookException{

        User user = userRepository.findByUsername(loginRequest.getUsername())

                .orElseThrow(() -> new FacebookException("User not found"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
        	throw new FacebookException("Invalid Credentials");
        }

        String token = jwtUtil.generateToken(user.getUsername());
        Long userId = user.getUserId();
        return ResponseEntity.ok(new JwtResponse(token, userId));

    }

    
}