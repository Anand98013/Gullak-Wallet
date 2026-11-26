package com.gullak.wallet.repository;

import com.gullak.wallet.entity.Transfer;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class TransferRepository implements PanacheRepository<Transfer> {

    public Transfer findByRequestId(UUID requestId){
        return find("requestId", requestId).firstResult();
    }
}

