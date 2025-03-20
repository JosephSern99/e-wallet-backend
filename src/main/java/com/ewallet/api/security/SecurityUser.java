package com.ewallet.api.security;
import java.security.Principal;
import java.util.Set;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SecurityUser implements Principal {

    private final String id;
    private final String username;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final Set<String> roles;

    public SecurityUser(String id, String username, String email, String firstName, String lastName, Set<String> roles) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roles = roles;
    }

    @Override
    public String getName() {
        return username;
    }
}