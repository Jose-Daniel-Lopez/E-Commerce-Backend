package com.app.security;

import com.app.entities.User;
import com.app.repositories.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Optional;

@Configuration
public class WebSecurityConfig {

    private final UserRepository userRepo;

    public WebSecurityConfig(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    private final String[] publicUrl = {
            "/", // Home
            "/login", "/logout", // Auth endpoints
            "/register", "/register/**", // User registration
            "/api/products", "/api/products/**", // Product catalog and details
            "/api/categories", "/api/categories/**", // Categories
            "/api/product-reviews", "/api/product-reviews/**", // Product reviews
            "/api/discount-codes", "/api/discount-codes/**", // Discount codes
            "/global-search/**", // Global search
            "/webjars/**", "/resources/**", "/assets/**", "/css/**", "/js/**", "/fonts/**", // Static resources
            "/*.css", "/*.js", "/*.js.map", "/favicon.ico", "/error"
    };

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(publicUrl).permitAll();
                    auth.anyRequest().authenticated();
                })
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())
                .logout(logout -> {
                    logout.logoutUrl("/logout");
                    logout.logoutSuccessUrl("/");
                })
                .cors(Customizer.withDefaults());

        return http.build();
    }


    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                Optional<User> user = userRepo.findByEmail(username);

                if (user.isPresent()) {
                    return (UserDetails) user.get();
                }
                throw new UsernameNotFoundException("User '" + username + "' not found");
            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
