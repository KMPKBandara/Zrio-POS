package com.zrio.pos.inventory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import com.zrio.pos.user.User;
import com.zrio.pos.user.UserRepository;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final StockMovementRepository stockMovementRepository;
    private final UserRepository userRepository;

    public InventoryService(
            InventoryRepository inventoryRepository,
            StockMovementRepository stockMovementRepository,
            UserRepository userRepository
    ) {
        this.inventoryRepository = inventoryRepository;
        this.stockMovementRepository =
                stockMovementRepository;
        this.userRepository = userRepository;
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

    @Transactional
    public StockChangeResponse stockIn(
            Long productId,
            int quantity,
            String note,
            String username
    ) {

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Stock-in quantity must be greater than zero"
            );
        }

        Inventory inventory =
                findInventoryForUpdate(productId);

        User user =
                findUser(username);

        int newQuantity = calculateNewQuantity(
                inventory.getQuantityOnHand(),
                quantity
        );

        inventory.setQuantityOnHand(newQuantity);

        StockMovement movement =
                createMovement(
                        inventory,
                        StockMovementType.STOCK_IN,
                        quantity,
                        newQuantity,
                        cleanOptionalNote(note),
                        user
                );

        return new StockChangeResponse(
                toInventoryResponse(inventory),
                toMovementResponse(movement)
        );
    }

    @Transactional
    public StockChangeResponse adjustStock(
            Long productId,
            int quantityChange,
            String note,
            String username
    ) {

        if (quantityChange == 0) {
            throw new IllegalArgumentException(
                    "Adjustment quantity cannot be zero"
            );
        }

        String cleanedNote =
                cleanRequiredNote(note);

        Inventory inventory =
                findInventoryForUpdate(productId);

        User user =
                findUser(username);

        int newQuantity =
                calculateNewQuantity(
                        inventory.getQuantityOnHand(),
                        quantityChange
                );

        StockMovementType movementType;

        if (quantityChange > 0) {

            movementType =
                    StockMovementType.ADJUSTMENT_IN;

        } else {

            movementType =
                    StockMovementType.ADJUSTMENT_OUT;
        }

        inventory.setQuantityOnHand(newQuantity);

        StockMovement movement =
                createMovement(
                        inventory,
                        movementType,
                        quantityChange,
                        newQuantity,
                        cleanedNote,
                        user
                );

        return new StockChangeResponse(
                toInventoryResponse(inventory),
                toMovementResponse(movement)
        );
    }

    private Inventory findInventoryForUpdate(
            Long productId
    ) {

        return inventoryRepository
                .findByProductIdForUpdate(productId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Inventory not found"
                        )
                );
    }

    private int calculateNewQuantity(
            int currentQuantity,
            int quantityChange
    ) {

        long calculated =
                (long) currentQuantity
                        + quantityChange;

        if (calculated < 0) {
            throw new IllegalArgumentException(
                    "Stock cannot become negative"
            );
        }

        if (calculated > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(
                    "Stock quantity is too large"
            );
        }

        return (int) calculated;
    }

    private User findUser(String username) {

        return userRepository
                .findByUsernameIgnoreCase(username)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Authenticated user not found"
                        )
                );
    }

    private StockMovement createMovement(
            Inventory inventory,
            StockMovementType movementType,
            int quantityChange,
            int quantityAfter,
            String note,
            User user
    ) {

        StockMovement movement =
                new StockMovement();

        movement.setProduct(
                inventory.getProduct()
        );

        movement.setMovementType(
                movementType
        );

        movement.setQuantityChange(
                quantityChange
        );

        movement.setQuantityAfter(
                quantityAfter
        );

        movement.setNote(note);

        movement.setCreatedBy(user);

        return stockMovementRepository
                .save(movement);
    }

    private String cleanOptionalNote(
            String note
    ) {

        if (note == null || note.isBlank()) {
            return null;
        }

        return note.trim();
    }

    private String cleanRequiredNote(
            String note
    ) {

        if (note == null || note.isBlank()) {
            throw new IllegalArgumentException(
                    "Adjustment note cannot be blank"
            );
        }

        return note.trim();
    }
}