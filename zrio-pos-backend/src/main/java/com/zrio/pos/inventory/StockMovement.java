package com.zrio.pos.inventory;

import com.zrio.pos.product.Product;
import com.zrio.pos.user.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "stock_movements")
public class StockMovement {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "product_id",
            nullable = false
    )
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "movement_type",
            nullable = false,
            length = 30
    )
    private StockMovementType movementType;

    @Column(
            name = "quantity_change",
            nullable = false
    )
    private int quantityChange;

    @Column(
            name = "quantity_after",
            nullable = false
    )
    private int quantityAfter;

    @Column(length = 255)
    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(
            name = "created_at",
            nullable = false
    )
    private Instant createdAt;

    public StockMovement() {
    }

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public StockMovementType getMovementType() {
        return movementType;
    }

    public void setMovementType(
            StockMovementType movementType
    ) {
        this.movementType = movementType;
    }

    public int getQuantityChange() {
        return quantityChange;
    }

    public void setQuantityChange(
            int quantityChange
    ) {
        this.quantityChange = quantityChange;
    }

    public int getQuantityAfter() {
        return quantityAfter;
    }

    public void setQuantityAfter(
            int quantityAfter
    ) {
        this.quantityAfter = quantityAfter;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}