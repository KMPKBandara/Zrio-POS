package com.zrio.pos.user;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class InitialOwnerBootstrap implements ApplicationRunner {

    private final UserRepository userRepository;
    private final UserService userService;
    private final Environment environment;

    public InitialOwnerBootstrap(
            UserRepository userRepository,
            UserService userService,
            Environment environment
    ) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.environment = environment;
    }

    @Override
    public void run(ApplicationArguments args) {

        if (userRepository.count() > 0) {
            return;
        }

        String username =
                environment.getProperty("ZRIO_OWNER_USERNAME");

        String password =
                environment.getProperty("ZRIO_OWNER_PASSWORD");

        String fullName =
                environment.getProperty(
                        "ZRIO_OWNER_NAME",
                        "Shop Owner"
                );

        if (username == null || username.isBlank()
                || password == null || password.isBlank()) {

            System.out.println(
                    "No initial Zrio owner configured."
            );

            return;
        }

        userService.createUser(
                username,
                password,
                fullName,
                UserRole.OWNER
        );

        System.out.println(
                "Initial Zrio owner account created."
        );
    }
}