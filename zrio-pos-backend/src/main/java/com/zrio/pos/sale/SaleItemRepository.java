package com.zrio.pos.sale;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SaleItemRepository
        extends JpaRepository<SaleItem, Long> {

    List<SaleItem> findBySaleIdOrderByIdAsc(
            Long saleId
    );

    Optional<SaleItem> findBySaleIdAndProductId(
            Long saleId,
            Long productId
    );

    Optional<SaleItem> findByIdAndSaleId(
            Long id,
            Long saleId
    );
}