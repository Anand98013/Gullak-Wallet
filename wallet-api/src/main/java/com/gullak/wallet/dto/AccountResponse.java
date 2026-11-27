package com.gullak.wallet.dto;
import lombok.*;

@Getter
@Setter
public class AccountResponse {

    private Long accountId;
    private String name;
    private Long balancePaise;
    private String ownerUid;
}
