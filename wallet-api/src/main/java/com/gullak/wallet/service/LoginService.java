package com.gullak.wallet.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

@ApplicationScoped
public class LoginService {

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

    public boolean authenticate(String username, String password) {

        String userDn = "uid=" + username + "," + peopleOu + "," + baseDn;

        Hashtable<String, String> env = new Hashtable<>();

        env.put(Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.jndi.ldap.LdapCtxFactory");

        env.put(Context.PROVIDER_URL, ldapUrl);

        env.put(Context.SECURITY_AUTHENTICATION, "simple");

        env.put(Context.SECURITY_PRINCIPAL, userDn);

        env.put(Context.SECURITY_CREDENTIALS, password);

        try {
            DirContext context = new InitialDirContext(env);
            context.close();
            return true;

        } catch (Exception e) {
            return false;
        }
    }
}