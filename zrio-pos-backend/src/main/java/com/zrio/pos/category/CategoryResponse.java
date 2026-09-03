package com.zrio.pos.category;

public record CategoryResponse(
        Long id,
        String name,
        boolean active
) {
}