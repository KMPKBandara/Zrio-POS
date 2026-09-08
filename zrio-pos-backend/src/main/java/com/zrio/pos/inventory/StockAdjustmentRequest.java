package com.zrio.pos.inventory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StockAdjustmentRequest(

        @NotNull
        Integer quantityChange,

        @NotBlank
        @Size(max = 255)
        String note

) {
}