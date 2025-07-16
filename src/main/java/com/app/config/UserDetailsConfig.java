package com.app.config;

import com.app.repositories.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
public class UserDetailsConfig {

    private final UserRepository userRepository;

    public UserDetailsConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return login -> userRepository.findByEmail(login)
                .or(() -> userRepository.findByUsername(login))
                .map(user -> (org.springframework.security.core.userdetails.UserDetails) user)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User with email or username '" + login + "' not found"));
    }
}
