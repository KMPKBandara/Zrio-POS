package com.zrio.pos.inventory;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StockInRequest(

        @NotNull
        @Min(1)
        Integer quantity,

        @Size(max = 255)
        String note

) {
}