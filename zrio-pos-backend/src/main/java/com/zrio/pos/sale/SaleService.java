package com.zrio.pos.sale;

import com.zrio.pos.user.User;
import com.zrio.pos.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

@Service
public class SaleService {

    private static final BigDecimal ZERO_MONEY =
            new BigDecimal("0.00");

    private final SaleRepository saleRepository;
    private final UserRepository userRepository;

    public SaleService(
            SaleRepository saleRepository,
            UserRepository userRepository
    ) {
        this.saleRepository = saleRepository;
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

        Sale sale =
                saleRepository
                        .findById(saleId)
                        .orElseThrow(() ->
                                new NoSuchElementException(
                                        "Sale not found"
                                )
                        );

        return toResponse(sale);
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

    private String formatSaleNumber(
            Long saleId
    ) {

        return "S%06d".formatted(saleId);
    }
}