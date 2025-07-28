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
 * Main security configuration class for the application.
 * <p>
 * Configures:
 * <ul>
 *   <li>Stateless JWT-based authentication</li>
 *   <li>CORS policy (frontend: http://localhost:5173)</li>
 *   <li>CSRF disabled (since we use JWT, not session cookies)</li>
 *   <li>Custom authentication entry point for 401 responses</li>
 *   <li>JWT filter inserted before UsernamePasswordAuthenticationFilter</li>
 * </ul>
 * <p>
 * All endpoints are secured by default. Public access is explicitly defined.
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

    /**
     * Defines the security filter chain.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Enable CORS with default configuration (uses corsConfigurationSource bean)
                .cors(withDefaults())

                // Disable CSRF — safe to do when using stateless JWT authentication
                .csrf(AbstractHttpConfigurer::disable)

                // Use stateless session (no session created or used)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Handle authentication exceptions via custom entry point
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint))

                // Define authorization rules
                .authorizeHttpRequests(authorize -> authorize

                        // Allow preflight requests (OPTIONS) globally
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Public authentication & registration endpoints
                        .requestMatchers("/api/auth/login", "/api/auth/register", "/api/auth/refresh").permitAll()

                        // Public product & category APIs (read-only)
                        .requestMatchers(HttpMethod.GET, "/api/products", "/api/products/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categories", "/api/categories/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/product-reviews", "/api/product-reviews/**").permitAll()
                        .requestMatchers("/global-search/**").permitAll()

                        // Allow public access to static resources
                        .requestMatchers("/webjars/**", "/resources/**", "/assets/**",
                                "/css/**", "/js/**", "/fonts/**",
                                "/*.css", "/*.js", "/*.js.map", "/favicon.ico", "/error").permitAll()

                        // User profile: authenticated users only
                        .requestMatchers("/api/users/me").authenticated()

                        // Cart and order management: require authentication
                        .requestMatchers("/api/cart/**", "/api/orders", "/api/orders/**").authenticated()

                        // All other requests must be authenticated (default deny)
                        .anyRequest().authenticated()
                )

                // Logout configuration (though JWT is stateless, this can be used to clean up client-side)
                .logout(logout -> logout
                        .logoutUrl("/api/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            // No server-side session to invalidate, but you can add custom logic
                        })
                        .clearAuthentication(true)
                        .invalidateHttpSession(false) // No session used
                        .deleteCookies("JSESSIONID")  // Optional: if any cookies exist
                )

                // Insert JWT filter before Spring's UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS configuration to allow frontend (e.g., React/Vue on localhost:5173).
     * <p>
     * Only allows trusted origin. Credentials (e.g., cookies, Authorization header)
     * are supported if needed.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173")); // Adjust for prod
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); // Allows sending Authorization header
        configuration.setExposedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setMaxAge(3600L); // Cache preflight for 1 hour

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Exposes the AuthenticationManager as a bean so it can be used in controllers
     * or JWT authentication services.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Password encoder for securely hashing passwords using BCrypt.
     * <p>
     * Always use a password encoder — never store plain text!
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}