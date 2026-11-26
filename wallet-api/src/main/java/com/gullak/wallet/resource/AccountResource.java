package com.gullak.wallet.resource;

import com.gullak.wallet.dto.AccountResponse;
import com.gullak.wallet.dto.CreateAccountRequest;
import com.gullak.wallet.service.AccountService;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/accounts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
public class AccountResource {

    @Inject
    AccountService accountService;

    @Inject
    SecurityIdentity securityIdentity;

    @POST
    @RolesAllowed("customer")
    public Response createAccount(@Valid CreateAccountRequest request) {

        String ownerUid = securityIdentity.getPrincipal().getName();

        AccountResponse response =
                accountService.createAccount(request, ownerUid);

        return Response.status(Response.Status.CREATED)
                .entity(response)
                .build();
    }

    @GET
    @Path("/{accountId}")
    @RolesAllowed({"customer", "admin"})
    public Response getAccountById(@PathParam("accountId") Long accountId) {

        String requesterUid = securityIdentity.getPrincipal().getName();

        boolean isAdmin = securityIdentity.hasRole("admin");

        AccountResponse response = accountService.getAccountById(
                accountId, requesterUid, isAdmin
        );

        if (response == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(response).build();
    }
}