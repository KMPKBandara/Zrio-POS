package com.zrio.pos.sale;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    private final SaleService saleService;

    public SaleController(
            SaleService saleService
    ) {
        this.saleService = saleService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SaleResponse startSale(
            Authentication authentication
    ) {

        return saleService.startSale(
                authentication.getName()
        );
    }

    @GetMapping("/{id}")
    public SaleResponse getSale(
            @PathVariable Long id
    ) {

        try {

            return saleService.findSale(id);

        } catch (NoSuchElementException exception) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    exception.getMessage()
            );
        }
    }

    @GetMapping("/{id}/details")
    public SaleDetailsResponse getSaleDetails(
            @PathVariable Long id
    ) {

        try {

            return saleService
                    .findSaleDetails(id);

        } catch (NoSuchElementException exception) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    exception.getMessage()
            );
        }
    }

    @PostMapping("/{saleId}/items")
    public SaleDetailsResponse addItem(
            @PathVariable Long saleId,
            @Valid @RequestBody
            AddSaleItemRequest request
    ) {

        try {

            return saleService.addItem(
                    saleId,
                    request.productId(),
                    request.quantity()
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

        } catch (IllegalStateException exception) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    exception.getMessage()
            );
        }
    }

    @PatchMapping(
            "/{saleId}/items/{itemId}"
    )
    public SaleDetailsResponse updateItemQuantity(
            @PathVariable Long saleId,
            @PathVariable Long itemId,
            @Valid @RequestBody
            UpdateSaleItemQuantityRequest request
    ) {

        try {

            return saleService
                    .updateItemQuantity(
                            saleId,
                            itemId,
                            request.quantity()
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

        } catch (IllegalStateException exception) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    exception.getMessage()
            );
        }
    }

    @DeleteMapping(
            "/{saleId}/items/{itemId}"
    )
    public SaleDetailsResponse removeItem(
            @PathVariable Long saleId,
            @PathVariable Long itemId
    ) {

        try {

            return saleService
                    .removeItem(
                            saleId,
                            itemId
                    );

        } catch (NoSuchElementException exception) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    exception.getMessage()
            );

        } catch (IllegalStateException exception) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    exception.getMessage()
            );
        }
    }
}