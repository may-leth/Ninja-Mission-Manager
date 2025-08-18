package com.konoha.NinjaMissionManager.security;

import com.konoha.NinjaMissionManager.models.Ninja;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
public class NinjaUserDetail implements UserDetails {
    private final Ninja ninja;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return ninja.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return ninja.getPassword();
    }

    @Override
    public String getUsername() {
        return ninja.getEmail();
    }
}
