package com.app.controllers;

import com.app.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173") // Backup CORS
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // Obtener el usuario autenticado
            User user = (User) auth.getPrincipal();

            // Crear respuesta con información del usuario
            LoginResponse response = new LoginResponse();
            response.setMessage("Login successful");
            response.setUser(new UserInfo(user.getId(), user.getUsername(), user.getEmail(), user.getRole().name()));

            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid credentials"));
        }
    }

    @Getter
    @Setter
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Getter
    @Setter
    public static class LoginResponse {
        private String message;
        private UserInfo user;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String username;
        private String email;
        private String role;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ErrorResponse {
        private String message;
    }
}
