package com.zrio.pos.auth;

public record CsrfResponse(
        String headerName,
        String token
) {
}