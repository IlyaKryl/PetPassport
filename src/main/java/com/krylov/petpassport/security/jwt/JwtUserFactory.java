package com.krylov.petpassport.security.jwt;

import com.krylov.petpassport.model.Role;
import com.krylov.petpassport.model.Status;
import com.krylov.petpassport.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public final class JwtUserFactory {

    public JwtUserFactory() {
    }

    public static JwtUser create(User user) {
        return new JwtUser(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getFirstName(),
                user.getLastName(),
                user.getStatus().equals(Status.ACTIVE),
                user.getUpdated(),
                mapToGrantedAuthority(new HashSet<>(user.getRoles()))
        );
    }

    private static Set<GrantedAuthority> mapToGrantedAuthority(Set<Role> userRoles) {
        return userRoles.stream()
                .map(role ->
                        new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toSet());
    }
}
