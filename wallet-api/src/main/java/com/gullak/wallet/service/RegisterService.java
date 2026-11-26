package com.gullak.wallet.service;

import com.gullak.wallet.dto.RegisterRequest;
import com.gullak.wallet.dto.RegisterResponse;
import com.gullak.wallet.security.PasswordEncoder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.ldap.InitialLdapContext;

import java.util.Hashtable;

@ApplicationScoped
public class RegisterService {

    @Inject
    PasswordEncoder passwordEncoder;

    private static final Logger LOG = Logger.getLogger(RegisterService.class);

    @ConfigProperty(name = "ldap.url")
    String ldapUrl;

    @ConfigProperty(name = "ldap.base-dn")
    String baseDn;

    @ConfigProperty(name = "ldap.admin-dn")
    String adminDn;

    @ConfigProperty(name = "ldap.admin-password")
    String adminPassword;

    @ConfigProperty(name = "ldap.people-ou")
    String peopleOu;

    @ConfigProperty(name = "ldap.groups-ou")
    String groupsOu;

    public RegisterResponse register(RegisterRequest request) {

        DirContext ctx = null;
        NamingEnumeration<SearchResult> results = null;

        try {
            // 1. Connect to LDAP using admin credentials
            Hashtable<String, String> env = new Hashtable<>();

            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");

            env.put(Context.PROVIDER_URL, ldapUrl);
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.SECURITY_PRINCIPAL, adminDn);
            env.put(Context.SECURITY_CREDENTIALS, adminPassword);

            ctx = new InitialLdapContext(env, null);

            // 2. Define users location in LDAP
            String peopleDn = peopleOu + "," + baseDn;

            // 3. Check whether username already exists
            SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);

            results = ctx.search(peopleDn,
                    "(uid={0})",
                    new Object[]{request.getUsername()},
                    controls
            );

            if (results.hasMore()) {
                throw new WebApplicationException(
                        Response.status(Response.Status.CONFLICT)
                                .entity("{\"message\":\"Username already exists\"}")
                                .type(MediaType.APPLICATION_JSON)
                                .build()
                );
            }

            // 4. Validate username before using it in LDAP DN
            if (!request.getUsername().matches("^[a-zA-Z0-9_.]+$")) {
                throw new WebApplicationException(
                        "Invalid username",
                        Response.Status.BAD_REQUEST
                );
            }

            // 5. Create user's LDAP Distinguished Name
            String userDn = "uid=" + request.getUsername() + "," + peopleDn;

            // 6. Prepare LDAP attributes
            Attributes attributes = new BasicAttributes();

            Attribute objectClass = new BasicAttribute("objectClass");
            objectClass.add("top");
            objectClass.add("person");
            objectClass.add("organizationalPerson");
            objectClass.add("inetOrgPerson");

            attributes.put(objectClass);
            attributes.put("uid", request.getUsername());
            attributes.put("cn", request.getFullName());
            attributes.put("sn", surname(request.getFullName()));
            attributes.put("mail", request.getEmail());

            // 7. Hash password using PasswordEncoder
            String hashedPassword = passwordEncoder.hashPassword(request.getPassword());

            attributes.put("userPassword", hashedPassword);

            // 8. Create user entry in LDAP
            ctx.createSubcontext(userDn, attributes);

            // Assign user to customer group
            addToCustomerGroup(ctx, userDn);

            // 9. Return successful registration response
            RegisterResponse response = new RegisterResponse();
            response.setUsername(request.getUsername());
            response.setMessage("Registration successful");

            return response;

        } catch (WebApplicationException e) {
            throw e;

        } catch (NamingException e) {
            LOG.error("LDAP registration failed", e);
            throw new WebApplicationException(
                    "LDAP registration failed",
                    Response.Status.INTERNAL_SERVER_ERROR
            );

        } catch (Exception e) {
            LOG.error("Registration failed", e);
            throw new WebApplicationException(
                    "Registration failed",
                    Response.Status.INTERNAL_SERVER_ERROR
            );

        } finally {
            // 10. Close LDAP search and connection
            if (results != null) {
                try {
                    results.close();
                } catch (NamingException ignored) {
                }
            }

            if (ctx != null) {
                try {
                    ctx.close();
                } catch (NamingException ignored) {
                }
            }
        }
    }


    private void addToCustomerGroup(DirContext ctx, String userDn)
            throws NamingException {

        // LDAP customer group location
        String groupDn = "cn=customer," + groupsOu + "," + baseDn;

        ModificationItem[] modifications = new ModificationItem[1];

        modifications[0] = new ModificationItem(
                DirContext.ADD_ATTRIBUTE,
                new BasicAttribute("member", userDn)
        );

        ctx.modifyAttributes(groupDn, modifications);
    }


    // Extract surname for LDAP's required sn attribute
    private String surname(String fullName) {
        String[] parts = fullName.trim().split("\\s+");
        return parts[parts.length - 1];
    }
}
