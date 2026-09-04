package com.zrio.pos.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository
        extends JpaRepository<Product, Long> {

    boolean existsByItemCodeIgnoreCase(String itemCode);

    boolean existsByItemCodeIgnoreCaseAndIdNot(
            String itemCode,
            Long id
    );

    boolean existsByBarcode(String barcode);

    boolean existsByBarcodeAndIdNot(
            String barcode,
            Long id
    );

    List<Product> findByActiveTrueOrderByNameAsc();

    List<Product> findAllByOrderByNameAsc();
}