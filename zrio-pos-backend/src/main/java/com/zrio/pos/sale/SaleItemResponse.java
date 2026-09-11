package com.zrio.pos.sale;

import java.math.BigDecimal;

public record SaleItemResponse(

        Long id,

        Long productId,

        String itemCode,
        String productName,

        BigDecimal unitPrice,
        int quantity,
        BigDecimal lineTotal

) {
}