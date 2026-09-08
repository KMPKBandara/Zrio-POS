package com.zrio.pos.inventory;

public record StockChangeResponse(

        InventoryResponse inventory,
        StockMovementResponse movement

) {
}