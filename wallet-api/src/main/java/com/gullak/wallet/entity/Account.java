package com.gullak.wallet.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Table(name = "account")
@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "balance_paise", nullable = false)
    private Long balancePaise;

    @Column(name = "owner_uid", nullable = false)
    private String ownerUid;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
