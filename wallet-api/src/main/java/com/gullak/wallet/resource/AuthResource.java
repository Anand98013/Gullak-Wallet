package com.gullak.wallet.resource;

import com.gullak.wallet.dto.LoginRequest;
import com.gullak.wallet.dto.LoginResponse;
import com.gullak.wallet.dto.RegisterRequest;
import com.gullak.wallet.dto.RegisterResponse;
import com.gullak.wallet.service.AuthService;
import com.gullak.wallet.security.JwtService;
import com.gullak.wallet.service.LoginService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    AuthService authService;

    @Inject
    LoginService loginService;

    @Inject
    JwtService jwtService;

    @POST
    @Path("/register")
    public RegisterResponse register(@Valid RegisterRequest request) {
        return authService.register(request);
    }


    @POST
    @Path("/login")
    public Response login(@Valid LoginRequest request){
        boolean authenticated = loginService.authenticate(
                request.getUsername(),
                request.getPassword()
        );

        if(!authenticated){
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Invalid Username and Password")
                    .build();
        }

        String token = jwtService.generateToken(request.getUsername());

        LoginResponse response = new LoginResponse();
        response.setMessage("Login successful");
        response.setUsername(request.getUsername());
        response.setToken(token);
        response.setTokenType("Bearer");
        response.setExpiresIn(3600);

        return Response.ok(response).build();
    }

}
