package com.zrio.pos.inventory;

import com.zrio.pos.product.Product;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "inventory")
public class Inventory {

    @Id
    @Column(name = "product_id")
    private Long productId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(
            name = "quantity_on_hand",
            nullable = false
    )
    private int quantityOnHand;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private Instant updatedAt;

    public Inventory() {
    }

    @PrePersist
    void onCreate() {
        updatedAt = Instant.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public Long getProductId() {
        return productId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantityOnHand() {
        return quantityOnHand;
    }

    public void setQuantityOnHand(
            int quantityOnHand
    ) {
        this.quantityOnHand = quantityOnHand;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}