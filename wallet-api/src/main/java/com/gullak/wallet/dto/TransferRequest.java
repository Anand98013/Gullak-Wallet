package com.gullak.wallet.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Setter
@Getter
public class TransferRequest {

    @NotNull(message = "Sender account ID is required")
    @Positive(message = "Sender account ID must be in positive")
    private Long fromAccountId;

    @NotNull(message = "Receiver account ID is required")
    @Positive(message = "Receiver account ID must be positive")
    private Long toAccountId;

    @NotNull(message = "Transfer amount is required")
    @Positive(message = "Transfer amount must be greater than zero")
    private Long amountPaise;
}

