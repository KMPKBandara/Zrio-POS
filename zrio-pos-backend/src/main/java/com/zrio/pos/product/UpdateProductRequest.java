package com.zrio.pos.product;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record UpdateProductRequest(

        @NotNull
        Long categoryId,

        @NotBlank
        @Size(max = 50)
        String itemCode,

        @NotBlank
        @Size(max = 150)
        String name,

        @Size(max = 100)
        String barcode,

        @NotNull
        @DecimalMin("0.00")
        @Digits(integer = 10, fraction = 2)
        BigDecimal costPrice,

        @NotNull
        @DecimalMin("0.00")
        @Digits(integer = 10, fraction = 2)
        BigDecimal sellingPrice,

        @NotNull
        @Min(0)
        Integer lowStockThreshold

) {
}