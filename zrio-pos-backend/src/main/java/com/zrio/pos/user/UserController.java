package com.zrio.pos.user;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(
            UserService userService
    ) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponse> getUsers() {

        return userService
                .findAllUsers()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @PostMapping("/cashiers")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createCashier(
            @Valid @RequestBody
            CreateCashierRequest request
    ) {

        try {

            User user = userService.createCashier(
                    request.username(),
                    request.password(),
                    request.fullName()
            );

            return toResponse(user);

        } catch (IllegalArgumentException exception) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    exception.getMessage()
            );
        }
    }

    @PatchMapping("/{id}/active")
    public UserResponse updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody
            UpdateUserStatusRequest request
    ) {

        try {

            User user =
                    userService.changeCashierStatus(
                            id,
                            request.active()
                    );

            return toResponse(user);

        } catch (IllegalArgumentException exception) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    exception.getMessage()
            );
        }
    }

    private UserResponse toResponse(User user) {

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getRole().name(),
                user.isActive()
        );
    }
}