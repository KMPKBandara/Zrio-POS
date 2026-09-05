package com.zrio.pos.inventory;

import java.time.Instant;

public record StockMovementResponse(

        Long id,

        Long productId,
        String itemCode,
        String productName,

        String movementType,

        int quantityChange,
        int quantityAfter,

        String note,

        Long createdByUserId,
        String createdByUsername,

        Instant createdAt

) {
}