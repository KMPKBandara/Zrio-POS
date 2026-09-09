package com.zrio.pos.sale;

import java.math.BigDecimal;
import java.time.Instant;

public record SaleResponse(

        Long id,
        String saleNumber,

        Long cashierId,
        String cashierUsername,
        String cashierFullName,

        String status,

        BigDecimal subtotal,
        BigDecimal discountTotal,
        BigDecimal grandTotal,

        Instant createdAt,
        Instant completedAt

) {
}