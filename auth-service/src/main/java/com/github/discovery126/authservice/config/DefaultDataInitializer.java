package com.github.discovery126.authservice.config;

import com.github.discovery126.authservice.model.Role;
import com.github.discovery126.authservice.model.User;
import com.github.discovery126.authservice.repository.RoleRepository;
import com.github.discovery126.authservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DefaultDataInitializer {

    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.booking.email}")
    private String bookingEmail;

    @Value("${app.booking.password}")
    private String bookingPassword;

    @Bean
    @Transactional
    public CommandLineRunner initDefaultUsers(UserRepository userRepository,
                                              RoleRepository roleRepository) {
        return args -> {
            createUserIfNotExists(userRepository, roleRepository,
                    adminEmail, adminPassword, "ADMIN");
            createUserIfNotExists(userRepository, roleRepository,
                    bookingEmail, bookingPassword, "BOOKING_SERVICE");
        };
    }

    private void createUserIfNotExists(UserRepository userRepository,
                                       RoleRepository roleRepository,
                                       String email,
                                       String password,
                                       String roleName) {
        if (userRepository.existsByEmail(email)) {
            log.debug("User already exists, skipping: email={}", email);
            return;
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalStateException("Role not found: " + roleName));

        try {
            User user = User.builder()
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .created(Instant.now())
                    .roles(new HashSet<>(Set.of(role)))
                    .build();
            userRepository.save(user);
            log.info("Default user created: email={}, role={}", email, roleName);
        } catch (DataIntegrityViolationException e) {
            log.info("User already created by another instance: email={}", email);
        }
    }
}