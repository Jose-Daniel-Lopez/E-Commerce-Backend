package com.app.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Security configuration for the application.
 * <p>
 * This class configures:
 * <ul>
 *   <li>JWT-based authentication using {@link JwtRequestFilter}</li>
 *   <li>Stateless session management</li>
 *   <li>CORS policy for frontend (localhost:5173)</li>
 *   <li>CSRF disabled (JWT is stateless)</li>
 *   <li>Custom entry point for authentication errors ({@link JwtAuthenticationEntryPoint})</li>
 *   <li>Password encoding with BCrypt</li>
 *   <li>Public vs. secured endpoints</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    public WebSecurityConfig(JwtRequestFilter jwtRequestFilter,
                             JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.jwtRequestFilter = jwtRequestFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }

    // ========================================================================
    // PUBLIC ENDPOINTS
    // These paths are accessible without authentication.
    // Note: Some endpoints (e.g., /api/orders/**) are open for testing.
    // ========================================================================

    private static final String[] PUBLIC_ENDPOINTS = {
            // Root and base API
            "/", "/api", "/api/**",

            // Auth and registration
            "/login", "/logout", "/register", "/register/**",
            "/api/auth/**",

            // Public API endpoints
            "/api/users", "/api/users/**",
            "/api/orders", "/api/orders/**",  // TODO: Secure this later
            "/api/products", "/api/products/**",
            "/api/categories", "/api/categories/**",
            "/api/product-reviews", "/api/product-reviews/**",
            "/api/discount-codes", "/api/discount-codes/**",
            "/global-search/**",

            // Static resources
            "/webjars/**", "/resources/**", "/assets/**",
            "/css/**", "/js/**", "/fonts/**",
            "/*.css", "/*.js", "/*.js.map", "/favicon.ico", "/error"
    };

    // ========================================================================
    // SECURITY FILTER CHAIN
    // Main security configuration: CORS, CSRF, session, authorization, logout
    // ========================================================================

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Enable CORS using the corsConfigurationSource bean
                .cors(withDefaults())

                // Disable CSRF — safe because we use stateless JWT (no session)
                .csrf(AbstractHttpConfigurer::disable)

                // Use stateless session (no session created on server)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Handle authentication failures with custom JSON response
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint))

                // Define authorization rules
                .authorizeHttpRequests(authorize -> authorize

                        // Allow OPTIONS requests (CORS preflight) globally
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Explicitly allow auth and registration endpoints
                        .requestMatchers("/api/auth/**", "/api/register/**").permitAll()

                        // Temporary: Allow public access to orders (for testing)
                        .requestMatchers("/api/orders/**").permitAll()

                        // Authenticated users only
                        .requestMatchers("/api/users/me").authenticated()
                        .requestMatchers("/api/cart/**", "/api/orders/**").authenticated() // change to authenticated later

                        // All other requests are allowed (fallback)
                        .anyRequest().permitAll()
                )

                // Configure logout behavior
                .logout(logout -> logout
                        .logoutUrl("/api/logout")                    // Endpoint to trigger logout
                        .logoutSuccessUrl("/api/auth/login")         // Redirect after logout
                        .invalidateHttpSession(true)                 // Invalidate session if any
                        .deleteCookies("JSESSIONID")                 // Clear session cookie
                )

                // Add JWT filter before Spring's built-in username/password filter
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ========================================================================
    // CORS CONFIGURATION
    // Allows frontend (e.g., React on localhost:5173) to make requests
    // ========================================================================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "https://tab-to-dev.click"));  // Frontend origins
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));                       // Allow all headers
        configuration.setAllowCredentials(true);                             // Allow cookies/Authorization
        configuration.setExposedHeaders(List.of("Authorization", "Content-Type")); // Frontend can read these
        configuration.setMaxAge(3600L);                                      // Cache preflight for 1 hour

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);  // Apply to all paths
        return source;
    }

    // ========================================================================
    // AUTHENTICATION & PASSWORD ENCODING
    // Expose beans for use in controllers or services
    // ========================================================================

    /**
     * Provides the AuthenticationManager bean.
     * Used for authenticating users during login.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Password encoder for securely hashing passwords.
     * Uses BCrypt hashing algorithm.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}