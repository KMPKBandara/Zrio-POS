package com.zrio.pos.product;

import com.zrio.pos.category.Category;
import com.zrio.pos.category.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import com.zrio.pos.inventory.Inventory;
import com.zrio.pos.inventory.InventoryRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryRepository inventoryRepository;

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            InventoryRepository inventoryRepository
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public ProductResponse createProduct(
            CreateProductRequest request
    ) {

        String itemCode =
                cleanItemCode(request.itemCode());

        String name =
                cleanName(request.name());

        String barcode =
                cleanBarcode(request.barcode());

        validateItemCodeForCreate(itemCode);
        validateBarcodeForCreate(barcode);

        Category category =
                findActiveCategory(request.categoryId());

        Product product = new Product();

        product.setCategory(category);
        product.setItemCode(itemCode);
        product.setName(name);
        product.setBarcode(barcode);
        product.setCostPrice(request.costPrice());
        product.setSellingPrice(request.sellingPrice());
        product.setLowStockThreshold(
                request.lowStockThreshold()
        );
        product.setActive(true);

        Product saved =
                productRepository.save(product);

        Inventory inventory = new Inventory();

        inventory.setProduct(saved);
        inventory.setQuantityOnHand(0);

        inventoryRepository.save(inventory);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findActiveProducts() {

        return productRepository
                .findByActiveTrueOrderByNameAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findAllProducts() {

        return productRepository
                .findAllByOrderByNameAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ProductResponse updateProduct(
            Long id,
            UpdateProductRequest request
    ) {

        Product product = findRequired(id);

        String itemCode =
                cleanItemCode(request.itemCode());

        String name =
                cleanName(request.name());

        String barcode =
                cleanBarcode(request.barcode());

        if (productRepository
                .existsByItemCodeIgnoreCaseAndIdNot(
                        itemCode,
                        id
                )) {

            throw new IllegalArgumentException(
                    "Item code already exists"
            );
        }

        if (barcode != null
                && productRepository
                .existsByBarcodeAndIdNot(
                        barcode,
                        id
                )) {

            throw new IllegalArgumentException(
                    "Barcode already exists"
            );
        }

        Category category =
                findActiveCategory(request.categoryId());

        product.setCategory(category);
        product.setItemCode(itemCode);
        product.setName(name);
        product.setBarcode(barcode);
        product.setCostPrice(request.costPrice());
        product.setSellingPrice(request.sellingPrice());
        product.setLowStockThreshold(
                request.lowStockThreshold()
        );

        return toResponse(product);
    }

    @Transactional
    public ProductResponse changeStatus(
            Long id,
            boolean active
    ) {

        Product product = findRequired(id);

        product.setActive(active);

        return toResponse(product);
    }

    private Product findRequired(Long id) {

        return productRepository
                .findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Product not found"
                        )
                );
    }

    private Category findActiveCategory(Long id) {

        Category category =
                categoryRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new NoSuchElementException(
                                        "Category not found"
                                )
                        );

        if (!category.isActive()) {
            throw new IllegalArgumentException(
                    "Cannot assign product to an inactive category"
            );
        }

        return category;
    }

    private void validateItemCodeForCreate(
            String itemCode
    ) {

        if (productRepository
                .existsByItemCodeIgnoreCase(itemCode)) {

            throw new IllegalArgumentException(
                    "Item code already exists"
            );
        }
    }

    private void validateBarcodeForCreate(
            String barcode
    ) {

        if (barcode != null
                && productRepository
                .existsByBarcode(barcode)) {

            throw new IllegalArgumentException(
                    "Barcode already exists"
            );
        }
    }

    private String cleanItemCode(String itemCode) {

        if (itemCode == null || itemCode.isBlank()) {
            throw new IllegalArgumentException(
                    "Item code cannot be blank"
            );
        }

        return itemCode
                .trim()
                .toUpperCase(Locale.ROOT);
    }

    private String cleanName(String name) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Product name cannot be blank"
            );
        }

        return name.trim();
    }

    private String cleanBarcode(String barcode) {

        if (barcode == null || barcode.isBlank()) {
            return null;
        }

        return barcode.trim();
    }

    private ProductResponse toResponse(
            Product product
    ) {

        return new ProductResponse(
                product.getId(),
                product.getItemCode(),
                product.getName(),
                product.getBarcode(),

                product.getCategory().getId(),
                product.getCategory().getName(),

                product.getCostPrice(),
                product.getSellingPrice(),

                product.getLowStockThreshold(),

                product.getImagePath(),

                product.isActive()
        );
    }

    @Transactional(readOnly = true)
    public ProductResponse lookupProduct(String code) {

        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException(
                    "Lookup code cannot be blank"
            );
        }

        String cleanedCode = code.trim();

        Product product = productRepository
                .findByBarcodeAndActiveTrue(cleanedCode)
                .or(() ->
                        productRepository
                                .findByItemCodeIgnoreCaseAndActiveTrue(
                                        cleanedCode
                                )
                )
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Product not found"
                        )
                );

        return toResponse(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> searchProducts(
            String query
    ) {

        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException(
                    "Search query cannot be blank"
            );
        }

        String cleanedQuery = query.trim();

        return productRepository
                .searchActiveProducts(cleanedQuery)
                .stream()
                .map(this::toResponse)
                .toList();
    }
}