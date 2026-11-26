package com.gullak.wallet.service;

import com.gullak.wallet.dto.AccountResponse;
import com.gullak.wallet.dto.CreateAccountRequest;
import com.gullak.wallet.entity.Account;
import com.gullak.wallet.repository.AccountRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;

@ApplicationScoped
public class AccountService {

    @Inject
    AccountRepository accountRepository;

    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request, String ownerUid) {

        Account account = new Account();

        account.setName(request.getName());
        account.setBalancePaise(request.getStartingBalance());
        account.setOwnerUid(ownerUid);
        account.setCreatedAt(LocalDateTime.now());

        accountRepository.persist(account);

        AccountResponse response = new AccountResponse();
        response.setAccountId(account.getId());
        response.setName(account.getName());
        response.setBalancePaise(account.getBalancePaise());
        response.setOwnerUid(account.getOwnerUid());

        return response;
    }


    public AccountResponse getAccountById(Long accountId, String requesterUid, boolean isAdmin) {

        Account account = accountRepository.findById(accountId);

        if (account == null) {
            return null;

        }
        if (!isAdmin && !account.getOwnerUid().equals(requesterUid)) {
            throw new jakarta.ws.rs.ForbiddenException("You are not allowed to access this account");
        }

        AccountResponse response = new AccountResponse();
        response.setAccountId(account.getId());
        response.setName(account.getName());
        response.setBalancePaise(account.getBalancePaise());
        response.setOwnerUid(account.getOwnerUid());

        return response;
    }
}