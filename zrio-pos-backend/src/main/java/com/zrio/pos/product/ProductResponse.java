package com.zrio.pos.product;

import java.math.BigDecimal;

public record ProductResponse(

        Long id,

        String itemCode,
        String name,
        String barcode,

        Long categoryId,
        String categoryName,

        BigDecimal costPrice,
        BigDecimal sellingPrice,

        int lowStockThreshold,

        String imagePath,

        boolean active

) {
}