package com.zrio.pos.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User createUser(
            String username,
            String rawPassword,
            String fullName,
            UserRole role
    ) {

        String normalizedUsername =
                username.trim().toLowerCase(Locale.ROOT);

        if (normalizedUsername.isBlank()) {
            throw new IllegalArgumentException(
                    "Username cannot be blank"
            );
        }

        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException(
                    "Full name cannot be blank"
            );
        }

        if (rawPassword == null || rawPassword.length() < 8) {
            throw new IllegalArgumentException(
                    "Password must contain at least 8 characters"
            );
        }

        if (userRepository.existsByUsernameIgnoreCase(
                normalizedUsername
        )) {
            throw new IllegalArgumentException(
                    "Username already exists"
            );
        }

        User user = new User();

        user.setUsername(normalizedUsername);
        user.setPasswordHash(
                passwordEncoder.encode(rawPassword)
        );
        user.setFullName(fullName.trim());
        user.setRole(role);
        user.setActive(true);

        return userRepository.save(user);
    }
}