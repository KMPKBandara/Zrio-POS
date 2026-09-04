package com.zrio.pos.product;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(
            ProductService productService
    ) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductResponse> getActiveProducts() {

        return productService.findActiveProducts();
    }

    @GetMapping("/all")
    public List<ProductResponse> getAllProducts() {

        return productService.findAllProducts();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(
            @Valid @RequestBody
            CreateProductRequest request
    ) {

        try {

            return productService.createProduct(request);

        } catch (NoSuchElementException exception) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    exception.getMessage()
            );

        } catch (IllegalArgumentException exception) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    exception.getMessage()
            );
        }
    }

    @PutMapping("/{id}")
    public ProductResponse updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody
            UpdateProductRequest request
    ) {

        try {

            return productService.updateProduct(
                    id,
                    request
            );

        } catch (NoSuchElementException exception) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    exception.getMessage()
            );

        } catch (IllegalArgumentException exception) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    exception.getMessage()
            );
        }
    }

    @PatchMapping("/{id}/active")
    public ProductResponse updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody
            UpdateProductStatusRequest request
    ) {

        try {

            return productService.changeStatus(
                    id,
                    request.active()
            );

        } catch (NoSuchElementException exception) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    exception.getMessage()
            );
        }
    }
}