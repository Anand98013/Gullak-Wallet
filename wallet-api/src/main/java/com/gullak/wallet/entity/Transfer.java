package com.gullak.wallet.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "requestId", nullable = false, unique = true)
    private UUID requestId;

    @Column(name = "fromAccountId", nullable = false)
    private String fromAccountId;

    @Column(name = "toAccountId", nullable = false)
    private String toAccountId;

    @Column(name = "amount_paise", nullable = false)
    private Long amountPaise;

    @Column(nullable = false)
    private String status;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "reverse_by")
    private Long reversedBy;

    @Column(name = "reversal_of")
    private Long reversalOf;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

}
