package com.app.controllers;

import com.app.DTO.RegisterDTO;
import com.app.entities.User;
import com.app.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173") // Tu puerto del frontend Vue
public class RegistrationController {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegistrationController(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> processRegistration(@RequestBody RegisterDTO registerDTO) {
        // Validación y lógica usando request
        User newUser = User.builder()
                .username(registerDTO.getUsername())
                .email(registerDTO.getEmail())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .avatar(registerDTO.getAvatar())
                .role(registerDTO.getRole() != null ? registerDTO.getRole() : User.Role.CUSTOMER)
                .build();

            User savedUser = userRepo.save(newUser);

            // Respuesta exitosa
            Map<String, Object> response = new HashMap<>();
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", savedUser.getId());
            userInfo.put("username", savedUser.getUsername());
            userInfo.put("email", savedUser.getEmail());
            userInfo.put("role", savedUser.getRole().toString());

            response.put("user", userInfo);
            response.put("message", "User registered successfully");

            return ResponseEntity.ok(response);
    }
}
