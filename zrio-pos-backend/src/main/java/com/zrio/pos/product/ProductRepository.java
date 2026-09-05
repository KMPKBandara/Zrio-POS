package com.zrio.pos.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

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

    Optional<Product> findByBarcodeAndActiveTrue(
            String barcode
    );

    Optional<Product> findByItemCodeIgnoreCaseAndActiveTrue(
            String itemCode
    );

    @Query("""
            SELECT p
            FROM Product p
            WHERE p.active = true
              AND (
                  LOWER(p.name)
                      LIKE LOWER(CONCAT('%', :query, '%'))
                  OR
                  LOWER(p.itemCode)
                      LIKE LOWER(CONCAT('%', :query, '%'))
              )
            ORDER BY p.name ASC
            """)
    List<Product> searchActiveProducts(
            @Param("query") String query
    );
}