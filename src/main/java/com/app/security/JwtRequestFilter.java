package com.app.security;

import com.app.entities.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT-based authentication filter that runs once per request.
 * <p>
 * This filter intercepts incoming requests, checks for a valid JWT in the {@code Authorization}
 * header (with {@code Bearer} prefix), and if valid, sets up Spring Security's authentication
 * context to allow access to secured endpoints.
 * <p>
 * It skips filtering for {@code OPTIONS} requests (used in CORS preflight) and ensures that
 * authentication is only set if not already present.
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    public JwtRequestFilter(UserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Skip JWT processing for CORS preflight (OPTIONS) requests
        if (isPreflightRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwtToken = extractJwtFromHeader(request);
        String username = null;

        // Extract username from JWT if token is present and valid
        if (jwtToken != null) {
            try {
                username = jwtUtil.extractUsername(jwtToken);
            } catch (IllegalArgumentException ex) {
                logger.warn("Unable to extract username from JWT token", ex);
            } catch (Exception ex) {
                logger.warn("JWT token is expired or invalid", ex);
            }
        }

        // Proceed only if user is not already authenticated
        if (username != null && !isAlreadyAuthenticated()) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Validate token against the loaded user (including expiration and signature)
            if (jwtUtil.validateToken(jwtToken, (User) userDetails)) {
                authenticateUser(userDetails, request);
            } else {
                logger.debug("JWT validation failed for user: {}");
            }
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }

    /**
     * Checks if the request is a CORS preflight request.
     *
     * @param request the HTTP request
     * @return true if the request is an OPTIONS request with a non-null Origin header
     */
    private boolean isPreflightRequest(HttpServletRequest request) {
        return "OPTIONS".equalsIgnoreCase(request.getMethod()) &&
                request.getHeader("Origin") != null &&
                request.getHeader(AUTHORIZATION_HEADER) == null;
    }

    /**
     * Extracts the JWT token from the Authorization header.
     *
     * @param request the HTTP request
     * @return the extracted JWT token, or null if not present or invalid format
     */
    private String extractJwtFromHeader(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        logger.debug("No valid Authorization header found");
        return null;
    }

    /**
     * Checks if the current security context already has an authenticated user.
     *
     * @return true if user is already authenticated
     */
    private boolean isAlreadyAuthenticated() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }

    /**
     * Sets up Spring Security context with the authenticated user details.
     *
     * @param userDetails the authenticated user details
     * @param request     the current HTTP request
     */
    private void authenticateUser(UserDetails userDetails, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authToken);
        logger.debug("Authenticated user: {}");
    }
}