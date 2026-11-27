package com.gullak.wallet.repository;

import com.gullak.wallet.entity.Account;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.LockModeType;

@ApplicationScoped
public class AccountRepository implements PanacheRepository<Account> {

    public Account findByIdForUpdate(Long id) {
        return find("id", id)
                .withLock(LockModeType.PESSIMISTIC_WRITE)
                .firstResult();
    }
}
