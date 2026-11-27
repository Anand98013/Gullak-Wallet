package com.gullak.wallet.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Getter
@Setter
public class CreateAccountRequest {

    @NotNull(message = "Name is required")
    private String name;

    @NotNull(message = "Starting balance is required")
    @PositiveOrZero(message = "Starting balance cannot be negative")
    private Long startingBalance;

}
