package com.gullak.wallet.service;

import com.gullak.wallet.dto.*;
import com.gullak.wallet.entity.*;
import com.gullak.wallet.repository.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;

import java.time.OffsetDateTime;
import java.util.UUID;

@ApplicationScoped
public class TransferService {

    @Inject
    AccountRepository accountRepository;

    @Inject
    TransferRepository transferRepository;

    @Transactional
    public TransferResponse transfer(TransferRequest request, AuthenticatedUser user){
        Long fromId = request.getFromAccountId();
        Long toId = request.getToAccountId();

        Account sender;
        Account receiver;

        if(fromId < toId){
            sender = accountRepository.findByIdForUpdate(fromId);
            receiver = accountRepository.findByIdForUpdate(toId);
        }
        else {
            receiver = accountRepository.findByIdForUpdate(toId);
            sender = accountRepository.findByIdForUpdate(fromId);
        }

        if (fromId.equals(toId)) {
            throw new WebApplicationException("Sender and receiver accounts cannot be the same", 400);
        }

        if(sender == null){
            throw new WebApplicationException("Sender account not found", 404);
        }
        if(receiver == null){
            throw new WebApplicationException("Receiver account not found", 404);
        }

        if(user.getRole().equals("customer") && !sender.getOwnerUid().equals(user.getUserId())){
            throw new WebApplicationException("You are not authorize to transfer from this account", 403);
        }

        if(sender.getBalancePaise() < request.getAmountPaise()){
            throw new WebApplicationException("Insufficient balance", 400);
        }
        long senderNewBalance = sender.getBalancePaise() - request.getAmountPaise();
        long receiverNewBalance = receiver.getBalancePaise() + request.getAmountPaise();

        sender.setBalancePaise(senderNewBalance);
        receiver.setBalancePaise(receiverNewBalance);



        Transfer transfer = new Transfer();

        transfer.setRequestId(UUID.randomUUID());
        transfer.setFromAccountId(sender.getId());
        transfer.setToAccountId(receiver.getId());
        transfer.setAmountPaise(request.getAmountPaise());
        transfer.setStatus("SUCCESS");
        OffsetDateTime now = OffsetDateTime.now();
        transfer.setCreatedAt(now);
        transfer.setUpdatedAt(now);

        transferRepository.persist(transfer);

        TransferResponse response = new TransferResponse();

        response.setTransferId(transfer.getId());
        response.setFromAccountId(transfer.getFromAccountId());
        response.setToAccountId(transfer.getToAccountId());
        response.setAmountPaise(transfer.getAmountPaise());
        response.setStatus(transfer.getStatus());
        response.setCreatedAt(transfer.getCreatedAt());

        return response;

    }



}
