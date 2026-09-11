package com.zrio.pos.sale;

import com.zrio.pos.product.Product;
import com.zrio.pos.product.ProductRepository;
import com.zrio.pos.user.User;
import com.zrio.pos.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class SaleService {

    private static final BigDecimal ZERO_MONEY =
            new BigDecimal("0.00");

    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public SaleService(
            SaleRepository saleRepository,
            SaleItemRepository saleItemRepository,
            ProductRepository productRepository,
            UserRepository userRepository
    ) {
        this.saleRepository = saleRepository;
        this.saleItemRepository = saleItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public SaleResponse startSale(
            String username
    ) {

        User cashier =
                findUser(username);

        Sale sale = new Sale();

        sale.setCashier(cashier);
        sale.setStatus(SaleStatus.DRAFT);

        sale.setSubtotal(ZERO_MONEY);
        sale.setDiscountTotal(ZERO_MONEY);
        sale.setGrandTotal(ZERO_MONEY);

        Sale saved =
                saleRepository.save(sale);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public SaleResponse findSale(
            Long saleId
    ) {

        return toResponse(
                findRequiredSale(saleId)
        );
    }

    @Transactional(readOnly = true)
    public SaleDetailsResponse findSaleDetails(
            Long saleId
    ) {

        Sale sale =
                findRequiredSale(saleId);

        return toDetailsResponse(sale);
    }

    @Transactional
    public SaleDetailsResponse addItem(
            Long saleId,
            Long productId,
            int quantity
    ) {

        if (quantity <= 0) {

            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );
        }

        Sale sale =
                findRequiredSale(saleId);

        ensureDraft(sale);

        Product product =
                findActiveProduct(productId);

        SaleItem item =
                saleItemRepository
                        .findBySaleIdAndProductId(
                                saleId,
                                productId
                        )
                        .orElse(null);

        if (item == null) {

            item = new SaleItem();

            item.setSale(sale);
            item.setProduct(product);

            item.setItemCodeSnapshot(
                    product.getItemCode()
            );

            item.setProductNameSnapshot(
                    product.getName()
            );

            item.setUnitPrice(
                    product.getSellingPrice()
            );

            item.setQuantity(quantity);

            item.setLineTotal(
                    calculateLineTotal(
                            item.getUnitPrice(),
                            quantity
                    )
            );

            saleItemRepository.save(item);

        } else {

            int newQuantity =
                    addQuantitiesSafely(
                            item.getQuantity(),
                            quantity
                    );

            item.setQuantity(newQuantity);

            item.setLineTotal(
                    calculateLineTotal(
                            item.getUnitPrice(),
                            newQuantity
                    )
            );
        }

        recalculateSaleTotals(sale);

        return toDetailsResponse(sale);
    }

    @Transactional
    public SaleDetailsResponse updateItemQuantity(
            Long saleId,
            Long itemId,
            int quantity
    ) {

        if (quantity <= 0) {

            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );
        }

        Sale sale =
                findRequiredSale(saleId);

        ensureDraft(sale);

        SaleItem item =
                findRequiredSaleItem(
                        saleId,
                        itemId
                );

        item.setQuantity(quantity);

        item.setLineTotal(
                calculateLineTotal(
                        item.getUnitPrice(),
                        quantity
                )
        );

        recalculateSaleTotals(sale);

        return toDetailsResponse(sale);
    }

    @Transactional
    public SaleDetailsResponse removeItem(
            Long saleId,
            Long itemId
    ) {

        Sale sale =
                findRequiredSale(saleId);

        ensureDraft(sale);

        SaleItem item =
                findRequiredSaleItem(
                        saleId,
                        itemId
                );

        saleItemRepository.delete(item);

        /*
         * Ensures the DELETE reaches the persistence
         * context before we query the remaining lines
         * while recalculating totals.
         */
        saleItemRepository.flush();

        recalculateSaleTotals(sale);

        return toDetailsResponse(sale);
    }

    private Sale findRequiredSale(
            Long saleId
    ) {

        return saleRepository
                .findById(saleId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Sale not found"
                        )
                );
    }

    private SaleItem findRequiredSaleItem(
            Long saleId,
            Long itemId
    ) {

        return saleItemRepository
                .findByIdAndSaleId(
                        itemId,
                        saleId
                )
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Sale item not found"
                        )
                );
    }

    private Product findActiveProduct(
            Long productId
    ) {

        Product product =
                productRepository
                        .findById(productId)
                        .orElseThrow(() ->
                                new NoSuchElementException(
                                        "Product not found"
                                )
                        );

        if (!product.isActive()) {

            throw new IllegalArgumentException(
                    "Cannot add an inactive product to a sale"
            );
        }

        return product;
    }

    private void ensureDraft(
            Sale sale
    ) {

        if (sale.getStatus()
                != SaleStatus.DRAFT) {

            throw new IllegalStateException(
                    "Only DRAFT sales can be modified"
            );
        }
    }

    private int addQuantitiesSafely(
            int currentQuantity,
            int quantityToAdd
    ) {

        long calculated =
                (long) currentQuantity
                        + quantityToAdd;

        if (calculated > Integer.MAX_VALUE) {

            throw new IllegalArgumentException(
                    "Quantity is too large"
            );
        }

        return (int) calculated;
    }

    private BigDecimal calculateLineTotal(
            BigDecimal unitPrice,
            int quantity
    ) {

        return unitPrice.multiply(
                BigDecimal.valueOf(quantity)
        );
    }

    private void recalculateSaleTotals(
            Sale sale
    ) {

        List<SaleItem> items =
                saleItemRepository
                        .findBySaleIdOrderByIdAsc(
                                sale.getId()
                        );

        BigDecimal subtotal =
                items.stream()
                        .map(SaleItem::getLineTotal)
                        .reduce(
                                ZERO_MONEY,
                                BigDecimal::add
                        );

        sale.setSubtotal(subtotal);

        /*
         * Lesson 14 has no discounts yet.
         * Lesson 15 will improve this section.
         */
        sale.setDiscountTotal(ZERO_MONEY);

        sale.setGrandTotal(subtotal);
    }

    private User findUser(
            String username
    ) {

        return userRepository
                .findByUsernameIgnoreCase(username)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Authenticated user not found"
                        )
                );
    }

    private SaleResponse toResponse(
            Sale sale
    ) {

        return new SaleResponse(
                sale.getId(),
                formatSaleNumber(sale.getId()),

                sale.getCashier().getId(),
                sale.getCashier().getUsername(),
                sale.getCashier().getFullName(),

                sale.getStatus().name(),

                sale.getSubtotal(),
                sale.getDiscountTotal(),
                sale.getGrandTotal(),

                sale.getCreatedAt(),
                sale.getCompletedAt()
        );
    }

    private SaleDetailsResponse toDetailsResponse(
            Sale sale
    ) {

        List<SaleItemResponse> items =
                saleItemRepository
                        .findBySaleIdOrderByIdAsc(
                                sale.getId()
                        )
                        .stream()
                        .map(this::toItemResponse)
                        .toList();

        return new SaleDetailsResponse(
                toResponse(sale),
                items
        );
    }

    private SaleItemResponse toItemResponse(
            SaleItem item
    ) {

        return new SaleItemResponse(
                item.getId(),
                item.getProduct().getId(),

                item.getItemCodeSnapshot(),
                item.getProductNameSnapshot(),

                item.getUnitPrice(),
                item.getQuantity(),
                item.getLineTotal()
        );
    }

    private String formatSaleNumber(
            Long saleId
    ) {

        return "S%06d".formatted(saleId);
    }
}