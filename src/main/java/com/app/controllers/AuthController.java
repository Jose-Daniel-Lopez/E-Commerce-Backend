package com.app.controllers;

import com.app.entities.User;
import com.app.security.JwtUtil;
import lombok.AllArgsConstructor;
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

/**
 * REST controller for handling user authentication.
 * <p>
 * Provides endpoints for user login, including JWT token generation
 * and verification checks. Designed to be used with a stateless
 * authentication flow via JSON Web Tokens (JWT).
 * </p>
 * <p>
 * Endpoint: {@code /api/auth}
 * </p>
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:5173", "https://tab-to-dev.click/tejon-tech/"}) // Allow frontend dev server
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Authenticates a user with email and password.
     * <p>
     * If authentication succeeds and the user is verified, returns a JWT token
     * and basic user info. If credentials are invalid or account is not verified,
     * returns an appropriate error.
     * </p>
     *
     * @param request the login credentials (email and password)
     * @return {@link ResponseEntity} containing either a {@link LoginResponse} or {@link ErrorResponse}
     * @response 200 Successful login with token and user info
     * @response 401 Invalid credentials
     * @response 403 Account not verified
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // Authenticate using Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // Extract authenticated user (assumes User implements UserDetails)
            User user = (User) authentication.getPrincipal();

            // Enforce email verification before allowing login
            if (!user.isVerified()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse("Account not verified. Please check your email for the verification link."));
            }

            // Generate JWT token for the authenticated session
            String token = jwtUtil.generateToken(user);

            // Construct successful response with user details and token
            LoginResponse response = new LoginResponse();
            response.setMessage("Login successful");
            response.setToken(token);
            response.setUser(new UserInfo(user.getId(), user.getUsername(), user.getEmail(), user.getRole().name()));

            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            // Failed authentication (bad credentials)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid credentials"));
        }
    }

    // === Data Transfer Objects (DTOs) ===

    /**
     * Request body for login endpoint.
     */
    @Getter
    @Setter
    public static class LoginRequest {
        private String email;
        private String password;
    }

    /**
     * Response body for successful login.
     */
    @Getter
    @Setter
    public static class LoginResponse {
        private String message;
        private String token;
        private UserInfo user;
    }

    /**
     * Minimal user info included in the login response.
     */
    @Getter
    @Setter
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String username;
        private String email;
        private String role; // Role name as string (e.g., "CUSTOMER", "ADMIN")
    }

    /**
     * Generic error response structure for authentication failures.
     */
    @Getter
    @Setter
    @AllArgsConstructor
    public static class ErrorResponse {
        private String message;
    }
}