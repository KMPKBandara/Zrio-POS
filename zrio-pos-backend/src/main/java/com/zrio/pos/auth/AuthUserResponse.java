package com.zrio.pos.auth;

public record AuthUserResponse(
        Long id,
        String username,
        String fullName,
        String role
) {
}