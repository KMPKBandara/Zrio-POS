package com.zrio.pos.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Comparator;
import java.util.List;

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

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException(
                    "Username cannot be blank"
            );
        }

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

    @Transactional
    public User createCashier(
            String username,
            String rawPassword,
            String fullName
    ) {

        return createUser(
                username,
                rawPassword,
                fullName,
                UserRole.CASHIER
        );
    }

    @Transactional(readOnly = true)
    public List<User> findAllUsers() {

        return userRepository
                .findAll()
                .stream()
                .sorted(
                        Comparator.comparing(User::getUsername)
                )
                .toList();
    }

    @Transactional
    public User changeCashierStatus(
            Long userId,
            boolean active
    ) {

        User user = userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"
                        )
                );

        if (user.getRole() != UserRole.CASHIER) {
            throw new IllegalArgumentException(
                    "Only cashier accounts can be enabled or disabled"
            );
        }

        user.setActive(active);

        return user;
    }
}