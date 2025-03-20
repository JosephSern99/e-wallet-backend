package com.ewallet.api.controller;

import com.ewallet.api.dto.request.RegisterRequest;
import com.ewallet.api.dto.response.LoginResponse;
import com.ewallet.api.model.User;
import com.ewallet.api.repository.UserRepository;
import com.ewallet.api.security.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

//Handles user sign-in and sign-up requests.
//Generates JWT tokens upon successful authentication.
@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtil jwtUtils;

    @PostMapping("/signin")
    public LoginResponse authenticateUser(@RequestBody User user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        user.getPassword()
                )
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtils.generateToken(userDetails.getUsername());

        // Fetch the user details from the database
        User authenticatedUser = userRepository.findByUsername(user.getUsername());

        if (authenticatedUser == null) {
            throw new UsernameNotFoundException("User Not Found with username: " + user.getUsername());
        }

//        log.debug("Authenticated User: {}", authenticatedUser);

        return new LoginResponse(token, authenticatedUser);
    }
    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequest user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }
        // Create new user's account
        String encodedPassword = encoder.encode(user.getPassword());
        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(encodedPassword);
        newUser.setEmail(user.getEmail());
        newUser.setFullName(user.getFullName());
        newUser.setPhoneNumber(user.getPhoneNumber());
        newUser.setEnabled(true); // Assuming the user is enabled by default
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setCreatedBy("user"); // Assuming the user is created by the system

        userRepository.save(newUser);

        return ResponseEntity.ok("User registered successfully");
    }
}