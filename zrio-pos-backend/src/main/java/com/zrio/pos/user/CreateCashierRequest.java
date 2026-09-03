package com.zrio.pos.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCashierRequest(

        @NotBlank
        @Size(max = 50)
        String username,

        @NotBlank
        @Size(min = 8, max = 200)
        String password,

        @NotBlank
        @Size(max = 120)
        String fullName

) {
}