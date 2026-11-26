package com.gullak.wallet.resource;

import io.quarkus.security.Authenticated;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Authenticated
@Path("/secure")
@Produces(MediaType.TEXT_PLAIN)
public class SecureResource {

    @GET
    @Path("/test")
    public String test() {
        return "Access granted. JWT is valid.";
    }
}