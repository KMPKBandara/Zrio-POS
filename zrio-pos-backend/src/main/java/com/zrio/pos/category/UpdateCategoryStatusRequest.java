package com.zrio.pos.category;

import jakarta.validation.constraints.NotNull;

public record UpdateCategoryStatusRequest(

        @NotNull
        Boolean active

) {
}