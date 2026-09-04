package com.zrio.pos.product;

import jakarta.validation.constraints.NotNull;

public record UpdateProductStatusRequest(

        @NotNull
        Boolean active

) {
}