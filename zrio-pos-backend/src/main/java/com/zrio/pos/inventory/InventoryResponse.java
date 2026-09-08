package com.zrio.pos.inventory;

public record InventoryResponse(

        Long productId,
        String itemCode,
        String productName,

        Long categoryId,
        String categoryName,

        int quantityOnHand,
        int lowStockThreshold,

        StockStatus stockStatus

) {
}