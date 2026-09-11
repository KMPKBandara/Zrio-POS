package com.zrio.pos.sale;

import com.zrio.pos.product.Product;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "sale_items")
public class SaleItem {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "sale_id",
            nullable = false
    )
    private Sale sale;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "product_id",
            nullable = false
    )
    private Product product;

    @Column(
            name = "item_code_snapshot",
            nullable = false,
            length = 50
    )
    private String itemCodeSnapshot;

    @Column(
            name = "product_name_snapshot",
            nullable = false,
            length = 150
    )
    private String productNameSnapshot;

    @Column(
            name = "unit_price",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private int quantity;

    @Column(
            name = "line_total",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal lineTotal;

    public SaleItem() {
    }

    public Long getId() {
        return id;
    }

    public Sale getSale() {
        return sale;
    }

    public void setSale(Sale sale) {
        this.sale = sale;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getItemCodeSnapshot() {
        return itemCodeSnapshot;
    }

    public void setItemCodeSnapshot(
            String itemCodeSnapshot
    ) {
        this.itemCodeSnapshot = itemCodeSnapshot;
    }

    public String getProductNameSnapshot() {
        return productNameSnapshot;
    }

    public void setProductNameSnapshot(
            String productNameSnapshot
    ) {
        this.productNameSnapshot =
                productNameSnapshot;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(
            BigDecimal unitPrice
    ) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(
            BigDecimal lineTotal
    ) {
        this.lineTotal = lineTotal;
    }
}