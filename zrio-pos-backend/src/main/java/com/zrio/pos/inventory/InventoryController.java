package com.zrio.pos.inventory;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(
            InventoryService inventoryService
    ) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public List<InventoryResponse> getInventory() {

        return inventoryService
                .findActiveInventory();
    }

    @GetMapping("/products/{productId}")
    public InventoryResponse getProductInventory(
            @PathVariable Long productId
    ) {

        try {

            return inventoryService
                    .findProductInventory(productId);

        } catch (NoSuchElementException exception) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    exception.getMessage()
            );
        }
    }

    @GetMapping("/products/{productId}/movements")
    public List<StockMovementResponse>
    getProductMovements(
            @PathVariable Long productId
    ) {

        try {

            return inventoryService
                    .findProductMovements(productId);

        } catch (NoSuchElementException exception) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    exception.getMessage()
            );
        }
    }
}