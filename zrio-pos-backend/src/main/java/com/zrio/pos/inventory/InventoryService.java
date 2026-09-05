package com.zrio.pos.inventory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final StockMovementRepository
            stockMovementRepository;

    public InventoryService(
            InventoryRepository inventoryRepository,
            StockMovementRepository stockMovementRepository
    ) {
        this.inventoryRepository = inventoryRepository;
        this.stockMovementRepository =
                stockMovementRepository;
    }

    @Transactional(readOnly = true)
    public List<InventoryResponse>
    findActiveInventory() {

        return inventoryRepository
                .findByProductActiveTrueOrderByProductNameAsc()
                .stream()
                .map(this::toInventoryResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public InventoryResponse findProductInventory(
            Long productId
    ) {

        Inventory inventory =
                inventoryRepository
                        .findByProductId(productId)
                        .orElseThrow(() ->
                                new NoSuchElementException(
                                        "Inventory not found"
                                )
                        );

        return toInventoryResponse(inventory);
    }

    @Transactional(readOnly = true)
    public List<StockMovementResponse>
    findProductMovements(Long productId) {

        if (!inventoryRepository.existsById(productId)) {
            throw new NoSuchElementException(
                    "Inventory not found"
            );
        }

        return stockMovementRepository
                .findByProductIdOrderByCreatedAtDesc(
                        productId
                )
                .stream()
                .map(this::toMovementResponse)
                .toList();
    }

    private InventoryResponse toInventoryResponse(
            Inventory inventory
    ) {

        return new InventoryResponse(
                inventory.getProduct().getId(),
                inventory.getProduct().getItemCode(),
                inventory.getProduct().getName(),

                inventory.getProduct()
                        .getCategory()
                        .getId(),

                inventory.getProduct()
                        .getCategory()
                        .getName(),

                inventory.getQuantityOnHand(),

                inventory.getProduct()
                        .getLowStockThreshold()
        );
    }

    private StockMovementResponse toMovementResponse(
            StockMovement movement
    ) {

        Long userId = null;
        String username = null;

        if (movement.getCreatedBy() != null) {

            userId =
                    movement.getCreatedBy().getId();

            username =
                    movement.getCreatedBy().getUsername();
        }

        return new StockMovementResponse(
                movement.getId(),

                movement.getProduct().getId(),
                movement.getProduct().getItemCode(),
                movement.getProduct().getName(),

                movement.getMovementType().name(),

                movement.getQuantityChange(),
                movement.getQuantityAfter(),

                movement.getNote(),

                userId,
                username,

                movement.getCreatedAt()
        );
    }
}