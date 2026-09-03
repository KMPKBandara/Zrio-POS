package com.zrio.pos.user;

public record UserResponse(
        Long id,
        String username,
        String fullName,
        String role,
        boolean active
) {
}