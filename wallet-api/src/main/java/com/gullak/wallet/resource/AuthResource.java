package com.gullak.wallet.resource;

import com.gullak.wallet.dto.RegisterRequest;
import com.gullak.wallet.dto.RegisterResponse;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    @POST
    @Path("/register")
    public RegisterResponse register(@Valid RegisterRequest request) {

        RegisterResponse response = new RegisterResponse();

        response.setMessage("Registration endpoint reached. Logic pending.");
        response.setUsername(request.getUsername());

        return response;
    }
}