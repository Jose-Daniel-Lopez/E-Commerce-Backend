package com.app.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * Custom implementation of Spring Security's {@link AuthenticationEntryPoint}.
 * <p>
 * This class is responsible for handling unauthorized access attempts when JWT authentication fails
 * or is missing. It returns a standardized 401 Unauthorized JSON response instead of redirecting
 * to a login page (which is typical in form-based authentication).
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // ObjectMapper is thread-safe and can be reused
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Error details constants
    private static final int STATUS_UNAUTHORIZED = HttpServletResponse.SC_UNAUTHORIZED;
    private static final String ERROR_UNAUTHORIZED = "Unauthorized";
    private static final String MESSAGE_UNAUTHORIZED = "JWT token is missing or invalid";

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        /**
         * Handles unauthorized access by sending a JSON response with:
         * - Status code 401
         * - Error type
         * - Descriptive message
         * - Request path
         */

        // Set response status and content type
        response.setStatus(STATUS_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // Create error payload
        Map<String, Object> body = Map.of(
                "status", STATUS_UNAUTHORIZED,
                "error", ERROR_UNAUTHORIZED,
                "message", MESSAGE_UNAUTHORIZED,
                "path", request.getServletPath()
        );

        // Write JSON response
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}