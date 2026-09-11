package com.zrio.pos.sale;

import java.util.List;

public record SaleDetailsResponse(

        SaleResponse sale,
        List<SaleItemResponse> items

) {
}