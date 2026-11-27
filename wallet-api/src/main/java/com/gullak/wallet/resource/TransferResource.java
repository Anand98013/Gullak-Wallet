package com.gullak.wallet.resource;

import com.gullak.wallet.dto.*;
import com.gullak.wallet.service.TransferService;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

@Path("/transfers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TransferResource {

    @Inject
    TransferService transferService;

    @Inject
    SecurityIdentity securityIdentity;

    @POST
    @RolesAllowed("customer")
    public Response transfer(@Valid TransferRequest request) {

        String userId = securityIdentity.getPrincipal().getName();

        AuthenticatedUser user = new AuthenticatedUser(
                userId,
                userId,
                "customer"
        );

        TransferResponse response =
                transferService.transfer(request, user);

        return Response.ok(response).build();
    }
}