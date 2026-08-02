package com.mps.auth.security;

import com.mps.auth.model.AppUser;
import com.mps.auth.model.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public record UserPrincipal(
        UUID userId,
        UUID organizationId,
        String email,
        String password,
        boolean enabled,
        Collection<? extends GrantedAuthority> authorities
) implements UserDetails {

    public static UserPrincipal from(AppUser user) {
        Set<GrantedAuthority> resolvedAuthorities =
                new LinkedHashSet<>();

        user.getRoles().forEach(role -> {
            resolvedAuthorities.add(
                    new SimpleGrantedAuthority(
                            "ROLE_" + role.getCode()
                    )
            );

            role.getPermissions().forEach(permission ->
                    resolvedAuthorities.add(
                            new SimpleGrantedAuthority(
                                    permission.getCode()
                            )
                    )
            );
        });

        return new UserPrincipal(
                user.getId(),
                user.getOrganization().getId(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getStatus() == UserStatus.ACTIVE,
                resolvedAuthorities
        );
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
