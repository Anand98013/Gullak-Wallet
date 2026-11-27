package com.gullak.wallet.dto;

import lombok.*;
import java.time.OffsetDateTime;

@Getter
@Setter
public class TransferResponse {

    private Long transferId;
    private Long fromAccountId;
    private Long toAccountId;
    private Long amountPaise;
    private String status;
    private OffsetDateTime createdAt;
}
