
package com.gullak.wallet.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.Priorities;
import jakarta.annotation.Priority;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.Principal;
import java.util.Base64;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class JwtTokenValidator implements ContainerRequestFilter {

    private static final String SECRET = System.getenv("JWT_SECRET");

    @Inject
    ObjectMapper objectMapper;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        System.out.println("JWT FILTER RUNNING: "
                + requestContext.getUriInfo().getPath());

        String path = requestContext.getUriInfo()
                .getPath();

        path = path.replaceFirst("^/", "");

        if (!path.equals("secure") && !path.startsWith("secure/")) {
            return;
        }

        String authHeader = requestContext.getHeaderString("Authorization");
        System.out.println("AUTH HEADER PRESENT: " + (authHeader != null));

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            abort(requestContext);
            return;
        }

        String token = authHeader.substring(7);

        try {
            boolean valid = validateToken(token);

            System.out.println("JWT VALIDATION RESULT: " + valid);

            if (valid) {
                String[] parts = token.split("\\.");

                byte[] payloadBytes = Base64.getUrlDecoder()
                        .decode(parts[1]);

                JsonNode payload = objectMapper.readTree(payloadBytes);
                String username = payload.get("sub").asText();

                SecurityContext oldContext = requestContext.getSecurityContext();

                requestContext.setSecurityContext(
                        new SecurityContext() {
                            @Override
                            public Principal getUserPrincipal() {
                                return () -> username;
                            }

                            @Override
                            public boolean isUserInRole(String role) {
                                return false;
                            }

                            @Override
                            public boolean isSecure() {
                                return oldContext.isSecure();
                            }

                            @Override
                            public String getAuthenticationScheme() {
                                return "Bearer";
                            }
                        }
                );

                System.out.println("JWT AUTHENTICATED USER: " + username);

            } else {
                System.out.println("JWT FAILED VALIDATION");
                abort(requestContext);
            }

        } catch (Exception e) {
            System.out.println("JWT ERROR: " + e.getMessage());
            e.printStackTrace();
            abort(requestContext);
        }
    }

    private boolean validateToken(String token) throws Exception {

        if (SECRET == null || SECRET.length() < 32) {
            return false;
        }

        String[] parts = token.split("\\.");

        if (parts.length != 3) {
            return false;
        }

        String headerJson = new String(
                Base64.getUrlDecoder().decode(parts[0]),
                StandardCharsets.UTF_8
        );

        JsonNode header = objectMapper.readTree(headerJson);

        if (!"HS256".equals(header.path("alg").asText())) {
            return false;
        }

        String data = parts[0] + "." + parts[1];

        String expectedSignature = sign(data);

        byte[] expected = expectedSignature.getBytes(StandardCharsets.UTF_8);
        byte[] actual = parts[2].getBytes(StandardCharsets.UTF_8);

        if (!MessageDigest.isEqual(expected, actual)) {
            return false;
        }

        JsonNode payload = objectMapper.readTree(
                Base64.getUrlDecoder().decode(parts[1])
        );

        if (!"gullak-wallet".equals(payload.path("iss").asText())) {
            return false;
        }

        long expiration = payload.path("exp").asLong(0);
        long now = System.currentTimeMillis() / 1000;

        return expiration > now
                && payload.hasNonNull("sub");
    }

    private String sign(String data) throws Exception {

        Mac mac = Mac.getInstance("HmacSHA256");

        SecretKeySpec key = new SecretKeySpec(
                SECRET.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );

        mac.init(key);

        byte[] signature = mac.doFinal(
                data.getBytes(StandardCharsets.UTF_8)
        );

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(signature);
    }

    private void abort(ContainerRequestContext context) {
        context.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .entity("Invalid or expired token")
                        .build()
        );
    }
}
