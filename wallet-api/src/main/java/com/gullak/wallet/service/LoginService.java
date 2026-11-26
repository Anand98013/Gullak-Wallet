//package com.gullak.wallet.service;
//
//import com.gullak.wallet.dto.AuthenticatedUser;
//import jakarta.enterprise.context.ApplicationScoped;
//import org.eclipse.microprofile.config.inject.ConfigProperty;
//
//import javax.naming.Context;
//import javax.naming.NamingEnumeration;
//import javax.naming.directory.*;
//import java.util.Hashtable;
//
//@ApplicationScoped
//public class LoginService {
//
//    @ConfigProperty(name = "ldap.url")
//    String ldapUrl;
//
//    @ConfigProperty(name = "ldap.base-dn")
//    String baseDn;
//
//    @ConfigProperty(name = "ldap.people-ou")
//    String peopleOu;
//
//    @ConfigProperty(name = "ldap.admin-dn")
//    String adminDn;
//
//    @ConfigProperty(name = "ldap.admin-password")
//    String adminPassword;
//
//    public AuthenticatedUser authenticate(String username, String password) {
//
//        String userDn = "uid=" + username + "," + peopleOu + "," + baseDn;
//
//        // Step 1: Verify user's password with LDAP bind
//        Hashtable<String, String> userEnv = new Hashtable<>();
//
//        userEnv.put(Context.INITIAL_CONTEXT_FACTORY,
//                "com.sun.jndi.ldap.LdapCtxFactory");
//        userEnv.put(Context.PROVIDER_URL, ldapUrl);
//        userEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
//        userEnv.put(Context.SECURITY_PRINCIPAL, userDn);
//        userEnv.put(Context.SECURITY_CREDENTIALS, password);
//
//        try {
//            DirContext userContext = new InitialDirContext(userEnv);
//            userContext.close();
//
//        } catch (Exception e) {
//            return null; // Invalid credentials
//        }
//
//        // Step 2: Search entryUUID using LDAP admin bind
//        Hashtable<String, String> adminEnv = new Hashtable<>();
//
//        adminEnv.put(Context.INITIAL_CONTEXT_FACTORY,
//                "com.sun.jndi.ldap.LdapCtxFactory");
//        adminEnv.put(Context.PROVIDER_URL, ldapUrl);
//        adminEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
//        adminEnv.put(Context.SECURITY_PRINCIPAL, adminDn);
//        adminEnv.put(Context.SECURITY_CREDENTIALS, adminPassword);
//
//        try {
//            DirContext adminContext = new InitialDirContext(adminEnv);
//
//            String peopleDn = peopleOu + "," + baseDn;
//
//            SearchControls controls = new SearchControls();
//            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
//            controls.setReturningAttributes(new String[]{"entryUUID"});
//
//            NamingEnumeration<SearchResult> results =
//                    adminContext.search(
//                            peopleDn,
//                            "(uid=" + username + ")",
//                            controls
//                    );
//
//            if (results.hasMore()) {
//                SearchResult result = results.next();
//                String userId = result.getAttributes()
//                        .get("entryUUID").get().toString();
//
//                results.close();
//                adminContext.close();
//
//                return new AuthenticatedUser(userId, username);
//            }
//
//            results.close();
//            adminContext.close();
//            return null;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//}



package com.gullak.wallet.service;

import com.gullak.wallet.dto.AuthenticatedUser;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.*;
import java.util.Hashtable;

@ApplicationScoped
public class LoginService {

    @ConfigProperty(name = "ldap.url")
    String ldapUrl;

    @ConfigProperty(name = "ldap.base-dn")
    String baseDn;

    @ConfigProperty(name = "ldap.people-ou")
    String peopleOu;

    @ConfigProperty(name = "ldap.groups-ou")
    String groupsOu;

    @ConfigProperty(name = "ldap.admin-dn")
    String adminDn;

    @ConfigProperty(name = "ldap.admin-password")
    String adminPassword;

    public AuthenticatedUser authenticate(String username, String password) {

        String userDn = "uid=" + username + "," + peopleOu + "," + baseDn;

        // Step 1: Verify user's password with LDAP bind
        Hashtable<String, String> userEnv = new Hashtable<>();

        userEnv.put(Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.jndi.ldap.LdapCtxFactory");
        userEnv.put(Context.PROVIDER_URL, ldapUrl);
        userEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
        userEnv.put(Context.SECURITY_PRINCIPAL, userDn);
        userEnv.put(Context.SECURITY_CREDENTIALS, password);

        try {
            DirContext userContext = new InitialDirContext(userEnv);
            userContext.close();
        } catch (Exception e) {
            return null;
        }

        // Step 2: Connect using LDAP admin credentials
        Hashtable<String, String> adminEnv = new Hashtable<>();

        adminEnv.put(Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.jndi.ldap.LdapCtxFactory");
        adminEnv.put(Context.PROVIDER_URL, ldapUrl);
        adminEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
        adminEnv.put(Context.SECURITY_PRINCIPAL, adminDn);
        adminEnv.put(Context.SECURITY_CREDENTIALS, adminPassword);

        try {
            DirContext adminContext = new InitialDirContext(adminEnv);

            String peopleDn = peopleOu + "," + baseDn;

            // Step 3: Find user's entryUUID
            SearchControls userControls = new SearchControls();
            userControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            userControls.setReturningAttributes(new String[]{"entryUUID"});

            NamingEnumeration<SearchResult> userResults =
                    adminContext.search(
                            peopleDn,
                            "(uid=" + username + ")",
                            userControls
                    );

            if (!userResults.hasMore()) {
                userResults.close();
                adminContext.close();
                return null;
            }

            SearchResult userResult = userResults.next();
            String userId = userResult.getAttributes()
                    .get("entryUUID").get().toString();
            userResults.close();

            // Step 4: Find groups containing this user DN
            String groupBaseDn = groupsOu + "," + baseDn;

            SearchControls groupControls = new SearchControls();
            groupControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            groupControls.setReturningAttributes(new String[]{"cn"});

            NamingEnumeration<SearchResult> groupResults =
                    adminContext.search(
                            groupBaseDn,
                            "(member=" + userDn + ")",
                            groupControls
                    );

            String role = null;

            while (groupResults.hasMore()) {
                SearchResult groupResult = groupResults.next();
                String groupName = groupResult.getAttributes()
                        .get("cn").get().toString();

                if ("admin".equals(groupName)) {
                    role = "admin";
                } else if ("customer".equals(groupName) && role == null) {
                    role = "customer";
                }
            }

            groupResults.close();
            adminContext.close();

            // Step 5: Reject users without a recognized role
            if (role == null) {
                return null;
            }

            return new AuthenticatedUser(userId, username, role);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
