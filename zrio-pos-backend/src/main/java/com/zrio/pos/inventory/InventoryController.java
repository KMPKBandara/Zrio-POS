package com.zrio.pos.inventory;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;

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

    @PostMapping("/products/{productId}/stock-in")
    @ResponseStatus(HttpStatus.CREATED)
    public StockChangeResponse stockIn(
            @PathVariable Long productId,
            @Valid @RequestBody
            StockInRequest request,
            Authentication authentication
    ) {

        try {

            return inventoryService.stockIn(
                    productId,
                    request.quantity(),
                    request.note(),
                    authentication.getName()
            );

        } catch (NoSuchElementException exception) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    exception.getMessage()
            );

        } catch (IllegalArgumentException exception) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    exception.getMessage()
            );
        }
    }

    @PostMapping("/products/{productId}/adjustments")
    @ResponseStatus(HttpStatus.CREATED)
    public StockChangeResponse adjustStock(
            @PathVariable Long productId,
            @Valid @RequestBody
            StockAdjustmentRequest request,
            Authentication authentication
    ) {

        try {

            return inventoryService.adjustStock(
                    productId,
                    request.quantityChange(),
                    request.note(),
                    authentication.getName()
            );

        } catch (NoSuchElementException exception) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    exception.getMessage()
            );

        } catch (IllegalArgumentException exception) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    exception.getMessage()
            );
        }
    }

    @GetMapping("/low-stock")
    public List<InventoryResponse> getLowStock() {

        return inventoryService.findLowStock();
    }

    @GetMapping("/out-of-stock")
    public List<InventoryResponse> getOutOfStock() {

        return inventoryService.findOutOfStock();
    }

    @GetMapping("/search")
    public List<InventoryResponse> searchInventory(
            @RequestParam String q
    ) {

        try {

            return inventoryService
                    .searchInventory(q);

        } catch (IllegalArgumentException exception) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    exception.getMessage()
            );
        }
    }
}