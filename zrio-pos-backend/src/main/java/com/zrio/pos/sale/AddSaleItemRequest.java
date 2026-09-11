package com.zrio.pos.sale;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddSaleItemRequest(

        @NotNull
        Long productId,

        @NotNull
        @Min(1)
        Integer quantity

) {
}