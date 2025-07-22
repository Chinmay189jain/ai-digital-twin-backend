package com.digitaltwin.backend.controller;

import com.digitaltwin.backend.dto.JwtResponse;
import com.digitaltwin.backend.dto.LoginRequest;
import com.digitaltwin.backend.model.User;
import com.digitaltwin.backend.repository.UserRepository;
import com.digitaltwin.backend.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    // Register endpoint
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody LoginRequest request) {

        // Check if email is already registered
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already registered");
        }

        // Create new user with encoded password
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        // Save user to the database
        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail());
        return ResponseEntity.ok(new JwtResponse(token));
    }

    // Login endpoint
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // Verify credentials
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // Generate JWT on success
            String token = jwtService.generateToken(request.getEmail());
            return ResponseEntity.ok(new JwtResponse(token));

        } catch (BadCredentialsException e) {
            // If password is wrong or email doesn’t exist
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }
}
